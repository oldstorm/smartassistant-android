package com.yctc.zhiting.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.AndroidUtil;
import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.config.HttpBaseUrl;
import com.app.main.framework.fileutil.BaseFileUtil;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.imageutil.GlideUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.FeedbackContract;
import com.yctc.zhiting.activity.presenter.FeedbackPresenter;
import com.yctc.zhiting.adapter.FeedbackCategoryAdapter;
import com.yctc.zhiting.adapter.FeedbackTypeAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.entity.mine.FeedbackCategoryBean;
import com.yctc.zhiting.entity.mine.FeedbackPictureBean;
import com.yctc.zhiting.entity.mine.FeedbackTypeBean;
import com.yctc.zhiting.entity.mine.UploadFileBean;
import com.yctc.zhiting.request.FeedbackRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.FileUtils;
import com.yctc.zhiting.utils.LubanUtil;
import com.yctc.zhiting.utils.SpacesItemDecoration;
import com.yctc.zhiting.utils.UserUtils;
import com.zhy.view.flowlayout.FlowLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import top.zibin.luban.OnCompressListener;

/**
 * 意见反馈
 */
public class FeedbackActivity extends MVPBaseActivity<FeedbackContract.View, FeedbackPresenter> implements FeedbackContract.View {

    @BindView(R.id.rvType)
    RecyclerView rvType;
    @BindView(R.id.rvCategory)
    RecyclerView rvCategory;
    @BindView(R.id.flPicture)
    FlowLayout flPicture;
    @BindView(R.id.llNote)
    LinearLayout llNote;
    @BindView(R.id.tvAgree)
    TextView tvAgree;
    @BindView(R.id.tvSubmit)
    TextView tvSubmit;
    @BindView(R.id.etProblem)
    EditText etProblem;
    @BindView(R.id.etContact)
    EditText etContact;

    private FeedbackTypeAdapter mFeedbackTypeAdapter;
    private FeedbackCategoryAdapter mFeedbackCategoryAdapter;

    private List<FeedbackPictureBean> mPictureData = new LinkedList<>();
    private List<Integer> file_ids = new ArrayList<>();

    private final int REQUEST_ALBUM_OK = 0x11;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void initData() {
        super.initData();
        setTitleCenter(UiUtil.getString(R.string.mine_I_want_feedback));
        setSubmitEnabled(false);
        initRvType();
        initRvCategory();
        addFlPicture();
    }

    @SuppressLint("Range")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_ALBUM_OK) {
                if (data!=null) {
                    Uri uri = data.getData();
                    if (uri!=null) {
                        String path = com.yalantis.ucrop.util.FileUtils.getPath(this, uri);
                        LogUtil.e("path=" + path);
                        File file = new File(path);
                        double size = file.length();
                        LogUtil.e("size=" + size);
                        if (BaseFileUtil.B2MB(size) > 50) {
                            ToastUtil.show(UiUtil.getString(R.string.mine_feedback_file_restrict));
                            return;
                        }
                        byte[] fileData = FileUtils.hashV2(path);
                        String hash = "";
                        if (fileData != null) {
                            hash = FileUtils.toHex(fileData);
                        }
                        if (FileUtils.isImage(path)) {  // 如果是图片
                            String finalHash = hash;
                            LubanUtil.compressImage(this, path, new OnCompressListener() { // Luban压缩
                                @Override
                                public void onStart() {
                                    LogUtil.e("开始压缩");
                                }

                                @Override
                                public void onSuccess(File file) {
                                    LogUtil.e("压缩成功");
                                    LogUtil.e("压缩后路径" + file.getAbsolutePath());
                                    LogUtil.e("压缩后size=" + file.length());
                                    uploadFile(file.getAbsolutePath(), finalHash);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    LogUtil.e("压缩失败");
                                    uploadFile(path, finalHash);
                                }
                            });
                        } else {
                            uploadFile(path, hash);
                        }
                    }
                }
            }
        }
    }

    /**
     * 上传图片
     * @param path
     * @param hash
     */
    private void uploadFile(String path, String hash) {
        List<NameValuePair> uploadData = new ArrayList<>();
        uploadData.add(new NameValuePair(Constant.FILE_UPLOAD, path, true));
        uploadData.add(new NameValuePair(Constant.FILE_HASH, hash));
        uploadData.add(new NameValuePair(Constant.FILE_AUTH, Constant.PRI_SERVICE));
        uploadData.add(new NameValuePair(Constant.FILE_SERVER, Constant.CLOUD_SERVICE));
        uploadData.add(new NameValuePair(Constant.FILE_TYPE, Constant.FEEDBACK));
        mPresenter.uploadFile(uploadData);
    }

    @Override
    protected void hasPermissionTodo() {
        super.hasPermissionTodo();
        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "video/*;image/*");
        startActivityForResult(albumIntent, REQUEST_ALBUM_OK);
    }

    @OnClick({R.id.tvAgree, R.id.tvSubmit})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.tvAgree:
                tvAgree.setSelected(!tvAgree.isSelected());
                break;

            case R.id.tvSubmit:
                submit();
                break;
        }
    }

    /**
     * 提交
     */
    private void submit() {
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        int feedbackType = mFeedbackTypeAdapter.getType();
        feedbackRequest.setFeedback_type(feedbackType);
        feedbackRequest.setType(mFeedbackCategoryAdapter.getType());
        feedbackRequest.setDescription(etProblem.getText().toString());
        if (CollectionUtil.isNotEmpty(file_ids)) {
            feedbackRequest.setFile_ids(file_ids);
        }
        String contact = etContact.getText().toString().trim();
        if (!TextUtils.isEmpty(contact)) {
            feedbackRequest.setContact_information(contact);
        }
        if (feedbackType == 1) {
            boolean isAuth = tvAgree.isSelected();
            feedbackRequest.setIs_auth(isAuth);
            if (isAuth) {
                String apiVersion = HttpBaseUrl.VERSION.replace("/", "");
                apiVersion = apiVersion.replace("v", "");
                feedbackRequest.setApi_version(apiVersion);
                feedbackRequest.setApp_version(AndroidUtil.getAppVersion());
                feedbackRequest.setPhone_model(AndroidUtil.getSystemModel());
                feedbackRequest.setPhone_system(AndroidUtil.getSystemVersion());
                feedbackRequest.setSa_id(Constant.CurrentHome.getSa_id());
            }
        }
        String body = new Gson().toJson(feedbackRequest);

        mPresenter.feedback(UserUtils.getCloudUserId(), body);
    }

    @OnTextChanged(R.id.etProblem)
    void textChange() {
        checkData();
    }

    /**
     * 设置提交是否可点击
     *
     * @param isEnabled
     */
    private void setSubmitEnabled(boolean isEnabled) {
        tvSubmit.setEnabled(isEnabled);
        tvSubmit.setAlpha(isEnabled ? 1f : 0.5f);
    }

    /**
     * 检查反馈分类和内容是否填写
     */
    private void checkData() {
        String problem = etProblem.getText().toString().trim();
        setSubmitEnabled(mFeedbackCategoryAdapter.isSelected() && !TextUtils.isEmpty(problem));
    }

    /**
     * 类型
     */
    private void initRvType() {
        rvType.setLayoutManager(new GridLayoutManager(this, 3));
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_11));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_11));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        rvType.addItemDecoration(spacesItemDecoration);
        mFeedbackTypeAdapter = new FeedbackTypeAdapter();
        rvType.setAdapter(mFeedbackTypeAdapter);
        mFeedbackTypeAdapter.setNewData(FeedbackTypeBean.getData());
        mFeedbackTypeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FeedbackTypeBean ft = mFeedbackTypeAdapter.getItem(position);
                if (ft.isSelected()) return;
                for (FeedbackTypeBean feedbackTypeBean : mFeedbackTypeAdapter.getData()) {
                    feedbackTypeBean.setSelected(false);
                }
                ft.setSelected(true);
                setFeedbackCategoryData(ft);
                setAgreeVisible();
                checkData();
                mFeedbackTypeAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 设置同意...是否可见
     */
    private void setAgreeVisible() {
        tvAgree.setVisibility(FeedbackTypeBean.MEET_PROBLEM.isSelected() ? View.VISIBLE : View.INVISIBLE);
        tvAgree.setEnabled(FeedbackTypeBean.MEET_PROBLEM.isSelected());
    }

    /**
     * 设置反馈分类数据
     *
     * @param ft
     */
    private void setFeedbackCategoryData(FeedbackTypeBean ft) {
        switch (ft) {
            case MEET_PROBLEM:
                mFeedbackCategoryAdapter.setNewData(FeedbackCategoryBean.getProblemData());
                break;

            case ADVICE_SUGGEST:
                mFeedbackCategoryAdapter.setNewData(FeedbackCategoryBean.getSuggestData());
                break;
        }
    }

    /**
     * 分类
     */
    private void initRvCategory() {
        rvCategory.setLayoutManager(new GridLayoutManager(this, 3));
        HashMap<String, Integer> spaceValue = new HashMap<>();
        spaceValue.put(SpacesItemDecoration.LEFT_SPACE, UiUtil.getDimens(R.dimen.dp_11));
        spaceValue.put(SpacesItemDecoration.TOP_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        spaceValue.put(SpacesItemDecoration.RIGHT_SPACE, UiUtil.getDimens(R.dimen.dp_11));
        spaceValue.put(SpacesItemDecoration.BOTTOM_SPACE, UiUtil.getDimens(R.dimen.dp_7));
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration(spaceValue);
        rvCategory.addItemDecoration(spacesItemDecoration);
        mFeedbackCategoryAdapter = new FeedbackCategoryAdapter();
        rvCategory.setAdapter(mFeedbackCategoryAdapter);
        mFeedbackCategoryAdapter.setNewData(FeedbackCategoryBean.getProblemData());
        mFeedbackCategoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                FeedbackCategoryBean fc = mFeedbackCategoryAdapter.getItem(position);
                if (fc.isSelected()) return;
                for (FeedbackCategoryBean feedbackCategoryBean : mFeedbackCategoryAdapter.getData()) {
                    feedbackCategoryBean.setSelected(false);
                }
                fc.setSelected(true);
                checkData();
                mFeedbackCategoryAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 添加图片
     */
    private void addFlPicture() {
        View addView = View.inflate(this, R.layout.item_feedback_add_picture, null);
        addView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams clParams = (ViewGroup.LayoutParams) addView.getLayoutParams();
                clParams.width = (int) (UiUtil.getScreenWidth() * 0.14) + UiUtil.dip2px(13);
                clParams.height = (int) (UiUtil.getScreenWidth() * 0.14) + UiUtil.dip2px(13);
                addView.setLayoutParams(clParams);
            }
        });
        addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeStorageTask();
            }
        });
        flPicture.addView(addView, flPicture.getChildCount());
    }

    /**
     * 图片
     *
     * @param uploadFileBean
     */
    private void flPicture(UploadFileBean uploadFileBean) {
        file_ids.add(uploadFileBean.getFile_id());
        setLlNoteVisible();
        if (file_ids.size() == 9) {
            flPicture.removeViewAt(flPicture.getChildCount() - 1);
        }
        View pictureView = View.inflate(this, R.layout.item_feedback_picture, null);
        pictureView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams clParams = (ViewGroup.LayoutParams) pictureView.getLayoutParams();
                clParams.width = (int) (UiUtil.getScreenWidth() * 0.14) + UiUtil.dip2px(13);
                clParams.height = (int) (UiUtil.getScreenWidth() * 0.14) + UiUtil.dip2px(13);
                pictureView.setLayoutParams(clParams);
            }
        });
        ImageView ivPic = pictureView.findViewById(R.id.ivPic);
        ImageView ivClose = pictureView.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file_ids.remove(uploadFileBean.getFile_id());
                if (file_ids.size() == 8) {
                    addFlPicture();
                }
                setLlNoteVisible();
                flPicture.removeView(pictureView);
            }
        });
        GlideUtil.loadRound(uploadFileBean.getFile_url(), UiUtil.dip2px(4)).into(ivPic);
        flPicture.addView(pictureView, 0);
    }

    /**
     * 设置添加图片提示文案是否可见
     */
    private void setLlNoteVisible() {
        llNote.setVisibility(CollectionUtil.isEmpty(file_ids) ? View.VISIBLE : View.GONE);
    }


    /**
     * 上传成功
     *
     * @param uploadFileBean
     */
    @Override
    public void uploadFileSuccess(UploadFileBean uploadFileBean) {
        if (uploadFileBean != null)
            flPicture(uploadFileBean);
    }

    /**
     * 上传失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void uploadFileFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }

    /**
     * 反馈成功
     */
    @Override
    public void feedbackSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.mine_feedback_success));
        setResult(RESULT_OK);
        finish();
    }

    /**
     * 反馈失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void feedbackFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}