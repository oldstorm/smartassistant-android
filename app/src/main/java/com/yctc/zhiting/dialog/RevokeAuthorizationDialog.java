package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 解除绑定弹窗
 */
public class RevokeAuthorizationDialog extends CommonBaseDialog {

    @BindView(R.id.ivClose)
    ImageView ivClose;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.rbRevoke)
    ProgressBar rbRevoke;

    private String mThirdPartyName;

    public static RevokeAuthorizationDialog getInstance(String thirdPartyName) {
        RevokeAuthorizationDialog revokeAuthorizationDialog = new RevokeAuthorizationDialog();
        Bundle bundle = new Bundle();
        bundle.putString("thirdPartyName", thirdPartyName);
        revokeAuthorizationDialog.setArguments(bundle);
        return revokeAuthorizationDialog;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        mThirdPartyName = arguments.getString("thirdPartyName");
    }

    @Override
    protected void initView(View view) {
        setCancelable(false);
        if (!TextUtils.isEmpty(mThirdPartyName))
            tvTitle.setText(String.format(UiUtil.getString(R.string.mine_after_revoke_authorization), mThirdPartyName));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_revoke_authorization;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return LinearLayout.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    public void setLoading(boolean loading) {
        ivClose.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
        rbRevoke.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.llRevoke, R.id.tvCancel, R.id.ivClose})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.llRevoke:
                if (mRevokeAuthorizationListener != null) {
                    mRevokeAuthorizationListener.revokeAuthorization();
                }
                break;

            case R.id.tvCancel:
            case R.id.ivClose:
                dismiss();
                break;
        }
    }

    private RevokeAuthorizationListener mRevokeAuthorizationListener;

    public void setmRevokeAuthorizationListener(RevokeAuthorizationListener mRevokeAuthorizationListener) {
        this.mRevokeAuthorizationListener = mRevokeAuthorizationListener;
    }

    public interface RevokeAuthorizationListener {
        void revokeAuthorization();
    }
}
