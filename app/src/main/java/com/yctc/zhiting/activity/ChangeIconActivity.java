package com.yctc.zhiting.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lihang.ShadowLayout;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.ChangeIconContract;
import com.yctc.zhiting.activity.presenter.ChangeIconPresenter;
import com.yctc.zhiting.adapter.ChangeIconAdapter;
import com.yctc.zhiting.entity.home.DeviceLogoBean;
import com.yctc.zhiting.entity.home.DeviceLogoListBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.SpacesItemDecoration;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 更换图标
 */
public class ChangeIconActivity extends MVPBaseActivity<ChangeIconContract.View, ChangeIconPresenter> implements ChangeIconContract.View {

    @BindView(R.id.rvData)
    RecyclerView rvData;
    @BindView(R.id.rbFinish)
    ProgressBar rbFinish;
    @BindView(R.id.tvFinish)
    TextView tvFinish;
    @BindView(R.id.layout_null)
    View layout_null;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;
    @BindView(R.id.llFinish)
    LinearLayout llFinish;

    private ChangeIconAdapter mChangeIconAdapter;
    private int mDeviceId;
    private int logoType;
    private int mChangeType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_icon;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_change_icon));
        ivEmpty.setImageResource(R.drawable.icon_device_empty);
        tvEmpty.setText(UiUtil.getString(R.string.no_data_hint));
        initRvData();
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mDeviceId = intent.getIntExtra(IntentConstant.ID, 0);
        logoType = intent.getIntExtra(IntentConstant.TYPE, 0);
        mChangeType = intent.getIntExtra(IntentConstant.TYPE, 0);
        mPresenter.getLogoList(mDeviceId);
    }

    /**
     * 初始化列表
     */
    private void initRvData() {
        rvData.setLayoutManager(new GridLayoutManager(this, 4));
        HashMap<String, Integer> spaceValue = new HashMap<>();
        mChangeIconAdapter = new ChangeIconAdapter();
        rvData.setAdapter(mChangeIconAdapter);
        mChangeIconAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                for (DeviceLogoBean logoBean : mChangeIconAdapter.getData()) {
                    logoBean.setSelected(false);
                }
                DeviceLogoBean deviceLogoBean = mChangeIconAdapter.getItem(position);
                deviceLogoBean.setSelected(true);
                mChangeType = deviceLogoBean.getType();
                mChangeIconAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.llFinish)
    void  onClickFinish(){
        if (logoType==mChangeType) {
            finish();
        } else {
            setLoading(true);
            mPresenter.updateType(mDeviceId, mChangeType);
        }
    }

    private void setLoading(boolean isLoading) {
        llFinish.setEnabled(!isLoading);
        rbFinish.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        tvFinish.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void setNullView(boolean visible) {
        layout_null.setVisibility(visible ? View.VISIBLE : View.GONE);
        rvData.setVisibility(visible ? View.GONE : View.VISIBLE);
        llFinish.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    /**
     * 设备图标成功
     * @param deviceLogoListBean
     */
    @Override
    public void getLogoListSuccess(DeviceLogoListBean deviceLogoListBean) {
        if (deviceLogoListBean!=null) {
            List<DeviceLogoBean> logoList = deviceLogoListBean.getDevice_logos();
            if (CollectionUtil.isNotEmpty(logoList)) {
                for (DeviceLogoBean deviceLogoBean : logoList) {  // 默认选中
                    if (deviceLogoBean.getType() == logoType) {
                        deviceLogoBean.setSelected(true);
                        break;
                    }
                }
            }
            mChangeIconAdapter.setNewData(logoList);
            setNullView(CollectionUtil.isEmpty(logoList));
        } else {
            setNullView(true);
        }
    }

    /**
     * 设备图标失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getLogoListFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 修改图标成功
     */
    @Override
    public void updateTypeSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.mine_save_success));
        setLoading(false);
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 修改图标失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void updateTypeFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        setLoading(false);
    }
}