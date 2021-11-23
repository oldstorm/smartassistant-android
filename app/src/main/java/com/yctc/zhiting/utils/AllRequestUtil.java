package com.yctc.zhiting.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CertificateDialog;
import com.app.main.framework.entity.ChannelEntity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.HttpResponseHandler;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.SSLSocketClient;
import com.app.main.framework.httputil.TempChannelUtil;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.httputil.cookie.CookieJarImpl;
import com.app.main.framework.httputil.cookie.PersistentCookieStore;
import com.app.main.framework.httputil.log.LoggerInterceptor;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.application.Application;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.entity.AreaIdBean;
import com.yctc.zhiting.entity.home.AccessTokenBean;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.UpdateSaUserNameEvent;
import com.yctc.zhiting.request.AddHCRequest;
import com.yctc.zhiting.request.BindCloudRequest;
import com.yctc.zhiting.request.BindCloudStrRequest;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLPeerUnverifiedException;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;

public class AllRequestUtil {

    static WeakReference<Context> mContext = new WeakReference<>(Application.getContext());
    static DBManager dbManager = DBManager.getInstance(mContext.get());
    private static boolean hasDialog;
    public static String nickName = "";
    /**
     * 获取云端家庭数据
     */
    public static void getCloudArea() {
        if (!UserUtils.isLogin()) return;
        HTTPCaller.getInstance().get(HomeCompanyListBean.class, HttpUrlConfig.getSCAreasUrl() + Constant.ONLY_SC,
                new RequestDataCallback<HomeCompanyListBean>() {
                    @Override
                    public void onSuccess(HomeCompanyListBean obj) {
                        super.onSuccess(obj);
                        if (obj != null) {
                            List<HomeCompanyBean> areas = obj.getAreas();
                            if (CollectionUtil.isNotEmpty(areas)) {// 云端家庭数据不为空，则同步
                                getCloudAreaSuccess(areas);
                            } else {// 否则创建一个云端家庭
                                UiUtil.starThread(() -> {
                                    List<HomeCompanyBean> homeList = dbManager.queryLocalHomeCompanyList();
                                    if (CollectionUtil.isEmpty(homeList)) {//本地数据库是否有默认家庭，否则创建家庭
                                        insertDefaultHome();
                                    }
                                    sysAreaCloud(true);
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        Log.e("getCloudArea", errorMessage);
                    }
                });
    }

    /**
     * 插入默认 我的家庭
     */
    private static void insertDefaultHome() {
        HomeCompanyBean homeCompanyBean = new HomeCompanyBean(UiUtil.getString(R.string.main_my_home));
        dbManager.insertHomeCompany(homeCompanyBean, null, false);
    }

    /**
     * 成功获取云端数据
     *
     * @param areas
     */
    private static void getCloudAreaSuccess(List<HomeCompanyBean> areas) {
        UiUtil.starThread(() -> {
            int cloudUserId = UserUtils.getCloudUserId();
            dbManager.removeFamilyNotPresentUserFamily(cloudUserId);
            for (HomeCompanyBean homeBean : areas) {
                homeBean.setCloud_user_id(cloudUserId);
            }
            List<HomeCompanyBean> userHomeCompanyList = dbManager.queryHomeCompanyListByCloudUserId(cloudUserId);
            List<Long> cloudIdList = new ArrayList<>();
            for (HomeCompanyBean hcb : userHomeCompanyList) {
                cloudIdList.add(hcb.getId());
            }
            for (HomeCompanyBean area : areas) {
                if (cloudIdList.contains(area.getId())) {  // 如果家庭已存在，则更新
                    dbManager.updateHomeCompanyByCloudId(area.getId(), area.getName());
                } else {//不存在，插入
                    if (area.isIs_bind_sa()){
                        area.setArea_id(area.getId());
                    }
                    dbManager.insertCloudHomeCompany(area);
                }
            }
            sysAreaCloud(false);
            EventBus.getDefault().post(new RefreshHome());
        });
    }

    /**
     * 本地数据同步到云端
     */
    public static void sysAreaCloud(boolean updateScName) {
        List<HomeCompanyBean> homeCompanyList = dbManager.queryLocalHomeCompanyList();
        if (CollectionUtil.isNotEmpty(homeCompanyList)) {
            for (int i = 0; i < homeCompanyList.size(); i++) {
                HomeCompanyBean homeBean = homeCompanyList.get(i);
                if (homeBean.isIs_bind_sa() && homeBean.getId()!=homeBean.getArea_id()){  // 如果绑了sa
//                    bindCloudWithoutCreateHome(homeBean, null);
                }else {

                    boolean isLast = (i == homeCompanyList.size() - 1);
                    List<LocationBean> list = dbManager.queryLocations(homeBean.getLocalId());
                    List<String> locationNames = new ArrayList<>();
                    if (CollectionUtil.isNotEmpty(list)) {
                        for (LocationBean locationBean : list) {
                            locationNames.add(locationBean.getName());
                        }
                    }
                    AddHCRequest addHCRequest = new AddHCRequest(homeBean.getName(), locationNames);
                    homeBean.setCloud_user_id(UserUtils.getCloudUserId());
                    HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreas() + Constant.ONLY_SC, addHCRequest,
                            new RequestDataCallback<IdBean>() {
                                @Override
                                public void onSuccess(IdBean obj) {
                                    super.onSuccess(obj);
                                    Log.e("sysAreaCloud-success", "success=homeId=" + obj.getId());
                                    IdBean.CloudSaUserInfo cloudSaUserInfo = obj.getCloud_sa_user_info();
                                    int userId = homeBean.getUser_id();
                                    String saToken = homeBean.getSa_user_token();
                                    if (cloudSaUserInfo != null && !homeBean.isIs_bind_sa()) {
                                        userId = cloudSaUserInfo.getId();
                                        saToken = cloudSaUserInfo.getToken();
                                    }
                                    updateArea(homeBean.getLocalId(), obj.getId(), userId, saToken, isLast);
                                }

                                @Override
                                public void onFailed(int errorCode, String errorMessage) {
                                    super.onFailed(errorCode, errorMessage);
                                    Log.e("sysAreaCloud-fail", errorMessage);
                                }
                            });
                }
                if (i==homeCompanyList.size()-1){
                    if (updateScName){
                        updateSCUserName();
                        UserUtils.setCloudUserName(nickName);
                    }else {
                        EventBus.getDefault().post(new MineUserInfoEvent(true));
                    }
                }
            }
        }
    }

    /**
     * 修改sc昵称
     */
    public static void updateSCUserName(){
        UpdateUserPost updateUserPost = new UpdateUserPost();
        updateUserPost.setNickname(nickName);
        String body = new Gson().toJson(updateUserPost);
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getSCUsersWithoutHeader() + "/" + UserUtils.getCloudUserId() + Constant.ONLY_SC, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                LogUtil.e("updateSCUserName=====成功");
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                LogUtil.e("updateSCUserName=====失败");
            }
        });
    }

    /**
     * 更新本地家庭cloud_id和cloud_user_id
     *
     * @param localId 本地家庭
     * @param cloudId 云端id
     */
    public static void updateArea(long localId, long cloudId, int saUserId, String saToken, boolean isLast) {
        dbManager.updateHomeCompanyCloudId(localId, cloudId, UserUtils.getCloudUserId(), saUserId, saToken);
        //数据全部同步完，检查是否有绑定sa数据
        if (isLast) {
            List<HomeCompanyBean> homeCompanyList = dbManager.queryHomeCompanyList();
            LogUtil.e("updateArea1=" + GsonConverter.getGson().toJson(homeCompanyList));
            for (HomeCompanyBean homeBean : homeCompanyList) {
                System.out.println("绑定云：第三次");
                bindCloudWithoutCreateHome(homeBean, null);
            }
        }
    }

    /**
     * 判断是否再SA环境
     *
     * @param homeBean
     * @return
     */
    public static boolean isSAEnvironment(HomeCompanyBean homeBean) {
        if (homeBean.isIs_bind_sa() && Constant.wifiInfo != null && homeBean.getMac_address() != null
                && homeBean.getMac_address().equalsIgnoreCase(Constant.wifiInfo.getBSSID())) {
            return true;
        }
        return false;
    }

    /**
     * 创建并绑定家庭云
     */
    public static void createHomeBindSC(HomeCompanyBean homeBean, ChannelEntity channelEntity) {
        //云端id=0表示没有绑定云
        if (UserUtils.isLogin()) {
            if (homeBean.getId()==0 && homeBean.isIs_bind_sa()) {
                SynPost.AreaBean areaBean = new SynPost.AreaBean(homeBean.getName(), new ArrayList<>());//家庭
                HTTPCaller.getInstance().post(IdBean.class,  HttpUrlConfig.getScAreas()+Constant.ONLY_SC, areaBean,
                        new RequestDataCallback<IdBean>() {
                            @Override
                            public void onSuccess(IdBean data) {
                                super.onSuccess(data);
                                LogUtil.e("sysAreaCloud-success", "success");
                                homeBean.setId(data.getId());
                                Constant.CurrentHome = homeBean;
                                int userId =  Constant.CurrentHome.getUser_id();
                                String token =  Constant.CurrentHome.getSa_user_token();
                                IdBean.CloudSaUserInfo cloudSaUserInfo = data.getCloud_sa_user_info();
                                if (cloudSaUserInfo!=null && ! Constant.CurrentHome.isIs_bind_sa()){
                                    userId = cloudSaUserInfo.getId();
                                    token = cloudSaUserInfo.getToken();
                                }
                                dbManager.updateHomeCompanyCloudId(homeBean.getLocalId(), homeBean.getId(), UserUtils.getCloudUserId(), userId, token);
                                bindCloud(homeBean, channelEntity);
                            }

                            @Override
                            public void onFailed(int errorCode, String errorMessage) {
                                super.onFailed(errorCode, errorMessage);
                                LogUtil.e("sysAreaCloud-fail", errorMessage);
                            }
                        });
            } else {
                bindCloud(homeBean,channelEntity);
            }
        }
    }

    /**
     * 绑定云家庭
     *
     * @param homeBean 家庭对象
     */
    public static void bindCloud(HomeCompanyBean homeBean, ChannelEntity channelEntity) {
        //如果再sa环境，如果未绑定云端，则绑定云端；否则不绑定
//        if (isSAEnvironment(homeBean)) {
        if (homeBean.isIs_bind_sa() && homeBean.getId()>0) {
            LogUtil.e("updateArea2=" + GsonConverter.getGson().toJson(homeBean));
            Constant.CurrentHome.setId(homeBean.getId());
            BindCloudStrRequest request = new BindCloudStrRequest();
//            request.setCloud_area_id(String.valueOf(homeBean.getId()));
            request.setCloud_user_id(homeBean.getCloud_user_id());

            HttpConfig.addHeader(homeBean.getSa_user_token());
            HTTPCaller.getInstance().post(AreaIdBean.class, channelEntity!=null ? Constant.HTTPS_HEAD + channelEntity.getHost() + HttpUrlConfig.API + HttpUrlParams.cloud_bind  : HttpUrlConfig.getBindCloud(), request,
                    new RequestDataCallback<AreaIdBean>() {
                        @Override
                        public void onSuccess(AreaIdBean obj) {
                            super.onSuccess(obj);
                            if (obj!=null) {
                                Constant.CurrentHome.setId(obj.getArea_id());  // 重新设置云id值
                                dbManager.updateHCAreaId(homeBean.getLocalId(), obj.getArea_id(), channelEntity==null);  // 绑定云端成功之后，修改本地云id值
                            }
                            EventBus.getDefault().post(new UpdateSaUserNameEvent());
                            Log.e("updateArea-bind", "success");
                        }

                        @Override
                        public void onFailed(int errorCode, String errorMessage) {
                            super.onFailed(errorCode, errorMessage);
                            Log.e("updateArea-fail", errorMessage);
                        }
                    });

        }
    }

    /**
     * 绑定云端家庭（不用在云端创建家庭）
     * @param homeCompanyBean
     * @param channelEntity
     */
    public static void bindCloudWithoutCreateHome(HomeCompanyBean homeCompanyBean, ChannelEntity channelEntity){
        if (homeCompanyBean.isIs_bind_sa() && homeCompanyBean.getId() != homeCompanyBean.getArea_id() && UserUtils.isLogin()){
            checkUrl500(homeCompanyBean.getSa_lan_address(), new onCheckUrlListener() {
                @Override
                public void onSuccess() {
                    getAccessToken(homeCompanyBean, null);
                }

                @Override
                public void onError() {
                    if (channelEntity!=null){
                        getAccessToken(homeCompanyBean, channelEntity);
                    }else {
                        List<Header> headers = new ArrayList<>();
                        headers.add(new Header(HttpConfig.SA_ID, homeCompanyBean.getSa_id()));
                        List<NameValuePair> requestData = new ArrayList<>();
                        requestData.add(new NameValuePair("scheme", Constant.HTTPS));
                        String url = TempChannelUtil.baseSCUrl + "/datatunnel";

                        HTTPCaller.getInstance().getChannel(ChannelEntity.class, url, headers.toArray(new Header[headers.size()]), requestData,
                                new RequestDataCallback<ChannelEntity>() {
                                    @Override
                                    public void onSuccess(ChannelEntity obj) {  // 获取临时通道成功
                                        super.onSuccess(obj);
                                        if (obj != null) {
                                            getAccessToken(homeCompanyBean, obj);
                                        }
                                    }

                                    @Override
                                    public void onFailed(int errorCode, String errorMessage) {  // 获取临时通道失败
                                        super.onFailed(errorCode, errorMessage);
                                        Log.e("CaptureNewActivity=", "checkTemporaryUrl=onFailed");
                                    }
                                }, false);
                    }
                }
            });
        }
    }

    /**
     * 获取设备access_token
     * @param homeCompanyBean
     * @param channelEntity
     */
    public static void getAccessToken(HomeCompanyBean homeCompanyBean, ChannelEntity channelEntity){
        HTTPCaller.getInstance().post(AccessTokenBean.class, HttpUrlConfig.getDeviceAccessToken(), "", new RequestDataCallback<AccessTokenBean>() {
            @Override
            public void onSuccess(AccessTokenBean obj) {
                super.onSuccess(obj);
                if (obj!=null){
                    LogUtil.e("getAccessToken==============成功");
                    BindCloudStrRequest request = new BindCloudStrRequest();
                    request.setAccess_token(obj.getAccess_token());
                    request.setCloud_user_id(UserUtils.getCloudUserId());
                    HttpConfig.addHeader(HttpConfig.SA_ID, homeCompanyBean.getSa_id());
                    HttpConfig.addHeader(HttpConfig.TOKEN_KEY, homeCompanyBean.getSa_user_token());
                    HTTPCaller.getInstance().post(AreaIdBean.class, channelEntity!=null ? Constant.HTTPS_HEAD + channelEntity.getHost() + HttpUrlConfig.API + HttpUrlParams.cloud_bind  : HttpUrlConfig.getBindCloud(), request,
                            new RequestDataCallback<AreaIdBean>() {
                                @Override
                                public void onSuccess(AreaIdBean obj) {
                                    super.onSuccess(obj);
                                    if (obj!=null) {
                                        Constant.CurrentHome.setId(obj.getArea_id());  // 重新设置云id值
                                        dbManager.updateHCAreaId(homeCompanyBean.getLocalId(), obj.getArea_id(), channelEntity==null);  // 绑定云端成功之后，修改本地云id值
                                    }
                                    EventBus.getDefault().post(new UpdateSaUserNameEvent());
                                    LogUtil.e("updateArea-bind====success");
                                }

                                @Override
                                public void onFailed(int errorCode, String errorMessage) {
                                    super.onFailed(errorCode, errorMessage);
                                    LogUtil.e("updateArea-fail====="+errorMessage);
                                }
                            });
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                LogUtil.e("getAccessToken==============失败："+errorMessage);
            }
        });
    }



    /**
     * 检测接口
     *
     * @param url 家庭对象
     */
    public static void checkUrl(String url, onCheckUrlListener listener) {
        if (url != null && !TextUtils.isEmpty(url)) {
            if (!url.startsWith("http"))
                url = "http://" + url;
            url = url + "/api/check";
        }
        HTTPCaller.getInstance().post(String.class, url, "",
                new RequestDataCallback<String>() {
                    @Override
                    public void onSuccess(String obj) {
                        super.onSuccess(obj);
                        if (listener != null) listener.onSuccess();
                    }

                    @Override
                    public void onFailed(int errorCode, String errorMessage) {
                        super.onFailed(errorCode, errorMessage);
                        if (listener != null) listener.onError();
                    }
                });
    }

    public static void checkUrl500(String url, onCheckUrlListener listener){
        if (url != null && !TextUtils.isEmpty(url)) {
            if (!url.startsWith("http"))
                url = "http://" + url;
            url = url + "/api/check";
        }
        String finalUrl = url;
        HTTPCaller.getInstance().postBuilder(url, new Header[]{}, "", getClient(),
                new HttpResponseHandler() {
                    @Override
                    public void onSuccess(int status, Header[] headers, byte[] responseBody) {
                        String str = null;
                        try {
                            str = new String(responseBody, StandardCharsets.UTF_8);
                            LogUtil.e(finalUrl + " " + status + " " + str);
                            String data;
                            if (str.contains("\"data\"")) {
                                JSONObject jsonObject = new JSONObject(str);
                                data = jsonObject.getString("data");
                                LogUtil.e("HTTPCaller1=" + data);
                            }
                            if (listener != null) listener.onSuccess();
                            LogUtil.e("HTTPCaller2=" + str);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (listener != null) listener.onError();
                        }
                    }

                    @Override
                    public void onFailure(int status, byte[] data) {
                        super.onFailure(status, data);
                        String datas = null;
                        try {
                            datas = new String(data, StandardCharsets.UTF_8);
                            LogUtil.e(finalUrl + " " + status + " " + datas);
                            System.out.println("结果：" + datas);
                            String dataStr = "";
                            String result="";
                            if (status != -1) {
                                JSONObject jsonObject = new JSONObject(datas);
                                dataStr = jsonObject.getString("data");
                            }
                            if (listener != null) listener.onError();
                            LogUtil.e("HTTPCaller1=" + dataStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (listener != null) listener.onError();
                        }

                    }
                });
    }

    public static OkHttpClient getClient(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .writeTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("ZhiTing", true))
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .hostnameVerifier((hostname, session) -> {
                    if (hostname.equals(HTTPCaller.CLOUD_HOST_NAME)) {  // SC直接访问
                        return true;
                    } else {
                        if (session != null) {
                            String cersCacheJson = SpUtil.get(hostname); // 根据主机名获取本地存储的数据
                            LogUtil.e("缓存证书："+cersCacheJson);
                            try {
                                Certificate[] certificates = session.getPeerCertificates();  // 证书
                                String cersJson = HTTPCaller.byte2Base64String(certificates[0].getEncoded());  // 把证书转为base64存储
                                if (!TextUtils.isEmpty(cersCacheJson)) {  // 如果之前存储过
                                    LogUtil.e("服务证书："+cersJson);
                                    String ccj = new String(cersCacheJson.getBytes(), "UTF-8");
                                    String cj = new String(cersJson.getBytes(), "UTF-8");
                                    boolean cer = cj.equals(ccj);
                                    if (cer) {  // 之前存储过的证书和当前证书一样，直接访问
                                        return true;
                                    } else {// 之前存储过的证书和当前证书不一样，重新授权
                                        showAlertDialog(LibLoader.getCurrentActivity().getString(com.app.main.framework.R.string.whether_trust_this_certificate_again), hostname, cersJson);
                                        return false;
                                    }
                                } else {// 之前没存储过，询问是否信任证书
                                    showAlertDialog(LibLoader.getCurrentActivity().getString(com.app.main.framework.R.string.whether_trust_this_certificate), hostname, cersJson);
                                    return false;
                                }
                            } catch (SSLPeerUnverifiedException e) {
                                e.printStackTrace();
                            } catch (CertificateEncodingException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                })
                .cookieJar(new CookieJarImpl(PersistentCookieStore.getInstance()))
                .build();
        return client;
    }

    private static void showAlertDialog(String tips, String hostName, String cerCache) {
        FragmentActivity activity = (FragmentActivity) LibLoader.getCurrentActivity();
        if (activity != null) {
            if (!hasDialog) {
                hasDialog = true;
                CertificateDialog certificateDialog = CertificateDialog.newInstance(tips);
                certificateDialog.setConfirmListener(() -> {
                    SpUtil.put(hostName, cerCache);
                    certificateDialog.dismiss();
                    hasDialog = false;
                });
                certificateDialog.show(activity);
            }
        }
    }

    public interface onCheckUrlListener {
        void onSuccess();

        void onError();
    }
}
