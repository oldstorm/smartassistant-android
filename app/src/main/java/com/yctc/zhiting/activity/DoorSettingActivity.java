package com.yctc.zhiting.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.DoorSettingContract;
import com.yctc.zhiting.activity.presenter.DoorSettingPresenter;
import com.yctc.zhiting.dialog.DoorLanguageDialog;
import com.yctc.zhiting.dialog.VolumeDialog;
import com.yctc.zhiting.widget.VolumeProgressBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author by Ouyangle, Date on 2022/4/11.
 * PS: Not easy to write code, please indicate.
 */
public class DoorSettingActivity extends MVPBaseActivity<DoorSettingContract.View, DoorSettingPresenter> implements DoorSettingContract.View {
    @BindView(R.id.rlApplication)
    RelativeLayout rlApplication;
    @BindView(R.id.rlLanguage)
    RelativeLayout rlLanguage;
    @BindView(R.id.rlVolume)
    RelativeLayout rlVolume;
    @BindView(R.id.tvLanguage)
    TextView tvLanguage;
    @BindView(R.id.tvVolume)
    TextView tvVolume;
    @BindView(R.id.switch1)
    Switch switch1;
    @BindView(R.id.switch2)
    Switch switch2;

    @BindView(R.id.llView1)
    View llView1;
    @BindView(R.id.llView2)
    View llView2;
    @BindView(R.id.llDoorApplication)
    View llDoorApplication;
    @BindView(R.id.rlLook)
    RelativeLayout rlLook;
    @BindView(R.id.rlCatEye)
    RelativeLayout rlCatEye;

    @BindView(R.id.ivType)
    ImageView ivType;
    @BindView(R.id.tvHint)
    TextView tvHint;

    private VolumeDialog volumeDialog;
    private DoorLanguageDialog languageDialog;
    private int viewIndex = 0;
    private boolean atLookView = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_door_setting;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(UiUtil.getString(R.string.home_setting));

        volumeDialog = new VolumeDialog((progress, values) -> {
            tvVolume.setText(values);
        });
        languageDialog = new DoorLanguageDialog();

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {

        });

        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {

        });
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.rlApplication, R.id.rlLanguage, R.id.rlVolume, R.id.rlLook, R.id.rlCatEye})
    void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.rlApplication:
                viewIndex ++;
                initView();
                break;
            case R.id.rlLanguage:
                languageDialog.show(this);
                break;
            case R.id.rlVolume:
                volumeDialog.show(this);
                break;
            case R.id.rlLook:
                viewIndex ++;
                atLookView = true;
                initView();
                break;
            case R.id.rlCatEye:
                viewIndex ++;
                atLookView = false;
                initView();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        viewIndex --;
        if(viewIndex >=0){
            initView();
        }else {
            super.onBackPressed();
        }
    }

    private void initView() {
        switch (viewIndex){
            case 0:
                llView1.setVisibility(View.VISIBLE);
                llView2.setVisibility(View.GONE);
                llDoorApplication.setVisibility(View.GONE);
                setTitleCenter(UiUtil.getString(R.string.home_setting));
                break;
            case 1:
                llView1.setVisibility(View.GONE);
                llView2.setVisibility(View.VISIBLE);
                llDoorApplication.setVisibility(View.GONE);
                setTitleCenter(UiUtil.getString(R.string.door_setting_hint2));
                break;
            case 2:
                llDoorApplication.setVisibility(View.VISIBLE);
                setTitleCenter(atLookView ? UiUtil.getString(R.string.door_setting_hint13) : UiUtil.getString(R.string.door_setting_hint14));
                ivType.setImageResource(atLookView ? R.drawable.icon_door_lock : R.drawable.icon_cat_eye);
                tvHint.setText(atLookView ? UiUtil.getString(R.string.door_setting_hint11) : UiUtil.getString(R.string.door_setting_hint12));
                break;
        }
    }
}
