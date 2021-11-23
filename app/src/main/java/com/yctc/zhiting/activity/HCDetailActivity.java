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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.SpConstant;
import com.app.main.framework.baseutil.SpUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.gsonutils.GsonConverter;
import com.app.main.framework.httputil.NameValuePair;
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
import com.yctc.zhiting.dialog.RemovedTipsDialog;
import com.yctc.zhiting.dialog.VerificationCodeDialog;
import com.yctc.zhiting.entity.FindSATokenBean;
import com.yctc.zhiting.entity.GenerateCodeJson;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCodeBean;
import com.yctc.zhiting.entity.mine.InvitationCodePost;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.MemberDetailBean;
import com.yctc.zhiting.entity.mine.MembersBean;
import com.yctc.zhiting.entity.mine.PermissionBean;
import com.yctc.zhiting.entity.mine.RolesBean;
import com.yctc.zhiting.event.DeviceDataEvent;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.RefreshHomeList;
import com.yctc.zhiting.fragment.HomeFragment;
import com.yctc.zhiting.request.AddHCRequest;
import com.yctc.zhiting.request.BooleanRequest;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.HomeUtil;
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
    @BindView(R.id.rlInvalid)
    RelativeLayout rlInvalid;
    @BindView(R.id.tvTips)
    TextView tvTips;
    @BindView(R.id.ivGo)
    ImageView ivGo;
    @BindView(R.id.llHome)
    LinearLayout llHome;
    @BindView(R.id.rlVerificationCode)
    RelativeLayout rlVerificationCode;

    private final int ROOM_ACT_REQUEST_CODE = 100;

    private MemberAdapter memberAdapter;
    private MyHandler myHandler;
    private QRCodeDialog qrCodeDialog;

    private WeakReference<Context> mContext;
    private DBManager dbManager;

    /**
     * 1.家庭详情
     * 2.角色列表
     * 3.删除
     * 4.修改名称
     */
    private int kind;
    private int userId;
    private long mLocalId;// 家庭本地id
    private long mCurrentHomeLocalId;
    private long mAreaId;//CurrentHome id
    private boolean invalid; // 无效的SAToken
    private boolean isBindSa;//是否绑定sa
    private boolean isCreator;// 是否是创建者
    private boolean updateNamePermission;// 是否有修改家庭名称权限
    private boolean isLoadRole;//是否已经请求过角色列表
    private boolean isExitFamily;

    private String updateName;// 修改的名称
    private String name;
    private String userName;
    private String saUrl;
    private List<ListBottomBean> roleData = new ArrayList<>();
    private ListBottomDialog mListBottomDialog;
    private CenterAlertDialog centerAlertDialog;
    private HomeCompanyBean mHomeBean;
    private HomeCompanyBean mTempHomeBean;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_h_c_detail;
    }

    @Override
    protected void onResume() {
        super.onResume();
        kind = 1;
    }

    private void initFirstData() {
        //已经绑定sa且在sa环境，或者登陆状态
        if (isBindSa || (UserUtils.isLogin() && userId > 0)) {
            checkUrl();
        } else {//否则，从本地获取
            long cloudId = mHomeBean.getId();
            if (UserUtils.isLogin() && cloudId > 0) { // 登录了
                HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(mHomeBean.getId()));
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

        HttpConfig.clearHeader();
        HttpConfig.addHeader(mHomeBean.getSa_user_token());
        HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(mHomeBean.getId()));
        SpUtil.put(SpConstant.SA_TOKEN, mHomeBean.getSa_user_token());
        SpUtil.put(SpConstant.IS_BIND_SA, mHomeBean.isIs_bind_sa());


        mLocalId = mHomeBean.getLocalId();
        userId = mHomeBean.getUser_id();
        isBindSa = mHomeBean.isIs_bind_sa();
        saUrl = mHomeBean.getSa_lan_address();

        isCreator = isBindSa ? false : true;
        myHandler = new MyHandler(this);

        memberAdapter = new MemberAdapter();
        rvMember.setLayoutManager(new LinearLayoutManager(this));
        rvMember.setAdapter(memberAdapter);

        memberAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(HCDetailActivity.this, MemberDetailActivity.class);
            intent.putExtra(IntentConstant.ID, memberAdapter.getItem(position).getUser_id());
            startActivityForResult(intent, ROOM_ACT_REQUEST_CODE);
        });
        initFirstData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ROOM_ACT_REQUEST_CODE) {
            initFirstData();
        }
    }

    /**
     * 检测地址
     */
    private void checkUrl() {
        if (isBindSa && wifiInfo != null) {
            showLoadingView();
            AllRequestUtil.checkUrl500(CurrentHome.getSa_lan_address(), new AllRequestUtil.onCheckUrlListener() {
                @Override
                public void onSuccess() {
                    LogUtil.e("checkUrl===onSuccess");
                    WifiInfo wifiInfo = Constant.wifiInfo;
                    if (!TextUtils.isEmpty(wifiInfo.getBSSID()))
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
        if (TextUtils.isEmpty(CurrentHome.getMac_address()))
            CurrentHome.setMac_address(macAddress);
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        boolean isEnabled = isBindSa && !isSAEnvironment();
        UiUtil.runInMainThread(() -> {
            if (!UserUtils.isLogin()) {
                rlName.setEnabled(!isEnabled);
                rlCode.setEnabled(!isEnabled);
                rlRoom.setEnabled(!isEnabled);
            }
            tvQuit.setVisibility(View.GONE);
            tvTips.setText(getResources().getString(R.string.home_connect_fail));
            ivGo.setVisibility(View.GONE);
            if (UserUtils.isLogin()) {
                llTips.setVisibility(View.GONE);
            } else {
                llTips.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
            }
        });
        long areaId = mHomeBean.getArea_id();
        HttpConfig.addHeader(mHomeBean.getSa_user_token());
        HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(mHomeBean.getId()));
        mPresenter.getDetail(areaId > 0 ? areaId : mHomeBean.getId());
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
     * 验证码生成
     */
    @OnClick(R.id.tvOpenCode)
    public void onClickVerificationCode() {
        mPresenter.getVerificationCode();

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
                long homeId = getHomeId();
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
        startActivityForResult(intent, ROOM_ACT_REQUEST_CODE);
    }

    /**
     * 删除/退出家庭
     */
    @OnClick(R.id.tvQuit)
    void onClickQuit() {
        kind = 3;
        long homeId = getHomeId();
        centerAlertDialog = CenterAlertDialog.newInstance(
                isCreator ? getResources().getString(R.string.common_confirm_del) : getResources().getString(R.string.common_confirm_exit),
                isCreator ? getResources().getString(R.string.mine_room_del_tip) : getResources().getString(R.string.mine_room_exit_tip), true, isCreator && isBindSa);
        centerAlertDialog.setConfirmListener(del -> {
            if (isCreator) {  // 自己创建  暂时注释
                if (isBindSa && isSAEnvironment() || (UserUtils.isLogin() && mHomeBean.getId() > 0)) {
                    BooleanRequest booleanRequest = new BooleanRequest(del);
                    mPresenter.delHomeCompany(homeId, booleanRequest.toString());
                } else {
                    removeLocal(true);
                }
            } else {
                mPresenter.exitHomeCompany(homeId, userId);
            }
        });
        centerAlertDialog.show(this);
    }

    @OnClick(R.id.llTips)
    void onClickTips() {
        if (invalid) {
            switchToActivity(FindSAGuideActivity.class);
        }
    }

    /**
     * 获取当前家庭的id
     *
     * @return
     */
    private long getHomeId() {
        if (isBindSa) {
            return mHomeBean.getArea_id();
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
                    tvQuit.setText(getResources().getString(R.string.mine_remove));
                    llHome.setVisibility(View.VISIBLE);
                    tvQuit.setVisibility(View.VISIBLE);
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
                        if (mHomeBean.getId() > 0 && !mHomeBean.isIs_bind_sa()) {//虚拟SA
                            AddHCRequest addHCRequest = new AddHCRequest(getResources().getString(R.string.mine_home), new ArrayList<>());
                            mPresenter.addScHome(addHCRequest);
                        } else {
                            HomeCompanyBean homeCompanyBean = new HomeCompanyBean(1, getResources().getString(R.string.mine_home));
                            createFamily(homeCompanyBean);
                        }
                    } else {
                        if (showTips) {
                            ToastUtil.show(UiUtil.getString(R.string.mine_remove_success));
                        }
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
    private void createFamily(HomeCompanyBean homeCompanyBean) {
        UiUtil.starThread(() -> {
            dbManager.insertHomeCompany(homeCompanyBean, null, false);
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
        } else if (isBindSa || CurrentHome.getId() > 0) {//yjj
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
                invitationCodePost.setArea_id(mHomeBean.getArea_id());
                invitationCodePost.setRole_ids(ids);
                String body = new Gson().toJson(invitationCodePost);
                mPresenter.generateCode(mHomeBean.getUser_id(), body);
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
            showInvalid(false);
            name = homeCompanyBean.getName();
            tvName.setText(homeCompanyBean.getName());
            tvRoom.setText(homeCompanyBean.getLocation_count() + "");
            updateName(homeCompanyBean.getName(), true, null);
            if (isBindSa || CurrentHome.getId() > 0) {//yjj,ios就这样
                mPresenter.getMembers();
                mPresenter.getPermissions(userId);
                mPresenter.getMemberDetail(userId);
            } else {
                tvQuit.setVisibility(View.VISIBLE);
            }
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
            tvQuit.setText(isCreator ? getResources().getString(R.string.mine_remove) : getResources().getString(R.string.mine_quit_family));
            rlVerificationCode.setVisibility(isCreator ? View.VISIBLE : View.GONE);
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
            GenerateCodeJson generateCodeJson = new GenerateCodeJson(invitationCodeBean.getQr_code(), saUrl, mHomeBean.getArea_id(), name);
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
            if (errorCode != 5012)
                ToastUtil.show(msg);
            closeDialog();
        } else {
            if (errorCode != 5012)
                ToastUtil.show(msg);
        }
    }

    /**
     * 家庭详情失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getDetailFail(int errorCode, String msg) {
        if (errorCode == 5012) {
            if (UserUtils.isLogin()) { // 用户登录SC情况
                NameValuePair nameValuePair = new NameValuePair("area_id", String.valueOf(CurrentHome.getId()));
                List<NameValuePair> requestData = new ArrayList<>();
                requestData.add(nameValuePair);
                mPresenter.getSAToken(CurrentHome.getCloud_user_id(), requestData);  // sc的用户id, sc上的家庭id
            } else {
                removeLocalFamily();
            }
        } else if (errorCode == 3002 || errorCode == 5003) {//状态码3002，提示被管理员移除家庭
            removeLocalFamily();
        }
    }

    /**
     * 通过sc找回sa的用户凭证成功
     *
     * @param findSATokenBean
     */
    @Override
    public void getSATokenSuccess(FindSATokenBean findSATokenBean) {
        if (findSATokenBean != null) {
            String saToken = findSATokenBean.getSa_token();
            if (!TextUtils.isEmpty(saToken)) {
                HomeUtil.tokenIsInvalid = false;
                CurrentHome.setSa_user_token(saToken);
                HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, saToken);
                initFirstData();
                getData();
                UiUtil.starThread(() -> dbManager.updateSATokenByLocalId(CurrentHome.getLocalId(), saToken));
            }
        }
    }

    /**
     * 通过sc找回sa的用户凭证是失败
     *
     * @param errorCode
     * @param msg
     */
    @Override
    public void getSATokenFail(int errorCode, String msg) {
        if (errorCode == 2011 || errorCode == 2010) {    //凭证获取失败，状态码2011，无权限
            invalid = true;
            showInvalid(true);
            llTips.setVisibility(View.VISIBLE);
            ivGo.setVisibility(View.VISIBLE);
            tvQuit.setVisibility(View.GONE);
            tvTips.setText(getResources().getString(R.string.home_invalid_token));
            EventBus.getDefault().post(new DeviceDataEvent(null));
        } else if (errorCode == 3002) {  //状态码3002，提示被管理员移除家庭
            removeLocalFamily();
        } else {
            ToastUtil.show(msg);
        }
    }

    @Override
    public void addScHomeSuccess(IdBean data) {
        HomeCompanyBean homeCompanyBean = new HomeCompanyBean(getResources().getString(R.string.mine_home));
        homeCompanyBean.setId(data.getId());
        homeCompanyBean.setCloud_user_id(UserUtils.getCloudUserId());
        if (data != null && data.getCloud_sa_user_info() != null) {
            int saUserId = data.getCloud_sa_user_info().getId();
            String saToken = data.getCloud_sa_user_info().getToken();
            homeCompanyBean.setUser_id(saUserId);
            homeCompanyBean.setSa_user_token(saToken);
        }
        createFamily(homeCompanyBean);
    }

    @Override
    public void addScHomeFail(int errorCode, String msg) {

    }

    @Override
    public void onVerificationCodeSuccess(String code) {
        VerificationCodeDialog dialog = VerificationCodeDialog.newInstance(code);
        dialog.show(this);
    }

    @Override
    public void onVerificationCodeFail(int errorCode, String msg) {

    }

    /**
     * 显示satoken失效
     *
     * @param show
     */
    private void showInvalid(boolean show) {
        rlInvalid.setVisibility(show ? View.VISIBLE : View.GONE);
        llHome.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    /**
     * 移除本地家庭
     */
    private void removeLocalFamily() {
        RemovedTipsDialog removedTipsDialog = new RemovedTipsDialog(CurrentHome.getName());
        removedTipsDialog.setKnowListener(() -> finish());
        removedTipsDialog.show(this);
        dbManager.removeFamily(CurrentHome.getLocalId());
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
    public void finish() {
        if (isExitFamily) {  // 退出/删除
            if (mCurrentHomeLocalId == mLocalId) {// 如果该家庭是外面选中的家庭且做了删除或退出操作，外面选中的家庭置空
                CurrentHome = null;
                HomeFragment.homeLocalId = 0;
            }
            EventBus.getDefault().post(new RefreshHome());  // 通知首页刷新数据
        } else {
            CurrentHome = mTempHomeBean;
            SpUtil.put(SpConstant.SA_TOKEN, mTempHomeBean.getSa_user_token());
            HttpConfig.addHeader(CurrentHome.getSa_user_token());
            HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(HomeUtil.getHomeId()));
            SpUtil.put(SpConstant.IS_BIND_SA, CurrentHome.isIs_bind_sa());
            if (CurrentHome != null && CurrentHome.getLocalId() == mLocalId && !CurrentHome.isIs_bind_sa() && !UserUtils.isLogin() && !TextUtils.isEmpty(updateName)) {
                CurrentHome.setName(updateName);
            }
        }
        super.finish();
    }
}