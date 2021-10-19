package com.yctc.zhiting.utils;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.yctc.zhiting.R;
import com.yctc.zhiting.application.Application;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.HomeCompanyListBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.event.MineUserInfoEvent;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.UpdateSaUserNameEvent;
import com.yctc.zhiting.request.AddHCRequest;
import com.yctc.zhiting.request.BindCloudRequest;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AllRequestUtil {

    static WeakReference<Context> mContext = new WeakReference<>(Application.getContext());
    static DBManager dbManager = DBManager.getInstance(mContext.get());

    /**
     * 获取云端家庭数据
     */
    public static void getCloudArea() {
        if (!UserUtils.isLogin()) return;
        HTTPCaller.getInstance().get(HomeCompanyListBean.class, HttpUrlConfig.getSCAreasUrl(),
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
                                    sysAreaCloud();
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
        dbManager.insertHomeCompany(homeCompanyBean, null);
    }

    /**
     * 成功获取云端数据
     *
     * @param areas
     */
    private static void getCloudAreaSuccess(List<HomeCompanyBean> areas) {
        UiUtil.starThread(() -> {
            //删除本地云端家庭数据
//            dbManager.removeFamilyByCloudUserId();
            int cloudUserId = UserUtils.getCloudUserId();
            dbManager.removeFamilyNotPresentUserFamily(cloudUserId);
            for (HomeCompanyBean homeBean : areas) {
                homeBean.setCloud_user_id(cloudUserId);
            }
            List<HomeCompanyBean> userHomeCompanyList = dbManager.queryHomeCompanyListByCloudUserId(cloudUserId);
            List<Integer> cloudIdList = new ArrayList<>();
            for (HomeCompanyBean hcb : userHomeCompanyList){
                cloudIdList.add(hcb.getId());
            }
            for (HomeCompanyBean area : areas){
                if (cloudIdList.contains(area.getId())){  // 如果家庭已存在，则更新
                    dbManager.updateHomeCompanyByCloudId(area.getId(), area.getName());
                }else {  // 不存在，插入
                    dbManager.insertCloudHomeCompany(area);
                }
            }
//            dbManager.insertHomeCompanyList(areas);
            sysAreaCloud();
            EventBus.getDefault().post(new RefreshHome());
        });
    }

    /**
     * 本地数据同步到云端
     */
    public static void sysAreaCloud() {
        List<HomeCompanyBean> homeCompanyList = dbManager.queryLocalHomeCompanyList();
        if (CollectionUtil.isNotEmpty(homeCompanyList)) {
            for (int i = 0; i < homeCompanyList.size(); i++) {
                HomeCompanyBean homeBean = homeCompanyList.get(i);
                boolean isLast = (i == homeCompanyList.size() - 1);
                List<LocationBean> list = dbManager.queryLocations(homeBean.getLocalId());
//                SynPost.AreaBean areaBean = new SynPost.AreaBean(homeBean.getName(), list);
                List<String> locationNames = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(list)){
                    for (LocationBean locationBean : list){
                        locationNames.add(locationBean.getName());
                    }
                }
                AddHCRequest addHCRequest = new AddHCRequest(homeBean.getName(), locationNames);
                HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreas(), addHCRequest,
                        new RequestDataCallback<IdBean>() {
                            @Override
                            public void onSuccess(IdBean obj) {
                                super.onSuccess(obj);
                                Log.e("sysAreaCloud-success", "success");
                                updateArea(homeBean.getLocalId(), obj.getId(), isLast);
                            }

                            @Override
                            public void onFailed(int errorCode, String errorMessage) {
                                super.onFailed(errorCode, errorMessage);
                                Log.e("sysAreaCloud-fail", errorMessage);
                            }
                        });
            }
        }
    }

    /**
     * 更新本地家庭cloud_id和cloud_user_id
     *
     * @param localId 本地家庭
     * @param cloudId 云端id
     */
    public static void updateArea(long localId, int cloudId, boolean isLast) {
        dbManager.updateHomeCompanyCloudId(localId, cloudId, UserUtils.getCloudUserId());
        //数据全部同步完，检查是否有绑定sa数据
        if (isLast) {
            List<HomeCompanyBean> homeCompanyList = dbManager.queryHomeCompanyList();
            LogUtil.e("updateArea1=" + GsonConverter.getGson().toJson(homeCompanyList));
            for (HomeCompanyBean homeBean : homeCompanyList) {
                bindCloud(homeBean);
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
    public static void createHomeBindSC(HomeCompanyBean homeBean) {
        //云端id=0表示没有绑定云
        if (UserUtils.isLogin()) {
            if (homeBean.getId() == 0 && homeBean.isIs_bind_sa()) {
                SynPost.AreaBean areaBean = new SynPost.AreaBean(homeBean.getName(), new ArrayList<>());//家庭
                HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreas(), areaBean,
                        new RequestDataCallback<IdBean>() {
                            @Override
                            public void onSuccess(IdBean data) {
                                super.onSuccess(data);
                                LogUtil.e("sysAreaCloud-success", "success");
                                homeBean.setId(data.getId());
                                Constant.CurrentHome = homeBean;
                                dbManager.updateHomeCompanyCloudId(homeBean.getLocalId(), homeBean.getId(), UserUtils.getCloudUserId());
                                bindCloud(homeBean);
                            }

                            @Override
                            public void onFailed(int errorCode, String errorMessage) {
                                super.onFailed(errorCode, errorMessage);
                                LogUtil.e("sysAreaCloud-fail", errorMessage);
                            }
                        });
            } else {
                bindCloud(homeBean);
            }
        }
    }

    /**
     * 绑定云家庭
     *
     * @param homeBean 家庭对象
     */
    public static void bindCloud(HomeCompanyBean homeBean) {
        //如果再sa环境，如果未绑定云端，则绑定云端；否则不绑定
        if (!isSAEnvironment(homeBean)) return;
        LogUtil.e("updateArea2=" + GsonConverter.getGson().toJson(homeBean));
        Constant.CurrentHome.setId(homeBean.getId());
        BindCloudRequest request = new BindCloudRequest();
        request.setCloud_area_id(homeBean.getId());
        request.setCloud_user_id(homeBean.getCloud_user_id());

        HttpConfig.addHeader(homeBean.getSa_user_token());
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getBindCloud(), request,
                new RequestDataCallback<Object>() {
                    @Override
                    public void onSuccess(Object obj) {
                        super.onSuccess(obj);
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

    public interface onCheckUrlListener {
        void onSuccess();

        void onError();
    }
}
