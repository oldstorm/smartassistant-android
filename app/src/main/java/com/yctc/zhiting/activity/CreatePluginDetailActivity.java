package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ApNetworkContract;
import com.yctc.zhiting.activity.contract.CreatePluginDetailContract;
import com.yctc.zhiting.activity.presenter.ApNetworkPresenter;
import com.yctc.zhiting.activity.presenter.CreatePluginDetailPresenter;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.PluginsBean;
import com.yctc.zhiting.entity.scene.PluginDetailBean;
import com.yctc.zhiting.fragment.SBCreateFragment;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.widget.RingProgressBar;
import com.yctc.zhiting.widget.TagTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 创作插件详情
 */
public class CreatePluginDetailActivity extends MVPBaseActivity<CreatePluginDetailContract.View, CreatePluginDetailPresenter> implements CreatePluginDetailContract.View {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvDel)
    TextView tvDel;
    @BindView(R.id.ringProgressBar)
    RingProgressBar ringProgressBar;
    @BindView(R.id.tvDesc)
    TextView tvDesc;
    @BindView(R.id.tvVersion)
    TextView tvVersion;

    private CreatePluginListBean.PluginsBean pluginsBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_plugin_detail;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.mine_mine_plugin_detail));
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
         pluginsBean = (CreatePluginListBean.PluginsBean) intent.getSerializableExtra(IntentConstant.BEAN);
         mPresenter.getDetail(pluginsBean.getId());
//        List<String> tags = new ArrayList<>();
//        int buildStatus = pluginsBean.getBuild_status();
//        if (buildStatus == -1)
//            tags.add(UiUtil.getString(R.string.add_fail));
//        tvName.setContentAndTag(pluginsBean.getName()+"\t", tags);
//        tvDesc.setVisibility(TextUtils.isEmpty(pluginsBean.getInfo()) ? View.GONE : View.VISIBLE);
//        tvDesc.setText(pluginsBean.getInfo());
//        ringProgressBar.setVisibility(buildStatus == 0 ? View.VISIBLE : View.GONE);
//        ringProgressBar.setProgress(30);
//        ringProgressBar.setRotating(buildStatus == 0);
//        tvDel.setVisibility(buildStatus != 0 ? View.VISIBLE : View.GONE);
    }


    @OnClick(R.id.tvDel)
    void onClickDel(){
        if (pluginsBean.getBuild_status() == 1){  // 如果添加成功，需要确认弹窗
            CenterAlertDialog alertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_mine_plugin_del_tips_2), getResources().getString(R.string.common_cancel), getResources().getString(R.string.confirm), false);
            alertDialog.setConfirmListener(del -> {
                alertDialog.dismiss();
               mPresenter.removePlugin(pluginsBean.getId());

            });
            alertDialog.show(CreatePluginDetailActivity.this);
        }else { // 如果添加失败，直接删除
            mPresenter.removePlugin(pluginsBean.getId());
        }
    }

    /**
     * 获取数据成功
     * @param pluginDetailBean
     */
    @Override
    public void getDetailSuccess(PluginDetailBean pluginDetailBean) {
        if (pluginDetailBean!=null){
            PluginsBean pluginsBean = pluginDetailBean.getPlugin();
            if (pluginsBean != null){
                tvVersion.setText( getResources().getString(R.string.brand_versionCode) + pluginsBean.getVersion());
                tvVersion.setVisibility(TextUtils.isEmpty(pluginsBean.getVersion()) ? View.GONE : View.VISIBLE);
                tvName.setText(pluginsBean.getName());
                tvDesc.setVisibility(TextUtils.isEmpty(pluginsBean.getInfo()) ? View.GONE : View.VISIBLE);
                tvDesc.setText(pluginsBean.getInfo());
            }
        }
    }

    /**
     * 获取数据失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 删除成功
     */
    @Override
    public void removePluginSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 删除失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void removePluginFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}