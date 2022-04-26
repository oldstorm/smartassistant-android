package com.yctc.zhiting.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.httputil.NameValuePair;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.app.main.framework.imageutil.GlideUtil;
import com.google.gson.Gson;
import com.yalantis.ucrop.UCrop;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AvatarContract;
import com.yctc.zhiting.activity.presenter.AvatarPresenter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.entity.mine.UpdateUserPost;
import com.yctc.zhiting.entity.mine.UploadFileBean;
import com.yctc.zhiting.event.UpdateHeadImgSuccessEvent;
import com.yctc.zhiting.utils.FileUtils;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 头像图片
 */
public class AvatarActivity extends MVPBaseActivity<AvatarContract.View, AvatarPresenter> implements AvatarContract.View {

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage";

    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.llModify)
    LinearLayout llModify;
    @BindView(R.id.rbModify)
    ProgressBar rbModify;
    @BindView(R.id.tvModify)
    TextView tvModify;

    String destinationFileName;


    private final int CROP_REQUEST_CODE = 1;
    private String avatarUrl = "";

    private DBManager dbManager;
    private WeakReference<Context> mContext;
    private boolean mIsUpload;


    @Override
    protected void initUI() {
        super.initUI();
        mContext = new WeakReference<>(getActivity());
        dbManager = DBManager.getInstance(mContext.get());
        ivAvatar.post(new Runnable() {
            @Override
            public void run() {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) ivAvatar.getLayoutParams();
                layoutParams.width = UiUtil.getScreenWidth();
                layoutParams.height = UiUtil.getScreenWidth();
                ivAvatar.setLayoutParams(layoutParams);
            }
        });
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        avatarUrl = intent.getStringExtra(IntentConstant.AVATAR_URL);
        GlideUtil.load(avatarUrl).into(ivAvatar);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_avatar;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CROP_REQUEST_CODE) {
                final Uri selectedUri = data.getData();
                String type = data.getType();
                if (!isSupport(type)) {
                    ToastUtil.show(UiUtil.getString(R.string.mine_file_is_not_support));
                    return;
                }
                if (selectedUri != null) {
                    startCrop(selectedUri);
                } else {
                    Toast.makeText(AvatarActivity.this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            handleCropError(data);
        }
    }

    private boolean isSupport(String type) {
        LogUtil.e("isSupport==========" + type);
        if (!TextUtils.isEmpty(type) && (type.equalsIgnoreCase("image/heic") || type.equalsIgnoreCase("image/heif"))) {
            return false;
        }
        return true;
    }

    private void startCrop(@NonNull Uri uri) {
        destinationFileName = SAMPLE_CROPPED_IMAGE_NAME + System.currentTimeMillis();
        destinationFileName += ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(60);
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(false);
        uCrop.withOptions(options);
        uCrop.start(AvatarActivity.this);
    }

    /**
     * 处理裁剪成功
     *
     * @param result
     */
    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            String path = getCacheDir().getAbsolutePath() + "/" + destinationFileName;
            if (Constant.CurrentHome.getArea_id() > 0 || Constant.CurrentHome.getId() > 0) {  // 有绑sa或是云端家庭
                byte[] fileData = FileUtils.hashV2(path);
                String hash = "";
                if (fileData != null) {
                    hash = FileUtils.toHex(fileData);
                }

                if (!TextUtils.isEmpty(hash)) {
                    List<NameValuePair> uploadSAData = new ArrayList<>();
                    uploadSAData.add(new NameValuePair(Constant.FILE_UPLOAD, path, true));
                    uploadSAData.add(new NameValuePair(Constant.FILE_HASH, hash));
                    uploadSAData.add(new NameValuePair(Constant.FILE_TYPE, Constant.IMG));
                    mPresenter.uploadAvatar(uploadSAData, false);
                    if (UserUtils.isLogin()) { // 登录了，也要更换SC头像
                        List<NameValuePair> uploadSCData = new ArrayList<>();
                        uploadSCData.add(new NameValuePair(Constant.FILE_UPLOAD, path, true));
                        uploadSCData.add(new NameValuePair(Constant.FILE_HASH, hash));
                        uploadSCData.add(new NameValuePair(Constant.FILE_AUTH, Constant.PRI_SERVICE));
                        uploadSCData.add(new NameValuePair(Constant.FILE_SERVER, Constant.CLOUD_SERVICE));
                        uploadSCData.add(new NameValuePair(Constant.FILE_TYPE, Constant.IMG));
                        HttpConfig.clearHear(HttpConfig.AREA_ID);
                        mPresenter.uploadAvatarSC(uploadSCData, true);
                    }
                    setUploading(true);
                }
            } else {
                EventBus.getDefault().post(new UpdateHeadImgSuccessEvent(path));
                avatarUrl = path;
                updateUserInfo();
                GlideUtil.load(path).into(ivAvatar);
                finish();
            }

        } else {
            Toast.makeText(AvatarActivity.this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(AvatarActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AvatarActivity.this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 修改中
     *
     * @param isUpload
     */
    private void setUploading(boolean isUpload) {
        rbModify.setVisibility(isUpload ? View.VISIBLE : View.GONE);
        tvModify.setVisibility(isUpload ? View.GONE : View.VISIBLE);
        mIsUpload = isUpload;
    }

    @OnClick(R.id.llModify)
    void onClickModify(View view) {
        if (!mIsUpload) {
            pickFromGallery();
        }
    }

    @OnClick(R.id.ivAvatar)
    void onClickAvatar() {
        finish();
    }

    /**
     * 打开手机图片
     */
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*")
                .addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }

        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), CROP_REQUEST_CODE);
    }

    /**
     * 上传SA头像成功
     *
     * @param uploadFileBean
     */
    @Override
    public void uploadAvatarSuccess(UploadFileBean uploadFileBean) {
        LogUtil.e("上传头像成功");
        avatarUrl = uploadFileBean.getFile_url();
        UpdateUserPost updateUserPost = new UpdateUserPost();
        updateUserPost.setAvatar_id(uploadFileBean.getFile_id());
        updateUserPost.setAvatar_url(avatarUrl);
        String body = new Gson().toJson(updateUserPost);
        mPresenter.updateMember(Constant.CurrentHome.getUser_id(), body);
    }

    /**
     * 上传SA头像失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void uploadAvatarFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        setUploading(false);
        LogUtil.e("上传头像失败============" + msg);
    }

    /**
     * 上传SC头像成功
     *
     * @param uploadFileBean
     */
    @Override
    public void uploadAvatarSCSuccess(UploadFileBean uploadFileBean) {
        avatarUrl = uploadFileBean.getFile_url();
        UpdateUserPost updateUserPost = new UpdateUserPost();
        updateUserPost.setAvatar_id(uploadFileBean.getFile_id());
        String body = new Gson().toJson(updateUserPost);
        mPresenter.updateMemberSC(UserUtils.getCloudUserId(), body);
    }

    /**
     * 上传SC头像失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void uploadAvatarSCFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        setUploading(false);
        LogUtil.e("上传头像失败============" + msg);
    }

    @Override
    public void updateSuccess() {
        if (!UserUtils.isLogin()) {
            ToastUtil.show(UiUtil.getString(R.string.mine_modify_success));
            updateUserInfo();
            GlideUtil.load(avatarUrl).into(ivAvatar);
            EventBus.getDefault().post(new UpdateHeadImgSuccessEvent(avatarUrl));
            setUploading(false);
            finish();
        }
    }

    @Override
    public void updateFail(int errorCode, String msg) {
        ToastUtil.show(msg);
        if (!UserUtils.isLogin()) {
            ToastUtil.show(UiUtil.getString(R.string.mine_modify_fail));
            setUploading(false);
        }
        LogUtil.e("修改失败=========" + msg);
    }

    @Override
    public void updateScSuccess() {
        ToastUtil.show(UiUtil.getString(R.string.mine_modify_success));
        updateUserInfo();
        GlideUtil.load(avatarUrl).into(ivAvatar);
        UserUtils.setCloudUserHeadImg(avatarUrl);
        EventBus.getDefault().post(new UpdateHeadImgSuccessEvent(avatarUrl));
        LogUtil.e("修改成功=========");
        setUploading(false);
        finish();
    }

    @Override
    public void updateScFail(int errorCode, String msg) {
        LogUtil.e("修改失败=========" + msg);
        ToastUtil.show(UiUtil.getString(R.string.mine_modify_fail));
        setUploading(false);
    }


    /**
     * 修改用户昵称
     */
    private void updateUserInfo() {
        UiUtil.starThread(() -> {
            dbManager.updateUser(1, "", avatarUrl);
        });
    }
}