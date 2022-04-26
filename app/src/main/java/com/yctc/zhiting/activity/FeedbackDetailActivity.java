package com.yctc.zhiting.activity;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.FeedbackDetailContract;
import com.yctc.zhiting.activity.presenter.FeedbackDetailPresenter;
import com.yctc.zhiting.adapter.FeedbackDetailInfoAdapter;
import com.yctc.zhiting.adapter.FeedbackDetailPicAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.KeyValBean;
import com.yctc.zhiting.entity.mine.FeedbackDetailBean;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.SpacesItemDecoration;
import com.yctc.zhiting.utils.StringUtil;
import com.yctc.zhiting.utils.UserUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 反馈详情
 */
public class FeedbackDetailActivity extends MVPBaseActivity<FeedbackDetailContract.View, FeedbackDetailPresenter> implements FeedbackDetailContract.View {

    @BindView(R.id.nsv)
    NestedScrollView nsv;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.rvPic)
    RecyclerView rvPic;
    @BindView(R.id.rvInfo)
    RecyclerView rvInfo;
    @BindView(R.id.tvAgree)
    TextView tvAgree;

    private FeedbackDetailPicAdapter mFeedbackDetailPicAdapter;
    private FeedbackDetailInfoAdapter mFeedbackDetailInfoAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback_detail;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitleCenter(UiUtil.getString(R.string.mine_feedback_detail));
        initRvPic();
        initRvInfo();

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        int feedbackId = intent.getIntExtra(IntentConstant.ID, 0);
        mPresenter.getFeedbackDetail(UserUtils.getCloudUserId(), feedbackId);
    }

    /**
     * 图片列表
     */
    private void initRvPic() {
        mFeedbackDetailPicAdapter = new FeedbackDetailPicAdapter();
        rvPic.setLayoutManager(new GridLayoutManager(this, 5));
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        rvPic.addItemDecoration(spacesItemDecoration);
        rvPic.setAdapter(mFeedbackDetailPicAdapter);
        mFeedbackDetailPicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FeedbackDetailBean.FilesBean filesBean = mFeedbackDetailPicAdapter.getItem(position);
                if (filesBean.getFile_type().equals(Constant.VIDEO)) {  // 视频
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String videoUrl = filesBean.getFile_url();
                    if (videoUrl.contains("?")) {
                        String[] videoUrlArr = videoUrl.split("\\?");
                        videoUrl = videoUrlArr[0];
                    }
                    intent.setDataAndType(Uri.parse(videoUrl), "video/*");
                    startActivity(intent);
                } else {  // 图片
                    Bundle bundle = new Bundle();
                    bundle.putString(IntentConstant.URL, filesBean.getFile_url());
                    switchToActivity(ImageActivity.class, bundle);
                }
            }
        });
    }

    /**
     * 其他信息
     */
    private void initRvInfo() {
        mFeedbackDetailInfoAdapter = new FeedbackDetailInfoAdapter();
        rvInfo.setLayoutManager(new LinearLayoutManager(this));
        rvInfo.setAdapter(mFeedbackDetailInfoAdapter);
    }

    /**
     * 反馈详情成功
     *
     * @param feedbackDetailBean
     */
    @Override
    public void getFeedbackDetailSuccess(FeedbackDetailBean feedbackDetailBean) {
        if (feedbackDetailBean != null) {
            tvContent.setText(feedbackDetailBean.getDescription());
            List<FeedbackDetailBean.FilesBean> files = feedbackDetailBean.getFiles();
            if (CollectionUtil.isNotEmpty(files)) {
                rvPic.setVisibility(View.VISIBLE);
                mFeedbackDetailPicAdapter.setNewData(files);
            }
            List<KeyValBean> data = new ArrayList<>();
            int feedbackType = feedbackDetailBean.getFeedback_type();
            String feedbackTypeStr = feedbackType == 1 ? UiUtil.getString(R.string.mine_meet_problem) : UiUtil.getString(R.string.mine_advise_suggest);
            data.add(new KeyValBean(UiUtil.getString(R.string.mine_type), feedbackTypeStr));
            data.add(new KeyValBean(UiUtil.getString(R.string.mine_category), StringUtil.getFeedbackType(this, feedbackDetailBean.getType())));
            String contactInfo = feedbackDetailBean.getContact_information();
            if (!TextUtils.isEmpty(contactInfo)) {
                data.add(new KeyValBean(UiUtil.getString(R.string.mine_contact_information), contactInfo));
            }
            mFeedbackDetailInfoAdapter.setNewData(data);
            tvAgree.setVisibility(feedbackDetailBean.isIs_auth() ? View.VISIBLE : View.GONE);
            nsv.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 反馈详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getFeedbackDetailFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}