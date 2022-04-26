package com.yctc.zhiting.activity;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.FeedbackListContract;
import com.yctc.zhiting.activity.presenter.FeedbackListPresenter;
import com.yctc.zhiting.adapter.FeedbackListAdapter;
import com.yctc.zhiting.entity.mine.FeedbackListBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 我的反馈
 */
public class FeedbackListActivity extends MVPBaseActivity<FeedbackListContract.View, FeedbackListPresenter> implements FeedbackListContract.View {

    @BindView(R.id.rvFeedback)
    RecyclerView rvFeedback;
    @BindView(R.id.tvEmpty)
    TextView tvEmpty;
    @BindView(R.id.ivEmpty)
    ImageView ivEmpty;
    @BindView(R.id.viewEmpty)
    View viewEmpty;

    private FeedbackListAdapter mFeedbackListAdapter;
    private final int FEEDBACK_CODE = 100;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback_list;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initData() {
        super.initData();
        tvEmpty.setText(UiUtil.getString(R.string.mine_no_feedback_record));
        ivEmpty.setImageResource(R.drawable.icon_empty_list);
        viewEmpty.setBackgroundColor(UiUtil.getColor(R.color.color_f6f8fd));
        initRv();
        getData();
    }

    private void getData() {
        mPresenter.getFeedbackList(UserUtils.getCloudUserId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FEEDBACK_CODE && resultCode == RESULT_OK) {
            getData();
        }
    }

    /**
     *  反馈列表
     */
    private void initRv() {
        rvFeedback.setLayoutManager(new LinearLayoutManager(this));
        mFeedbackListAdapter = new FeedbackListAdapter();
        rvFeedback.setAdapter(mFeedbackListAdapter);
        mFeedbackListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FeedbackListBean.FeedbacksBean feedbacksBean = mFeedbackListAdapter.getItem(position);
                Bundle bundle = new Bundle();
                bundle.putInt(IntentConstant.ID, feedbacksBean.getId());
                switchToActivity(FeedbackDetailActivity.class, bundle);
            }
        });
    }

    @OnClick({R.id.ivBack, R.id.ivEdit})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.ivBack:  // 返回
                onBackPressed();
                break;

            case R.id.ivEdit:  // 反馈
                switchToActivityForResult(FeedbackActivity.class, FEEDBACK_CODE);
                break;
        }
    }

    /**
     * 设置空视图
     * @param visible
     */
    private void setEmptyView(boolean visible) {
        viewEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
        rvFeedback.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    /**
     * 反馈列表成功
     * @param feedbackListBean
     */
    @Override
    public void getFeedbackListSuccess(FeedbackListBean feedbackListBean) {
        if (feedbackListBean != null) {
            List<FeedbackListBean.FeedbacksBean> feedbackList = feedbackListBean.getFeedbacks();
            setEmptyView(CollectionUtil.isEmpty(feedbackList));
            mFeedbackListAdapter.setNewData(feedbackList);
        } else {
            setEmptyView(true);
        }
    }

    /**
     * 反馈列表失败
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFeedbackListFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}