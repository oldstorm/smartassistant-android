package com.yctc.zhiting.activity.model;

import android.text.TextUtils;

import com.app.main.framework.httputil.HTTPCaller;
import com.app.main.framework.httputil.Header;
import com.app.main.framework.httputil.RequestDataCallback;
import com.app.main.framework.httputil.comfig.HttpConfig;
import com.yctc.zhiting.activity.contract.ScanContract;
import com.yctc.zhiting.config.HttpUrlConfig;
import com.yctc.zhiting.config.HttpUrlParams;
import com.yctc.zhiting.entity.GenerateCodeJson;
import com.yctc.zhiting.entity.home.SynPost;
import com.yctc.zhiting.entity.mine.IdBean;
import com.yctc.zhiting.entity.mine.InvitationCheckBean;
import com.yctc.zhiting.request.BindCloudRequest;
import com.yctc.zhiting.utils.AllRequestUtil;
import com.yctc.zhiting.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加设备
 */
public class ScanModel implements ScanContract.Model {

    @Override
    public void invitationCheck(String body, GenerateCodeJson qrCode, RequestDataCallback<InvitationCheckBean> callback) {
        Header[] headers = getHeaders(qrCode);
        String requestUrl = HttpUrlConfig.getInvitationCheck();
        if (UserUtils.isLogin() && qrCode.getArea_id() > 0)
            requestUrl = HttpUrlConfig.baseSCUrl + HttpUrlParams.invitationCheck;
        HTTPCaller.getInstance().post(InvitationCheckBean.class, requestUrl, headers, body, callback, false);
    }

    @Override
    public void createHomeSC(SynPost.AreaBean request, RequestDataCallback<IdBean> callback) {
        HTTPCaller.getInstance().post(IdBean.class, HttpUrlConfig.getScAreas(), request, callback);
    }

    @Override
    public void bindCloudSC(BindCloudRequest request, RequestDataCallback<Object> callback) {
        HTTPCaller.getInstance().post(Object.class, HttpUrlConfig.getBindCloud(), request, callback);
    }

    /**
     * 获取header
     *
     * @return
     */
    private Header[] getHeaders(GenerateCodeJson qrCode) {
        if (qrCode == null) return null;
        List<Header> headers = new ArrayList<>();
        if (!TextUtils.isEmpty(qrCode.getSaToken())) {
            headers.add(new Header(HttpConfig.TOKEN_KEY, qrCode.getSaToken()));
        }
        //如果是云家庭并且登陆，添加area_id
        if (qrCode.getArea_id() > 0 && UserUtils.isLogin()) {
            headers.add(new Header(HttpConfig.AREA_ID, qrCode.getArea_id() + ""));
        }
        return headers.toArray(new Header[headers.size()]);
    }
}
