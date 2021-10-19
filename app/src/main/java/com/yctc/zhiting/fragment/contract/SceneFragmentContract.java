package com.yctc.zhiting.fragment.contract;

import com.app.main.framework.baseview.BasePresenter;
import com.app.main.framework.baseview.BaseView;
import com.app.main.framework.httputil.RequestDataCallback;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.scene.SceneListBean;

public interface SceneFragmentContract {
    interface Model {
        void getSceneList(RequestDataCallback<SceneListBean> callback);
        void perform(int id, String body, RequestDataCallback<Object> callback);
        void getPermissions(int id, RequestDataCallback<PermissionBean> callback);
    }

    interface View extends BaseView {
        void getSceneListSuccess(SceneListBean data);
        void getSceneListError(int errorCode, String msg);
        void performSuccess();
        void performFail(int errorCode, String msg);
        void getPermissionsSuccess(PermissionBean permissionBean);
    }

    interface Presenter extends BasePresenter<View> {
        void getSceneList(boolean showLoading);
        void perform(int id, boolean execute);
        void getPermissions(int id);
    }
}
