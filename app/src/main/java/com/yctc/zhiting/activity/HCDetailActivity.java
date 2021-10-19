package com.yctc.zhiting.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.google.gson.Gson;
import com.king.zxing.util.CodeUtils;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.HCDetailContract;
import com.yctc.zhiting.activity.presenter.HCDetailPresenter;
import com.yctc.zhiting.adapter.MemberAdapter;
import com.yctc.zhiting.bean.ListBottomBean;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.dialog.ListBottomDialog;
import com.yctc.zhiting.dialog.QRCodeDialog;
import com.yctc.zhiting.entity.GenerateCodeJson;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.InvitationCodeBean;
import com.yctc.zhiting.entity.mine.InvitationCodePost;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.RefreshHomeList;
import com.yctc.zhiting.fragment.HomeFragment;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.IntentConstant;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.yctc.zhiting.config.Constant.CurrentHome;
import static com.yctc.zhiting.config.Constant.wifiInfo;

/**
 * 家庭/公司详情
 */
public class HCDetailActivity extends MVPBaseActivity<HCDetailContract.View, HCDetailPresenter> implements HCDetailContract.View {

    @BindView(R.id.rvMember)
    RecyclerView rvMember;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvRoom)
    TextView tvRoom;
    @BindView(R.id.tvQuit)
    TextView tvQuit;
    @BindView(R.id.rlCode)
    RelativeLayout rlCode;
    @BindView(R.id.tvMember)
    TextView tvMember;
    @BindView(R.id.llMember)
    LinearLayout llMember;
    @BindView(R.id.llTips)
    LinearLayout llTips;
    @BindView(R.id.rlName)
    RelativeLayout rlName;
    @BindView(R.id.rlRoom)
    RelativeLayout rlRoom;

    private MemberAdapter memberAdapter;
    private MyHandler myHandler;
    private QRCodeDialog qrCodeDialog;

    private WeakReference<Context> mContext;
    private DBManager dbManager;

    private int type = 1;// 类型 1. 自己创建
    private int userId;
    private long mLocalId;// 家庭本地id
    private boolean isBindSa;//是否绑定sa
    private boolean isCreator;// 是否是创建者
    private boolean updateNamePermission;// 是否有修改家庭名称权限
    private boolean isLoadRole;//是否已经请求过角色列表
    private String updateName;// 修改的名称
    private String name;
    private String userName;
    private String saUrl;
    private List<ListBottomBean> roleData = new ArrayList<>();
    private ListBottomDialog mListBottomDialog;
    private CenterAlertDialog centerAlertDialog;
    private HomeCompanyBean mHomeBean;
    private HomeCompanyBean mTempHomeBean;

    private long mCurrentHomeLocalId;

    private int mAreaId;//CurrentHome id
    private boolean mOriginBindSa;//CurrentHome 的 绑定sa情况
    private String mSaLanAddress;
    private String mSaToken;//CurrentHome 的token

    /**
     * 1.家庭详情
     * 2.角色列表
     * 3.删除
     * 4.修改名称
     */
    private int kind;

    private boolean isExitFamily;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_h_c_detail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        kind = 1;
        //已经绑定sa且在sa环境，或者登陆状态
        if (isBindSa || (UserUtils.isLogin() && userId > 0)) {
            checkUrl();
        } else {//否则，从本地获取
            int cloudId = mHomeBean.getId();
            if (UserUtils.isLogin() && cloudId > 0) { // 登录了
                mPresenter.getDetail(cloudId);
            } else {
                loadData();
            }
        }
    }

    /**
     * 是否再这个SA的环境
     *
     * @return
     */
    public boolean isSAEnvironment() {
        if (wifiInfo != null && CurrentHome != null && CurrentHome.getMac_address() != null &&
                wifiInfo.getBSSID().equalsIgnoreCase(CurrentHome.getMac_address())) {
            return true;
        }
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        setTitleCenter(getResources().getString(R.string.mine_home_company));

        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());

        mHomeBean = (HomeCompanyBean) getIntent().getSerializableExtra(IntentConstant.BEAN);

        String homeBeanJson = GsonConverter.getGson().toJson(CurrentHome);
        mTempHomeBean = GsonConverter.getGson().fromJson(homeBeanJson, HomeCompanyBean.class);
        mCurrentHomeLocalId = CurrentHome.getLocalId();
        mAreaId = CurrentHome.getId();
        CurrentHome = mHomeBean;

        mLocalId = mHomeBean.getLocalId();
        userId = mHomeBean.getUser_id();
        isBindSa = mHomeBean.isIs_bind_sa();
        saUrl = mHomeBean.getSa_lan_address();
//        mAreaId = mHomeBean.getId();
        tvQuit.setText(type == 1 ? getResources().getString(R.string.mine_remove) : getResources().getString(R.string.mine_quit_family));
        isCreator = isBindSa ? false : true;
        myHandler = new MyHandler(this);

        memberAdapter = new MemberAdapter();
        rvMember.setLayoutManager(new LinearLayoutManager(this));
        rvMember.setAdapter(memberAdapter);

        memberAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(HCDetailActivity.this, MemberDetailActivity.class);
            intent.putExtra(IntentConstant.ID, memberAdapter.getItem(position).getUser_id());
            startActivity(intent);
        });
    }

    /**
     * 检测地址
     */
    private void checkUrl() {
        if (isBindSa && wifiInfo != null) {
            showLoadingView();
            AllRequestUtil.checkUrl(CurrentHome.getSa_lan_address(), new AllRequestUtil.onCheckUrlListener() {
                @Override
                public void onSuccess() {
                    LogUtil.e("checkUrl===onSuccess");
                    WifiInfo wifiInfo = Constant.wifiInfo;
                    handleTipStatus(wifiInfo.getBSSID());
                }

                @Override
                public void onError() {
                    LogUtil.e("checkUrl===onError");
                    handleTipStatus("");
                }
            });
        } else {
            getData();
        }
    }

    private void handleTipStatus(String macAddress) {
        hideLoadingView();
        CurrentHome.setMac_address(macAddress);
        //dbManager.updateHomeCompanyCloudId(CurrentHome.getLocalId(), CurrentHome.getId(), UserUtils.getCloudUserId());
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        boolean isEnabled = isBindSa && !isSAEnvironment() && !UserUtils.isLogin();
        rlName.setEnabled(!isEnabled);
        rlCode.setEnabled(!isEnabled);
        rlRoom.setEnabled(!isEnabled);
        tvQuit.setVisibility(View.GONE);
        llTips.setVisibility(isEnabled ? View.VISIBLE : View.GONE);

        HttpConfig.addHeader(CurrentHome.getSa_user_token());
        mPresenter.getDetail(1);
        mPresenter.getMembers();
        mPresenter.getPermissions(userId);
        mPresenter.getMemberDetail(userId);
    }

    /**
     * 修改名称
     */
    @OnClick(R.id.rlName)
    void onClickName() {
        kind = 4;
        if (!isBindSa) {//为绑定sa
            updateHcName();
        } else {
            if (updateNamePermission) {
                updateHcName();
            }
        }
    }

    /**
     * 修改家庭名称
     */
    private void updateHcName() {
        String title = getResources().getString(R.string.mine_home_company_name);
        String hint = getResources().getString(R.string.mine_input_area);
        String contentStr = tvName.getText().toString();
        EditBottomDialog editBottomDialog = EditBottomDialog.newInstance(title, hint, contentStr, 2);
        editBottomDialog.setClickSaveListener(content -> {
            updateName = content;
            if (mHomeBean.getId() > 0 || isBindSa) {
                int homeId = getHomeId();
                mPresenter.updateName(homeId, content);
                editBottomDialog.dismiss();
            } else {
                updateName(updateName, true, editBottomDialog);
            }
        });
        editBottomDialog.show(this);
    }

    /**
     * 房间/区域
     */
    @OnClick(R.id.rlRoom)
    void onClickRoom() {
        Intent intent = new Intent(this, RoomAreaActivity.class);
        intent.putExtra(IntentConstant.IS_BIND_SA, isBindSa);
        intent.putExtra(IntentConstant.ID, mLocalId);
        intent.putExtra(IntentConstant.USER_ID, userId);
        startActivity(intent);
    }

    /**
     * 删除/退出家庭
     */
    @OnClick(R.id.tvQuit)
    void onClickQuit() {
        kind = 3;
        int homeId = getHomeId();
        centerAlertDialog = CenterAlertDialog.newInstance(
                isCreator ? getResources().getString(R.string.common_confirm_del) : getResources().getString(R.string.common_confirm_exit),
                isCreator ? getResources().getString(R.string.mine_room_del_tip) : getResources().getString(R.string.mine_room_exit_tip), true);
        centerAlertDialog.setConfirmListener(() -> {
            if (isCreator) {  // 自己创建  暂时注释
                if (isBindSa && isSAEnvironment() || (UserUtils.isLogin() && mHomeBean.getId() > 0)) {
                    mPresenter.delHomeCompany(homeId);
                } else {
                    removeLocal(true);
                }
            } else {
                mPresenter.exitHomeCompany(homeId, userId);
            }
        });
        centerAlertDialog.show(this);
    }

    /**
     * 获取当前家庭的id
     *
     * @return
     */
    private int getHomeId() {
        if (isBindSa) {
            return 1;
        } else {
            return mHomeBean.getId();
        }
    }

    /**
     * 二请码
     */
    @OnClick(R.id.rlCode)
    void onClickCode() {
        kind = 2;
        writeStorageTask();
    }

    /**
     * 加载家庭信息
     */
    private void loadData() {
        UiUtil.starThread(() -> {
            HomeCompanyBean homeCompanyBean = dbManager.queryHomeCompanyById(mLocalId);
            if (homeCompanyBean != null) {
                UiUtil.runInMainThread(() -> {
                    name = homeCompanyBean.getName();
                    tvName.setText(homeCompanyBean.getName());
                    tvRoom.setText(homeCompanyBean.getRoomAreaCount() + "");
                });
            }
        });
    }

    /**
     * 删除本地数据
     */
    private void removeLocal(boolean showTips) {
        UiUtil.starThread(() -> {
            long count = dbManager.removeFamily(mLocalId);
            List<HomeCompanyBean> homeList = dbManager.queryHomeCompanyList();
            LogUtil.e("removeLocal=removeLocal=" + count);
            UiUtil.runInMainThread(() -> {
                isExitFamily = true;
                if (count > 0) {
                    if (CollectionUtil.isEmpty(homeList)) {//删除最后一个，创建一个家庭
                        createFamily();
                    } else {
                        if (showTips) {
                            ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
                        }
//                        EventBus.getDefault().post(new RefreshHome());

                        finish();
                    }
                } else {
                    if (showTips) {
                        ToastUtil.show(UiUtil.getString(R.string.mine_remove_fail));
                    }
                    if (UserUtils.isLogin()) {
                        finish();
                    }
                }
                closeDialog();
            });
        });
    }

    /**
     * 删除最后一个需要创建一个家庭
     */
    private void createFamily() {
        UiUtil.starThread(() -> {
            HomeCompanyBean homeCompanyBean = new HomeCompanyBean(1, getResources().getString(R.string.mine_home));
            dbManager.insertHomeCompany(homeCompanyBean, null);
            UiUtil.runInMainThread(() -> {
                switchToActivity(MainActivity.class);
                finish();
            });
        });
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d("TAG", "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void hasPermissionTodo() {
        super.hasPermissionTodo();
        if (isLoadRole) {
            showRoleDialog();
        } else {
            isLoadRole = true;
            mPresenter.getRoleList();
        }
    }

    /**
     * 角色列表弹窗
     */
    private void showRoleDialog() {
        if (mListBottomDialog == null) {
            String title = UiUtil.getString(R.string.mine_invite_code);
            String tip = UiUtil.getString(R.string.mine_invite_code_tip);
            String strTodo = UiUtil.getString(R.string.mine_invite_code_generate);
            mListBottomDialog = ListBottomDialog.newInstance(title, tip, strTodo, true, roleData);
            mListBottomDialog.setClickTodoListener(data -> {
                List<Integer> ids = new ArrayList<>();
                for (ListBottomBean bottomBean : data) {
                    ids.add(bottomBean.getId());
                }
                InvitationCodePost invitationCodePost = new InvitationCodePost();
                invitationCodePost.setArea_id(1);
                invitationCodePost.setRole_ids(ids);
                String body = new Gson().toJson(invitationCodePost);
                mPresenter.generateCode(1, body);
                mListBottomDialog.dismiss();
            });
        }
        mListBottomDialog.setData(roleData);
        if (mListBottomDialog != null && !mListBottomDialog.isShowing()) {
            mListBottomDialog.show(this);
        }
    }

    /**
     * 获取数据
     *
     * @param homeCompanyBean
     */
    @SuppressLint("StringFormatMatches")
    @Override
    public void getDataSuccess(HomeCompanyBean homeCompanyBean) {
        if (homeCompanyBean != null) {
            name = homeCompanyBean.getName();
            tvName.setText(homeCompanyBean.getName());
            tvRoom.setText(homeCompanyBean.getLocation_count() + "");
            updateName(homeCompanyBean.getName(), true, null);
        }
    }

    /**
     * 成员列表
     *
     * @param membersBean
     */
    @SuppressLint("StringFormatMatches")
    @Override
    public void getMembersData(MembersBean membersBean) {
        if (membersBean != null) {
            isCreator = membersBean.isIs_owner();
            tvQuit.setVisibility(View.VISIBLE);
            tvQuit.setText(membersBean.isIs_owner() ? getResources().getString(R.string.mine_remove) : getResources().getString(R.string.mine_quit_family));
            if (CollectionUtil.isNotEmpty(membersBean.getUsers())) {
                memberAdapter.setNewData(membersBean.getUsers());
                tvMember.setText(String.format(getResources().getString(R.string.mine_mine_member_count), membersBean.getUsers().size()));
                llMember.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 修改家庭名称
     */
    @Override
    public void updateNameSuccess() {
        ToastUtil.show(getResources().getString(R.string.mine_update_success));
        tvName.setText(updateName);
        updateName(updateName, true, null);
    }

    /**
     * 获取用户权限
     */
    @Override
    public void getPermissionsSuccess(PermissionBean permissionBean) {
        if (permissionBean != null) {
            PermissionBean.PermissionsBean permissionsBean = permissionBean.getPermissions();
            rlCode.setVisibility(permissionsBean.isGet_area_invite_code() ? View.VISIBLE : View.GONE);
            updateNamePermission = permissionsBean.isUpdate_area_name();
        }
    }

    /**
     * 退出家庭成功
     */
    @Override
    public void exitHomeCompanySuccess() {
        isExitFamily = true;
        ToastUtil.show(getResources().getString(R.string.mine_quit_success));
        removeLocal(false);
    }

    @Override
    public void delHomeCompanySuccess() {
        isExitFamily = true;
        ToastUtil.show(getResources().getString(R.string.mine_have_removed));
        closeDialog();
        removeLocal(false);
    }

    /**
     * 角色列表
     *
     * @param rolesBean
     */
    @Override
    public void getRoleListSuccess(RolesBean rolesBean) {
        if (rolesBean != null) {
            roleData.clear();
            for (RolesBean.RoleBean roleBean : rolesBean.getRoles()) {
                if (roleBean.getId() != -1) {
                    roleData.add(new ListBottomBean(roleBean.getId(), roleBean.getName()));
                }
            }
            showRoleDialog();
        }
    }

    /**
     * 生成二维码
     *
     * @param invitationCodeBean
     */
    @Override
    public void generateCodeSuccess(InvitationCodeBean invitationCodeBean) {
        if (invitationCodeBean != null) {
            String url = !TextUtils.isEmpty(saUrl) ? saUrl : HttpUrlConfig.baseUrl;
            GenerateCodeJson generateCodeJson = new GenerateCodeJson(invitationCodeBean.getQr_code(), url, mHomeBean.getId(), name);
            String json = GsonConverter.getGson().toJson(generateCodeJson);
            UiUtil.starThread(() -> {
                Bitmap bitmap = CodeUtils.createQRCode(json, UiUtil.dip2px(167));
                UiUtil.runInMainThread(() -> {
                    if (bitmap != null) {
                        qrCodeDialog = new QRCodeDialog(bitmap, myHandler, userName, name);
                        qrCodeDialog.show(HCDetailActivity.this);
                    } else {
                        ToastUtil.show(getResources().getString(R.string.mine_qrcode_failed));
                    }
                });
            });
        }
    }

    @Override
    public void getMemberDetailSuccess(MemberDetailBean memberDetailBean) {
        if (memberDetailBean != null) {
            userName = memberDetailBean.getNickname();
        }
    }

    /**
     * 修改昵称
     *
     * @param content
     * @param refresh
     * @param bottomDialog
     */
    private void updateName(String content, boolean refresh, EditBottomDialog bottomDialog) {
        UiUtil.starThread(() -> {
            int count = dbManager.updateHomeCompany(mLocalId, content);
            UiUtil.runInMainThread(() -> {
                if (count > 0) {
                    tvName.setText(content);
                    if (!refresh)
                        ToastUtil.show(UiUtil.getString(R.string.mine_save_success));
                } else {
                    if (!refresh)
                        ToastUtil.show(UiUtil.getString(R.string.mine_save_fail));
                }
                if (bottomDialog != null) {
                    bottomDialog.dismiss();
                }
                if ((mAreaId == mHomeBean.getId() && UserUtils.isLogin()) || mCurrentHomeLocalId == mHomeBean.getLocalId())
                    EventBus.getDefault().post(new RefreshHomeList(content));
            });
        });
    }

    /**
     * 访问接口失败
     *
     * @param msg
     */
    @Override
    public void getFail(int errorCode, String msg) {
        if (kind == 2) {
            showRoleDialog();
        } else if (kind == 3) {
            ToastUtil.show(msg);
            closeDialog();
        } else {
            ToastUtil.show(msg);
        }
    }

    private static class MyHandler extends Handler {

        WeakReference<Activity> activityWeakReference;

        public MyHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String picFile = (String) msg.obj;
            String[] split = picFile.split("/");
            String fileName = split[split.length - 1];
            try {
                MediaStore.Images.Media.insertImage(activityWeakReference.get().getApplicationContext().getContentResolver(),
                        picFile, fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            activityWeakReference.get().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + picFile)));
            ToastUtil.show(activityWeakReference.get().getResources().getString(R.string.mine_save_success));
        }
    }

    /**
     * 关闭弹窗
     */
    private void closeDialog() {
        if (centerAlertDialog != null) {
            centerAlertDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isExitFamily){  // 退出/删除
            if (mCurrentHomeLocalId == mLocalId) {// 如果该家庭是外面选中的家庭且做了删除或退出操作，外面选中的家庭置空
                CurrentHome = null;
                HomeFragment.homeLocalId = 0;
            }
            EventBus.getDefault().post(new RefreshHome());  // 通知首页刷新数据
        }else {
            CurrentHome = mTempHomeBean;
            if (CurrentHome != null && CurrentHome.getLocalId() == mLocalId && !CurrentHome.isIs_bind_sa() && !UserUtils.isLogin() && !TextUtils.isEmpty(updateName)) {
                CurrentHome.setName(updateName);
            }
        }
    }
}