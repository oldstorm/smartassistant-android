package com.yctc.zhiting.activity;


import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.baseview.MVPBaseActivity;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.yctc.zhiting.R;
import com.yctc.zhiting.activity.contract.AddCompanyContract;
import com.yctc.zhiting.activity.presenter.AddCompanyPresenter;
import com.yctc.zhiting.adapter.AddHCAdapter;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.db.DBManager;
import com.yctc.zhiting.dialog.EditBottomDialog;
import com.yctc.zhiting.entity.mine.HomeCompanyBean;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.LocationBean;
import com.yctc.zhiting.entity.mine.LocationTmpl;
import com.yctc.zhiting.entity.mine.LocationsBean;
import com.yctc.zhiting.event.RefreshHome;
import com.yctc.zhiting.event.RefreshHomeEvent;
import com.yctc.zhiting.request.AddHCRequest;
import com.yctc.zhiting.utils.CollectionUtil;
import com.yctc.zhiting.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 添加公司
 */
public class AddCompanyActivity extends MVPBaseActivity<AddCompanyContract.View, AddCompanyPresenter> implements AddCompanyContract.View {

    @BindView(R.id.tvSave)
    TextView tvSave;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.rvDepartment)
    RecyclerView mRvDepartment;

    private WeakReference<Context> mContext;
    private DBManager dbManager;
    private AddHCAdapter addHCAdapter;

    private List<LocationTmpl> data = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_company;
    }

    @Override
    protected boolean isLoadTitleBar() {
        return false;
    }

    @Override
    protected void initUI() {
        super.initUI();
        mContext = new WeakReference<>(this);
        dbManager = DBManager.getInstance(mContext.get());
        addHCAdapter = new AddHCAdapter();
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_1), true));
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_2), true));
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_3), true));
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_4), true));
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_5), true));
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_6), false));
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_7), false));
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_8), false));
        data.add(new LocationTmpl(getResources().getString(R.string.default_department_9), false));
        addHCAdapter.setNewData(data);
        mRvDepartment.setLayoutManager(new LinearLayoutManager(this));
        mRvDepartment.setAdapter(addHCAdapter);
        addHCAdapter.setOnItemClickListener((adapter, view, position) -> {
            LocationTmpl locationTmpl = addHCAdapter.getItem(position);
            locationTmpl.setChosen(!locationTmpl.isChosen());
            addHCAdapter.notifyItemChanged(position);
        });
    }

    @OnTextChanged(R.id.etName)
    void editTextChange() {
        tvSave.setEnabled(!TextUtils.isEmpty(etName.getText().toString().trim()));
    }

    @OnClick(R.id.ivBack)
    void onClickBack() {
        finish();
    }


    /**
     * 保存
     */
    @OnClick(R.id.tvSave)
    void onClickSave() {
        if (CollectionUtil.isEmpty(getRoomAreas())){
            ToastUtil.show(UiUtil.getString(R.string.mine_please_add_one_department_at_least));
            return;
        }
        if (UserUtils.isLogin()) {
            createSCHome();
        } else {
            createLocalHome(0L, null);
        }
    }

    /**
     * 添加部门
     */
    @OnClick(R.id.llAdd)
    void onClickAdd() {
        EditBottomDialog editBottomDialog = EditBottomDialog.newInstance(getResources().getString(R.string.mine_department_name), getResources().getString(R.string.mine_input_department_name), null, 1);
        editBottomDialog.setClickSaveListener(content -> {
            for (LocationTmpl roomAreaBean : addHCAdapter.getData()) {
                if (content.equals(roomAreaBean.getName())) {
                    ToastUtil.show(getResources().getString(R.string.mine_department_duplicate));
                    return;
                }
            }
            addHCAdapter.getData().add(new LocationTmpl(content, true));
            addHCAdapter.notifyDataSetChanged();
            mRvDepartment.scrollToPosition(addHCAdapter.getData().size() - 1);
            editBottomDialog.dismiss();
        });
        editBottomDialog.show(this);
    }

    /**
     * 创建一个本地家庭
     * id 家庭id
     */
    private void createLocalHome(Long id, IdBean data) {
        UiUtil.starThread(() -> {
            String homeName = etName.getText().toString().trim();
            HomeCompanyBean homeCompanyBean = new HomeCompanyBean(homeName);
            ArrayList<LocationBean> roomAreas = getRoomAreas();
            if (id != null && id > 0) {
                homeCompanyBean.setId(id);
            }
            homeCompanyBean.setCloud_user_id(UserUtils.getCloudUserId());
            if (data != null && data.getCloud_sa_user_info() != null) {
                int saUserId = data.getCloud_sa_user_info().getId();
                String saToken = data.getCloud_sa_user_info().getToken();
                homeCompanyBean.setUser_id(saUserId);
                homeCompanyBean.setSa_user_token(saToken);
            }
            homeCompanyBean.setArea_type(Constant.COMPANY_MODE);
            dbManager.insertHomeCompany(homeCompanyBean, roomAreas, false);
            UiUtil.runInMainThread(() -> {
                EventBus.getDefault().post(new RefreshHomeEvent());
                ToastUtil.show(getResources().getString(R.string.mine_add_success));
                finish();
            });
        });
    }

    /**
     * 创建云端家庭
     */
    private void createSCHome() {
        List<LocationBean> roomList = getRoomAreas();//房间
        List<String> locations = new ArrayList<>();
        for (LocationBean locationBean : roomList){
            locations.add(locationBean.getName());
        }
        String homeName = etName.getText().toString().trim();
//        SynPost.AreaBean areaBean = new SynPost.AreaBean(homeName, locations);//家庭
        AddHCRequest addHCRequest = new AddHCRequest(homeName, Constant.COMPANY_MODE);
        addHCRequest.setDepartment_names(locations);
        HttpConfig.clearHear(HttpConfig.AREA_ID);
        HttpConfig.clearHear(HttpConfig.TOKEN_KEY);
        mPresenter.addScHome(addHCRequest);
    }

    /**
     * 获取房间集合
     *
     * @return
     */
    private ArrayList<LocationBean> getRoomAreas() {
        ArrayList<LocationBean> roomAreas = new ArrayList<>();
        int id = 0;
        for (LocationTmpl locationTmpl : addHCAdapter.getData()) {
            if (locationTmpl.isChosen()) {
                id++;
                LocationBean roomAreaBean = new LocationBean(id, locationTmpl.getName(), id);
                roomAreaBean.setLocationId(-1);
                roomAreaBean.setSa_user_token(null);
                roomAreas.add(roomAreaBean);
            }
        }
        return roomAreas;
    }

    @Override
    public void getDataSuccess(LocationsBean locationsBean) {

    }

    @Override
    public void getDataFail(String msg) {

    }

    @Override
    public void onCreateSCHomeSuccess(IdBean data) {

    }

    @Override
    public void onCreateSCHomeFail(String msg) {

    }

    @Override
    public void addScHomeSuccess(IdBean idBean) {
        if (idBean != null) {
            createLocalHome(idBean.getId(), idBean);
        }
    }

    @Override
    public void addScHomeFail(int errorCode, String msg) {
        ToastUtil.show(msg);
    }
}