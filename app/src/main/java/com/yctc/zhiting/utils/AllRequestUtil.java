package com.yctc.zhiting.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;

import com.app.main.framework.baseutil.LibLoader;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.config.HttpBaseUrl;
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
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.RefreshHomeEvent;
import com.yctc.zhiting.event.UpdateSaUserNameEvent;
import com.yctc.zhiting.request.AddHCRequest;
import com.yctc.zhiting.request.BindCloudStrRequest;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLPeerUnverifiedException;

import okhttp3.OkHttpClient;

public class AllRequestUtil {

    static WeakReference<Context> mContext = new WeakReference<>(Application.getContext());
    static DBManager dbManager = DBManager.getInstance(mContext.get());
    private static boolean hasDialog;
    public static String nickName = "";
    private static String TAG = "AllRequestUtil===";

    /**
     * 获取云端家庭数据
     */
    public static void getCloudArea() {
        LogUtil.e(TAG + "getCloudArea0");
        if (!UserUtils.isLogin()) return;
        LogUtil.e(TAG + "getCloudArea01");
        HTTPCaller.getInstance().get(HomeCompanyListBean.class, HttpUrlConfig.getSCAreasUrl() + Constant.ONLY_SC,
                new RequestDataCallback<HomeCompanyListBean>() {
                    @Override
                    public void onSuccess(HomeCompanyListBean obj) {
                        super.onSuccess(obj);
                        LogUtil.e(TAG + "getCloudArea1");
                        if (obj != null) {
                            List<HomeCompanyBean> areas = obj.getAreas();
                            if (CollectionUtil.isNotEmpty(areas)) {// 云端家庭数据不为空，则同步
                                EventBus.getDefault().post(new MineUserInfoEvent(true));
                                getCloudAreaSuccess(areas);
                            } else {// 否则创建一个云端家庭
                                updateSCUserName();
                                UserUtils.setCloudUserName(nickName);
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
                        LogUtil.e(TAG + "getCloudArea2", errorMessage);
                    }
                });
    }

    /**
     * 插入默认 我的家庭
     */
    private static void insertDefaultHome() {
        HomeCompanyBean homeCompanyBean = new HomeCompanyBean(UiUtil.getString(R.string.main_my_home));
        homeCompanyBean.setArea_type(Constant.HOME_MODE);
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
            List<HomeCompanyBean> userHomeCompanyList = dbManager.queryHomeCompanyList();

            List<Long> cloudIdList = new ArrayList<>();
            List<Long> areaIdList = new ArrayList<>();
            for (HomeCompanyBean hcb : userHomeCompanyList) {
                cloudIdList.add(hcb.getId());
                areaIdList.add(hcb.getArea_id());
            }
            for (HomeCompanyBean area : areas) {
                if (cloudIdList.contains(area.getId()) || areaIdList.contains(area.getId())) {  // 如果家庭已存在，则更新
                    dbManager.updateHomeCompanyByCloudId(area);
                } else {//不存在，插入
                    if (area.isIs_bind_sa()) {
                        area.setArea_id(area.getId());
                    }
                    dbManager.insertCloudHomeCompany(area);
                }
            }
            if (CollectionUtil.isNotEmpty(areas)) {
                EventBus.getDefault().post(new MineUserInfoEvent(true));
            }
            sysAreaCloud(false);
            EventBus.getDefault().post(new RefreshHomeEvent());
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
                if (homeBean.isIs_bind_sa() && homeBean.getId() != homeBean.getArea_id()) {  // 如果绑了sa

                } else {
                    boolean isLast = (i == homeCompanyList.size() - 1);
                    List<LocationBean> list = dbManager.queryLocations(homeBean.getLocalId());
                    List<String> locationNames = new ArrayList<>();
                    if (CollectionUtil.isNotEmpty(list)) {
                        for (LocationBean locationBean : list) {
                            locationNames.add(locationBean.getName());
                        }
                    }
                    AddHCRequest addHCRequest = new AddHCRequest(homeBean.getName(), homeBean.getArea_type(), locationNames);
                    homeBean.setCloud_user_id(UserUtils.getCloudUserId());
                    HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreas() + Constant.ONLY_SC, addHCRequest,
                            new RequestDataCallback<IdBean>() {
                                @Override
                                public void onSuccess(IdBean obj) {
                                    super.onSuccess(obj);
                                    LogUtil.e(TAG + "sysAreaCloud-success", "success=homeId=" + obj.getId());
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
                                    LogUtil.e(TAG + "sysAreaCloud-fail", errorMessage);
                                }
                            });
                }
            }
        }
    }

    /**
     * 修改sc昵称
     */
    public static void updateSCUserName() {
        UpdateUserPost updateUserPost = new UpdateUserPost();
        updateUserPost.setNickname(nickName);
        String body = new Gson().toJson(updateUserPost);
        HTTPCaller.getInstance().put(Object.class, HttpUrlConfig.getSCUsersWithoutHeader() + "/" + UserUtils.getCloudUserId() + Constant.ONLY_SC, body, new RequestDataCallback<Object>() {
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                LogUtil.e(TAG + "updateSCUserName=====成功");
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                LogUtil.e(TAG + "updateSCUserName=====失败");
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
            for (HomeCompanyBean homeBean : homeCompanyList) {
                bindCloudWithoutCreateHome(homeBean);
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
        if (homeBean.isIs_bind_sa() && Constant.wifiInfo != null && homeBean.getBSSID() != null
                && homeBean.getBSSID().equalsIgnoreCase(Constant.wifiInfo.getBSSID())) {
            return true;
        }
        return false;
    }

    public static void bindCloudWithoutCreateHome(HomeCompanyBean home) {
        bindCloudWithoutCreateHome(home, null, home.getSa_lan_address(), null);
    }

    /**
     * 绑定云端家庭（不用在云端创建家庭）
     *
     * @param homeCompanyBean
     * @param channelEntity
     */
    public static void bindCloudWithoutCreateHome(HomeCompanyBean homeCompanyBean, ChannelEntity channelEntity, String sa_lan_address, OnBindListener listener) {
        LogUtil.e(TAG + "home=" + GsonConverter.getGson().toJson(homeCompanyBean));
        LogUtil.e(TAG + "isLogin=" + UserUtils.isLogin());
        if (homeCompanyBean.isIs_bind_sa() && homeCompanyBean.getId() != homeCompanyBean.getArea_id() && UserUtils.isLogin()) {
            checkUrl500(homeCompanyBean.getSa_lan_address(), new onCheckUrlListener() {
                @Override
                public void onSuccess() {//内网ping的通，但是同步云端使用SC
                    LogUtil.e(TAG + "bindCloudWithoutCreateHome11=onSuccess");
                    getAccessToken(homeCompanyBean, null, sa_lan_address, listener);
                }

                @Override
                public void onError() {
                    LogUtil.e(TAG + "bindCloudWithoutCreateHome12===onError");
                    if (channelEntity != null) {
                        LogUtil.e(TAG + "bindCloudWithoutCreateHome13===onError");
                        getAccessToken(homeCompanyBean, channelEntity, sa_lan_address, listener);
                    } else {
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
                                            LogUtil.e(TAG + "bindCloudWithoutCreateHome14===onSuccess");
                                            getAccessToken(homeCompanyBean, obj, sa_lan_address, listener);
                                        }
                                    }

                                    @Override
                                    public void onFailed(int errorCode, String errorMessage) {  // 获取临时通道失败
                                        super.onFailed(errorCode, errorMessage);
                                        LogUtil.e(TAG + "bindCloudWithoutCreateHome15=", "checkTemporaryUrl=onFailed");
                                    }
                                }, false);
                    }
                }
            });
        } else if (listener != null) {
            listener.onBindSuccess(0);
        }
    }

    public interface OnBindListener {
        void onBindSuccess(long homeId);
    }

    /**
     * 获取设备access_token
     *
     * @param homeCompanyBean
     * @param channelEntity
     */
    public static void getAccessToken(HomeCompanyBean homeCompanyBean, ChannelEntity channelEntity, String sa_lan_address, OnBindListener listener) {
        HTTPCaller.getInstance().post(AccessTokenBean.class, HttpUrlConfig.getDeviceAccessToken(), "", new RequestDataCallback<AccessTokenBean>() {
            @Override
            public void onSuccess(AccessTokenBean obj) {
                super.onSuccess(obj);
                if (obj != null) {
                    LogUtil.e(TAG + "getAccessToken==============成功");
                    BindCloudStrRequest request = new BindCloudStrRequest();
                    request.setAccess_token(obj.getAccess_token());
                    request.setCloud_user_id(UserUtils.getCloudUserId());
                    HttpConfig.addHeader(HttpConfig.SA_ID, homeCompanyBean.getSa_id());
                    HttpConfig.addHeader(HttpConfig.TOKEN_KEY, homeCompanyBean.getSa_user_token());

                    String apiUrl = sa_lan_address + HttpUrlConfig.API + HttpUrlParams.cloud_bind;
                    if (channelEntity != null) {
                        apiUrl = Constant.HTTPS_HEAD + channelEntity.getHost() + HttpUrlConfig.API + HttpUrlParams.cloud_bind;
                    }
                    HTTPCaller.getInstance().post(AreaIdBean.class, apiUrl, request,
                            new RequestDataCallback<AreaIdBean>() {
                                @Override
                                public void onSuccess(AreaIdBean obj) {
                                    super.onSuccess(obj);
                                    if (obj != null) {
                                        Constant.CurrentHome.setId(obj.getArea_id());// 重新设置云id值
                                        dbManager.updateHCAreaId(homeCompanyBean.getLocalId(), obj.getArea_id(), channelEntity == null);  // 绑定云端成功之后，修改本地云id值
                                        if (listener != null) {
                                            listener.onBindSuccess(obj.getArea_id());
                                        }
                                    }
                                    EventBus.getDefault().post(new UpdateSaUserNameEvent());
                                    LogUtil.e(TAG + "updateArea-bind====success=" + obj.getArea_id());
                                }

                                @Override
                                public void onFailed(int errorCode, String errorMessage) {
                                    super.onFailed(errorCode, errorMessage);
                                    LogUtil.e(TAG + "updateArea-fail=====" + errorMessage);
                                    if (listener != null)
                                        listener.onBindSuccess(0);
                                }
                            });
                }
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                super.onFailed(errorCode, errorMessage);
                LogUtil.e(TAG + "getAccessToken==============失败：" + errorMessage);
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

    public static void checkUrl500(String url, onCheckUrlListener listener) {
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
                            String dataStr = "";
                            String result = "";
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

    public static OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(500, TimeUnit.MILLISECONDS)
                .writeTimeout(500, TimeUnit.MILLISECONDS)
                .readTimeout(500, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("ZhiTing", true))
                .sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager())
                .hostnameVerifier((hostname, session) -> {
                    if (hostname.equals(HttpBaseUrl.baseSCHost)) {  // SC直接访问
                        return true;
                    } else {
                        if (session != null) {
                            String cersCacheJson = SpUtil.get(hostname); // 根据主机名获取本地存储的数据
                            LogUtil.e("缓存证书：" + cersCacheJson);
                            try {
                                Certificate[] certificates = session.getPeerCertificates();  // 证书
                                String cersJson = HTTPCaller.byte2Base64String(certificates[0].getEncoded());  // 把证书转为base64存储
                                if (!TextUtils.isEmpty(cersCacheJson)) {  // 如果之前存储过
                                    LogUtil.e("服务证书：" + cersJson);
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
//                certificateDialog.setConfirmListener(() -> {
//                    SpUtil.put(hostName, cerCache);
//                    certificateDialog.dismiss();
//                    hasDialog = false;
//                });
                certificateDialog.setConfirmListener(new CertificateDialog.OnConfirmListener() {
                    @Override
                    public void onConfirm() {
                        SpUtil.put(hostName, cerCache);
                        certificateDialog.dismiss();
                        hasDialog = false;
                    }

                    @Override
                    public void onCancel() {
                        certificateDialog.dismiss();
                    }
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
