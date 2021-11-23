package com.yctc.zhiting.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.httputil.NameValuePair;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.CreatePluginDetailActivity;
import com.yctc.zhiting.activity.LoginActivity;
import com.yctc.zhiting.adapter.SbCreateAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.UploadPluginDialog;
import com.yctc.zhiting.entity.CreatePluginListBean;
import com.yctc.zhiting.entity.mine.OperatePluginBean;
import com.yctc.zhiting.fragment.contract.SBCreateContract;
import com.yctc.zhiting.fragment.presenter.SBCreatePresenter;
import com.yctc.zhiting.request.AddOrUpdatePluginRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 支持品牌创作碎片
 */
public class SBCreateFragment extends MVPBaseFragment<SBCreateContract.View, SBCreatePresenter> implements SBCreateContract.View {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rvBrand)
    RecyclerView rvBrand;
    @BindView(R.id.layoutNull)
    View layoutNull;
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;

    private SbCreateAdapter mSbCreateAdapter;

    private UploadPluginDialog uploadPluginDialog;

    public static SBCreateFragment getInstance(){
        return new SBCreateFragment();
    }

    private final int DETAIL_REQUEST_CODE = 100;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sb_create;
    }

    @Override
    protected void initUI() {
        super.initUI();
        ivEmpty.setImageResource(R.drawable.icon_empty_data);
        tvEmpty.setText(getResources().getString(R.string.common_no_content));
        initRv();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull @NotNull RefreshLayout refreshLayout) {
                getData(false);
            }
        });
        initUploadPluginDialog();
        getData(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            getData(true);
        }
    }

    /**
     * 初始化列表
     */
    private void initRv(){
        mSbCreateAdapter = new SbCreateAdapter();
        rvBrand.setLayoutManager(new LinearLayoutManager(getContext()));
        rvBrand.setAdapter(mSbCreateAdapter);

        mSbCreateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CreatePluginListBean.PluginsBean pluginsBean = mSbCreateAdapter.getItem(position);
                if (pluginsBean.getBuild_status() == 1) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(IntentConstant.BEAN, pluginsBean);
                    switchToActivityForResult(CreatePluginDetailActivity.class, bundle, DETAIL_REQUEST_CODE);
                }
            }
        });

        mSbCreateAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                CreatePluginListBean.PluginsBean pluginsBean = mSbCreateAdapter.getItem(position);
                if (viewId == R.id.tvDel){ // 删除
                    if (pluginsBean.getBuild_status() == 1){  // 如果添加成功，需要确认弹窗
                        CenterAlertDialog alertDialog = CenterAlertDialog.newInstance(getResources().getString(R.string.mine_mine_plugin_del_tips_2), getResources().getString(R.string.common_cancel), getResources().getString(R.string.confirm), false);
                        alertDialog.setConfirmListener(del -> {
                            alertDialog.dismiss();
                            removePlugin(pluginsBean, position);

                        });
                        alertDialog.show(SBCreateFragment.this);
                    }else { // 如果添加失败，直接删除
                        removePlugin(pluginsBean, position);
                    }
                }
            }
        });
    }

    /**
     * 删除插件
     * @param pluginsBean
     * @param position
     */
    private void removePlugin(CreatePluginListBean.PluginsBean pluginsBean, int position){
        pluginsBean.setLoading(true);
        mSbCreateAdapter.notifyItemChanged(position);
        mPresenter.removePlugin(pluginsBean.getId(), position);
        setNullView(CollectionUtil.isEmpty(mSbCreateAdapter.getData()));
    }


    /**
     * 上传插件弹窗
     */
    private void initUploadPluginDialog(){
        uploadPluginDialog = new UploadPluginDialog();
        uploadPluginDialog.setClickUploadListener(new UploadPluginDialog.OnClickUploadListener() {
            @Override
            public void onUpload() {
                selectOtherFile();
            }

            @Override
            public void onConfirm(String filePath) {
                if (!TextUtils.isEmpty(filePath)){
                    uploadPlugin(filePath);
                }
            }

            @Override
            public void onClear() {

            }
        });
    }

    /**
     * 打开其他文件
     */
    private void selectOtherFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                String filePath = BaseFileUtil.getFilePathByUri(getBaseActivity(), uri);
//            if (!filePath.endsWith(Constant.DOT_ZIP)){
//                ToastUtil.show(UiUtil.getString(R.string.only_zip));
//                return;
//            }
//            uploadPlugin(filePath);
                LogUtil.e("onActivityResult=filePath:" + filePath);
                uploadPluginDialog.resetUploadStatus(1, filePath);
            }else if (requestCode == DETAIL_REQUEST_CODE){
                getData(true);
            }
        }
    }

    /**
     * 上传插件
     * @param filePath
     */
    private void uploadPlugin(String filePath){
        NameValuePair nameValuePair = new NameValuePair("file", filePath, true);
        List<NameValuePair> requestData = new ArrayList<>();
        requestData.add(nameValuePair);
        uploadPluginDialog.resetUploadStatus(1);
        mPresenter.uploadPlugin(requestData);
    }

    /**
     * 获取数据
     * @param showLoading
     */
    public void getData(boolean showLoading){
        if (mPresenter!=null) {
            List<NameValuePair> requestData = new ArrayList<>();
            requestData.add(new NameValuePair("list_type", String.valueOf(1)));
            mPresenter.getCreateList(requestData, showLoading);
        }
    }

    /**
     * 添加插件
     */
    @OnClick(R.id.tvAdd)
    void onClickAdd() {
        if (uploadPluginDialog!=null && !uploadPluginDialog.isShowing()) {
            uploadPluginDialog.show(this);
        }
    }

    /**
     * 创作列表成功
     * @param createPluginListBean
     */
    @Override
    public void getCreateListSuccess(CreatePluginListBean createPluginListBean) {
        refreshLayout.finishRefresh();
        if (createPluginListBean!=null){
            List<CreatePluginListBean.PluginsBean> plugins = createPluginListBean.getPlugins();
            if (CollectionUtil.isNotEmpty(plugins)){
                mSbCreateAdapter.setNewData(plugins);
                setNullView(false);
            }else {
                setNullView(true);
            }
        }else {
            setNullView(true);
        }
    }

    /**
     * 创作列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getCreateListFail(int errorCode, String msg) {
        refreshLayout.finishRefresh();
        ToastUtil.show(msg);
    }

    /**
     * 上传插件成功
     * @param object
     */
    @Override
    public void uploadPluginSuccess(Object object) {
//        uploadPluginDialog.resetUploadStatus(2);
        uploadPluginDialog.dismiss();
        getData(true);
    }

    /**
     * 上传插件失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void uploadPluginFail(int errorCode, String msg) {
        uploadPluginDialog.resetUploadStatus(3, msg);
    }

    /**
     * 删除插件成功
     * @param position
     */
    @Override
    public void removePluginSuccess(int position) {
        mSbCreateAdapter.getData().remove(position);
        mSbCreateAdapter.notifyItemRemoved(position);
        setNullView(CollectionUtil.isEmpty(mSbCreateAdapter.getData()));
        ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
    }

    /**
     * 删除插件失败
     * @param errorCode
     * @param msg
     * @param position
     */
    @Override
    public void removePluginFail(int errorCode, String msg, int position) {
        CreatePluginListBean.PluginsBean pluginsBean = mSbCreateAdapter.getItem(position);
        pluginsBean.setLoading(false);
        mSbCreateAdapter.notifyItemChanged(position);
        ToastUtil.show(msg);
    }


    /**
     * 有无无数据
     */
    private void setNullView(boolean visible) {
        rvBrand.setVisibility(visible ? View.GONE : View.VISIBLE);
        layoutNull.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
