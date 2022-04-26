package com.yctc.zhiting.bean;

import static com.yctc.zhiting.config.Constant.CurrentHome;

import android.text.TextUtils;

import androidx.annotation.DrawableRes;

import com.app.main.framework.baseutil.UiUtil;
import com.yctc.zhiting.R;
import com.yctc.zhiting.config.Constant;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

public enum MineFunctionBean {
    HOME_COMPANY(R.drawable.icon_mine_home, UiUtil.getString(R.string.mine_home_company_2), true),
    CRM_SYSTEM(R.drawable.icon_mine_crm, UiUtil.getResources().getString(R.string.mine_crm_system), true),
    SUPPORT_BRAND(R.drawable.icon_mine_brand, UiUtil.getString(R.string.mine_brand), true),
    THIRD_PARTY(R.drawable.icon_mine_third_party, UiUtil.getString(R.string.mine_third_party), true),
    LANGUAGE(R.drawable.icon_mine_language, UiUtil.getString(R.string.mine_language), false),
    PROFESSIONAL(R.drawable.icon_mine_professional, UiUtil.getString(R.string.mine_professional), true),
    EXPERIENCE_CENTER(R.drawable.icon_experience_center, UiUtil.getString(R.string.experience_center), true),
    ABOUT_US(R.drawable.icon_about_us, UiUtil.getString(R.string.mine_about_us), true),
    SCM_SYSTEM(R.drawable.icon_supply_chain, UiUtil.getString(R.string.mine_supply_chain), true),
    FEEDBACK(R.drawable.icon_mine_feedback, UiUtil.getString(R.string.mine_feedback), true),
    ;

    @DrawableRes
    private int logo;
    private String name;
    private boolean enable;

    MineFunctionBean(int logo, String name) {
        this.logo = logo;
        this.name = name;
    }

    MineFunctionBean(int logo, String name, boolean enable) {
        this.logo = logo;
        this.name = name;
        this.enable = enable;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public static List<MineFunctionBean> getData() {
        List<MineFunctionBean> data = new ArrayList<>();
        data.add(HOME_COMPANY);
        data.add(SUPPORT_BRAND);
        data.add(THIRD_PARTY);
        if (CurrentHome != null && !TextUtils.isEmpty(CurrentHome.getSa_id())){
            data.add(PROFESSIONAL);
        }
        data.add(EXPERIENCE_CENTER);
        if (UserUtils.isLogin()) {
            data.add(FEEDBACK);
        }
        data.add(ABOUT_US);
        return data;
    }
}
