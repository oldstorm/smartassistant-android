package com.yctc.zhiting.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 二维码弹窗
 */
public class QRCodeDialog extends CommonBaseDialog {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvHome)
    TextView tvHome;
    @BindView(R.id.ivCode)
    ImageView ivCode;
    @BindView(R.id.llInfo)
    LinearLayout llInfo;

    private Bitmap bitmap;
    private Handler handler;

    private String name;  // 用户昵称
    private String hName; // 家庭名称

    public QRCodeDialog(Bitmap bitmap, Handler handler, String name, String hName) {
        this.bitmap = bitmap;
        this.handler = handler;
        this.name = name;
        this.hName = hName;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_qr_code;
    }

    @Override
    protected int obtainWidth() {
        return dp2px(345);
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.CENTER;
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected void initView(View view) {
        ivCode.setImageBitmap(bitmap);
        tvName.setText(name);
        tvHome.setText(getContext().getResources().getString(R.string.mine_invite) + hName);
    }

    @OnClick(R.id.ivClose)
    void onClickClose() {
        dismiss();
    }

    @OnClick(R.id.tvSave)
    void onClickSave() {
        UiUtil.saveViewBitmap(llInfo, new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String picFile = (String) msg.obj;
                String[] split = picFile.split("/");
                String fileName = split[split.length - 1];
                try {
                    MediaStore.Images.Media.insertImage(getContext().getApplicationContext().getContentResolver(),
                            picFile, fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                        + picFile)));
                ToastUtil.show(getContext().getResources().getString(R.string.mine_save_success));
            }
        });
    }

}
