package com.yctc.zhiting.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseview.MVPBaseFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yctc.zhiting.R;
import com.yctc.zhiting.adapter.DLAuthWayAdapter;
import com.yctc.zhiting.dialog.CenterAlertDialog;
import com.yctc.zhiting.fragment.contract.DLAuthWayFragmentContract;
import com.yctc.zhiting.fragment.presenter.DLAuthWayFragmentPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 验证方式
 */
public class DLAuthWayFragment extends MVPBaseFragment<DLAuthWayFragmentContract.View, DLAuthWayFragmentPresenter> implements DLAuthWayFragmentContract.View {

    @BindView(R.id.rvAuth)
    RecyclerView rvAuth;

    private DLAuthWayAdapter mDLAuthWayAdapter;
    private CenterAlertDialog mRemoveWayDialog;

    private int mType;

    public static DLAuthWayFragment getInstance(int type) {
        DLAuthWayFragment dlAuthWayFragment = new DLAuthWayFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        dlAuthWayFragment.setArguments(bundle);
        return dlAuthWayFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dl_auth_way;
    }

    @Override
    protected void initUI() {
        super.initUI();
        mType = getArguments().getInt("type");
        initRv();
    }

    /**
     *  初始化列表
     */
    private void initRv() {
        rvAuth.setLayoutManager(new LinearLayoutManager(mActivity));
        mDLAuthWayAdapter = new DLAuthWayAdapter();
        rvAuth.setAdapter(mDLAuthWayAdapter);
        List<String> data = new ArrayList<>();
        for (int i=0; i<4; i++) {
            data.add("");
        }
        mDLAuthWayAdapter.setNewData(data);
        mDLAuthWayAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int viewId = view.getId();
                switch (viewId) {
                    case R.id.tvBind:

                        break;

                    case R.id.tvRemove:
                        showRemoveDialog();
                        break;
                }
            }
        });
    }

    /**
     * 删除验证方式确认弹窗
     */
    private void showRemoveDialog() {
        if (mRemoveWayDialog == null) {
            mRemoveWayDialog = CenterAlertDialog.newInstance(UiUtil.getString(R.string.home_dl_remove), UiUtil.getString(R.string.home_dl_remove_ask),
                    UiUtil.getString(R.string.cancel), UiUtil.getString(R.string.home_dl_remove));
        }
        if (mRemoveWayDialog!=null && !mRemoveWayDialog.isShowing()) {
            mRemoveWayDialog.show(this);
        }
    }
}
