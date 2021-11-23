package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.scene.SceneListBean;

import java.util.List;

public interface SceneFragmentContract {
    interface Model {
        void getSceneList(RequestDataCallback<SceneListBean> callback);
        void perform(int id, String body, RequestDataCallback<Object> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
        void getSATokenBySC(int userId, List<NameValuePair> requestData, RequestDataCallback<FindSATokenBean> callback);
    }

    interface View extends BaseView {
        void getSceneListSuccess(SceneListBean data);
        void getSceneListError(int errorCode, String msg);
        void performSuccess();
        void performFail(int errorCode, String msg);
        void onPermissionsFail(int errorCode, String msg);
        void getPermissionsSuccess(PermissionBean permissionBean);
        void getSATokenSuccess(FindSATokenBean findSATokenBean);
        void getSATokenFail(int errorCode, String msg);
    }

    interface Presenter extends BasePresenter<View> {
        void getSceneList(boolean showLoading);
        void perform(int id, boolean execute);
        void getPermissions(int id);
        void getSAToken(int userId, List<NameValuePair> requestData);
    }
}
