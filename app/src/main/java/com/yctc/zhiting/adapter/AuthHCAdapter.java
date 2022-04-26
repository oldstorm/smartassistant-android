package com.yctc.zhiting.adapter;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yctc.zhiting.R;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;

/**
 * 授权登录家庭公司列表
 */
public class AuthHCAdapter extends BaseQuickAdapter<HomeCompanyBean, BaseViewHolder> {

    public AuthHCAdapter() {
        super(R.layout.item_auth_hc);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeCompanyBean item) {
        helper.addOnClickListener(R.id.tvRetry);
        LinearLayout llParent = helper.getView(R.id.llParent);
        ImageView ivStatus = helper.getView(R.id.ivStatus);
        TextView tvRetry = helper.getView(R.id.tvRetry);
        TextView tvName = helper.getView(R.id.tvName);

        int authStatus = item.getAuthStatus();
        int statusDrawable;
        switch (authStatus) {
            case 1:
                statusDrawable = R.drawable.icon_auth_success;
                break;

            case 2:
                statusDrawable = R.drawable.icon_auth_fail;
                break;

            default:
                statusDrawable = R.drawable.icon_auth_normal;
                break;
        }
        ivStatus.setImageResource(statusDrawable);
        tvRetry.setVisibility(authStatus == 2 ? View.VISIBLE : View.GONE);
        tvName.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                tvName.setMaxWidth(llParent.getWidth() - ivStatus.getWidth() - tvRetry.getWidth() - 26);
                return true;
            }
        });
        tvName.setText(item.getName());
    }
}
