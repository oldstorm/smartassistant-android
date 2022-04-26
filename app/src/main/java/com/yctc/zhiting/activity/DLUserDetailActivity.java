package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DLUserDetailContract;
import com.yctc.zhiting.activity.presenter.DLUserDetailPresenter;
import com.yctc.zhiting.adapter.DLPwdAdapter;
import com.yctc.zhiting.adapter.DLPwdTypeAdapter;
import com.yctc.zhiting.bean.FuncBottomBean;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.FuncBottomDialog;
import com.yctc.zhiting.dialog.PwdNameDialog;
import com.yctc.zhiting.dialog.RemovedTipsDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 门锁用户详情
 */
public class DLUserDetailActivity extends MVPBaseActivity<DLUserDetailContract.View, DLUserDetailPresenter> implements DLUserDetailContract.View {

    @BindView(R.id.tvEdit)
    TextView tvEdit;
    @BindView(R.id.tvCancel)
    TextView tvCancel;
    @BindView(R.id.tvRemove)
    TextView tvRemove;
    @BindView(R.id.rvPwdType)
    RecyclerView rvPwdType;

    private DLPwdTypeAdapter mDLPwdTypeAdapter;
    private FuncBottomDialog mFuncBottomDialog;

    private CenterAlertDialog mRemoveUserDialog; // 删除用户弹窗
    private CenterAlertDialog mRemovePwdDialog;  // 删除密码弹窗
    private PwdNameDialog mPwdNameDialog;  // 修改密码名称弹窗
    private RemovedTipsDialog mForcedUserDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dl_user_detail;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        initRv();
        showFullTipDialog();
    }

    /**
     * 初始化列表
     */
    private void initRv() {
        rvPwdType.setLayoutManager(new LinearLayoutManager(this));
        mDLPwdTypeAdapter = new DLPwdTypeAdapter();
        rvPwdType.setAdapter(mDLPwdTypeAdapter);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            data.add("");
        }
        mDLPwdTypeAdapter.setNewData(data);
        mDLPwdTypeAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                switch (viewId) {
                    case R.id.ivAdd:
                        showFuncBottomDialog(position);
                        break;
                }
            }
        });

        mDLPwdTypeAdapter.setSonAdapterItemChildListener(new DLPwdTypeAdapter.OnSonAdapterItemChildListener() {
            @Override
            public void onRemove() {  // 删除
                showRemoveDialog();
            }

            @Override
            public void onEdit() {  // 编辑
                showPwdNameDialog();
            }
        });
    }

    @OnClick({R.id.ivBack, R.id.tvEdit, R.id.tvCancel, R.id.tvRemove})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {

            case R.id.ivBack:  // 返回
                onBackPressed();
                break;

            case R.id.tvEdit:  // 编辑
                setIsEdit(true);
                break;

            case R.id.tvCancel:  // 取消
                setIsEdit(false);
                break;

            case R.id.tvRemove:  // 删除
                showRemoveUserDialog();
                break;
        }
    }

    /**
     * 设置是否处于编辑状态
     */
    private void setIsEdit(boolean isEdit) {
        tvEdit.setVisibility(isEdit ? View.GONE : View.VISIBLE);
        tvCancel.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        tvRemove.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        mDLPwdTypeAdapter.setIsEdit(isEdit);
    }

    /**
     * 添加密码弹窗
     *
     * @param type 暂定 0 指纹 1 密码 2 nfc
     */
    private void showFuncBottomDialog(int type) {
        LogUtil.e("TYPE===========" +type);
        if (mFuncBottomDialog == null) {
            mFuncBottomDialog = FuncBottomDialog.getInstance();
        }
        mFuncBottomDialog.setFuncBottomItemListener(new FuncBottomDialog.OnFuncBottomItemListener() {
            @Override
            public void onItem(FuncBottomBean funcBottomBean) {
                switch (funcBottomBean) {
                    case ADD_FROM_UNBIND:  // 从未绑定中选择添加
                        switchToActivity(DLAuthWayActivity.class);
                        break;

                    case ADD_FROM_DOOR_LOCK:  // 在门锁本地添加
                        if (type == 1) {
                            switchToActivity(DLAddPwdActivity.class);
                        } else {
                            ToastUtil.show("要去到引导页，还没做");
                        }
                        break;
                }
                mFuncBottomDialog.dismiss();
            }
        });
        if (mFuncBottomDialog != null && !mFuncBottomDialog.isShowing()) {
            String title = "";
            switch (type) {
                case 0:
                    title = UiUtil.getString(R.string.home_dl_add_finger_print);
                    break;

                case 1:
                    title = UiUtil.getString(R.string.home_dl_add_password);
                    break;

                case 2:
                    title = UiUtil.getString(R.string.home_dl_add_nfc);
                    break;
            }
            Bundle bundle = new Bundle();
            bundle.putString("title", title);
            mFuncBottomDialog.setArguments(bundle);
            mFuncBottomDialog.show(this);
        }
    }

    /**
     * 删除用户提示弹窗
     */
    private void showRemoveUserDialog() {
        if (mRemoveUserDialog == null) {
            mRemoveUserDialog = CenterAlertDialog.newInstance(UiUtil.getString(R.string.home_dl_remove), UiUtil.getString(R.string.home_dl_remove_user_tip),
                    UiUtil.getString(R.string.cancel), UiUtil.getString(R.string.confirm));
        }
        if (mRemoveUserDialog!=null && !mRemoveUserDialog.isShowing()) {
            mRemoveUserDialog.show(this);
        }
    }

    /**
     * 删除密码确认弹窗
     */
    private void showRemoveDialog() {
        if (mRemovePwdDialog == null) {
            mRemovePwdDialog = CenterAlertDialog.newInstance(UiUtil.getString(R.string.home_dl_remove), UiUtil.getString(R.string.home_dl_remove_ask),
                    UiUtil.getString(R.string.cancel), UiUtil.getString(R.string.home_dl_remove));
        }
        if (mRemovePwdDialog!=null && !mRemovePwdDialog.isShowing()) {
            mRemovePwdDialog.show(this);
        }
    }

    /**
     * 修改密码名称弹窗
     */
    private void showPwdNameDialog() {
        if (mPwdNameDialog == null) {
            mPwdNameDialog = PwdNameDialog.getInstance("指纹名称");
            mPwdNameDialog.setConfirmListener(new PwdNameDialog.OnConfirmListener() {
                @Override
                public void onConfirm(String pwdName) {

                }
            });
        }
        if (mPwdNameDialog != null && !mPwdNameDialog.isShowing()) {
            mPwdNameDialog.show(this);
        }
    }

    /**
     * 胁迫用户提示弹窗
     */
    private void showFullTipDialog() {
        if (mForcedUserDialog == null) {
            mForcedUserDialog = new RemovedTipsDialog(UiUtil.getString(R.string.home_dl_forced_user_tip));
            mForcedUserDialog.setKnowListener(new RemovedTipsDialog.OnKnowListener() {
                @Override
                public void onKnow() {
                    mForcedUserDialog.dismiss();
                }
            });
        }
        if (mForcedUserDialog != null && !mForcedUserDialog.isShowing()) {
            mForcedUserDialog.show(this);
        }
    }

}