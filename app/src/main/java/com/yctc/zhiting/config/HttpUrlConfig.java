package com.yctc.zhiting.config;

import android.text.TextUtils;

import com.app.main.framework.httputil.comfig.HttpConfig;
import com.yctc.zhiting.utils.HomeUtil;
import com.yctc.zhiting.utils.UserUtils;


public class HttpUrlConfig {
    //public static final String HTTP = BuildConfig.HTTP;
    //public static final String IP = BuildConfig.IP;
    //public static String baseUrl = "http://yapi.yctc.tech/mock/61/";
    //public static String baseUrl = "http://192.168.0.112:8088/";//马健
    //public static String baseUrl = "http://192.168.0.188:8088/";//伟杰

    //public static String baseUrl = "http://192.168.0.182:8088/";//巫力宏
    //public static String baseSCUrl = "http://192.168.0.182:9097/";//巫力宏

    //public static String baseSAUrl = "http://192.168.0.84:8088/";//测试服务SA1 ip
    //public static String baseUrl = "http://192.168.0.82:8088/";//测试服务SA2 ip
    //public static String baseSAUrl = "http://sa.zhitingtech.com/";//测试服务器SA1 域名

    public static String baseUrl = "http://192.168.0.123:9020/";//测试服务器SA
    public static String baseSCUrl = "https://sc.zhitingtech.com/api/";//测试服务SC
    public static String baseSAUrl = baseUrl + "api/";//测试服务器SA api

    //只走SA
    private static String getSAServerUrl(String url) {
        if (!TextUtils.isEmpty(HomeUtil.getSaToken())) {
            HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken());
        }
        return baseSAUrl + url;
    }

    //只走SC
    private static String getSCServerUrl(String url) {
        int homeId = HomeUtil.getHomeId();
        if (UserUtils.isLogin() && homeId > 0) {
            HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(homeId));
        } else {//移除
            HttpConfig.clearHear(HttpConfig.AREA_ID);
        }
        if (!TextUtils.isEmpty(HomeUtil.getSaToken())) {
            HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken());
        } else {//移除
            HttpConfig.clearHear(HttpConfig.TOKEN_KEY);
        }
        return baseSCUrl + url;
    }

    public static String getSCServerNeedBindIdUrl(String url){
        int homeId = HomeUtil.getHomeId();
        if (UserUtils.isLogin() && HomeUtil.isBindSA() && homeId > 0) {
            HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(homeId));
        } else {//移除
            HttpConfig.clearHear(HttpConfig.AREA_ID);
        }
        if (!TextUtils.isEmpty(HomeUtil.getSaToken())) {
            HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken());
        } else {//移除
            HttpConfig.clearHear(HttpConfig.TOKEN_KEY);
        }
        return baseSCUrl + url;
    }

    //根据条件判断走SA/SC
    private static String getACServerUrl(String url) {
//        int homeId = HomeUtil.getHomeId();
//        if (UserUtils.isLogin() && homeId > 0) {//登陆&不在SA环境
//            return getSCServerUrl(url);
//        } else {//没有登陆SC
//            return getSAServerUrl(url);
//        }
        if (HomeUtil.isSAEnvironment()) {
            return getSAServerUrl(url);
        } else {
            if (UserUtils.isLogin() && HomeUtil.getHomeId() > 0) {//登陆&不在SA环境
                return getSCServerUrl(url);
            } else {//没有登陆SC
                return getSAServerUrl(url);
            }
        }
    }

    /**
     * 没有header sc
     * @param url
     * @return
     */
    private static String getSCServerUrlWithoutHead(String url){
        return baseSCUrl + url;
    }

    /**
     * 获取品牌列表
     *
     * @return
     */
    public static String getBrandListUrl() {
        return getACServerUrl(HttpUrlParams.brand);
    }

    /**
     * 添加设备
     *
     * @return
     */
    public static String getAddDeviceUrl() {
        return getACServerUrl(HttpUrlParams.devices);
    }

    /**
     * 获取设备列表
     *
     * @return
     */
    public static String getDeviceList() {
        return getACServerUrl(HttpUrlParams.devices);
    }

    /**
     * 添加灯、插座设备
     *
     * @return
     */
    public static String getAddLightDeviceUrl() {
        return getACServerUrl(HttpUrlParams.devices);
    }

    /**
     * 家庭/物业列表
     *
     * @return
     */
    public static String getAreasUrl() {
        return getACServerUrl(HttpUrlParams.areas);
    }

    /**
     * 房间/位置默认勾选列表
     *
     * @return
     */
    public static String getLocation_tmpl() {
        return getSAServerUrl(HttpUrlParams.location_tmpl);
    }

    /**
     * 房间/成员列表
     *
     * @return
     */
    public static String getUsers() {
        return getACServerUrl(HttpUrlParams.users);
    }


    /**
     * 获取用户权限
     *
     * @return
     */
    public static String getPermissions(int id) {
        return getSAServerUrl(HttpUrlParams.users + "/" + id + "/" + HttpUrlParams.permissions);
    }

    public static String getPermissions1(int id) {
        return getACServerUrl(HttpUrlParams.users + "/" + id + "/" + HttpUrlParams.permissions);
    }

    /**
     * 退出家庭
     *
     * @param id      家庭id
     * @param user_id 用户id
     * @return
     */
    public static String getExitHomeCompany(int id, int user_id) {
        return getACServerUrl(HttpUrlParams.areas + "/" + id + "/" + HttpUrlParams.users + "/" + user_id);
    }

    /**
     * 删除家庭
     *
     * @param id 家庭id
     * @return
     */
    public static String getDelHomeCompany(int id) {
        return getACServerUrl(HttpUrlParams.areas + "/" + id);
    }

    /**
     * 数据同步
     *
     * @return
     */
    public static String getSync() {
        return getSAServerUrl(HttpUrlParams.sync);
    }


    /**
     * 房间/区域
     *
     * @return
     */
    public static String getLocation() {
        return getACServerUrl(HttpUrlParams.locations);
    }

    /**
     * 角色列表
     *
     * @return
     */
    public static String getRoles() {
        return getACServerUrl(HttpUrlParams.roles);
    }

    /**
     * 获取邀请二维码
     *
     * @param id
     * @return
     */
    public static String getInvitationCode(int id) {
        return getACServerUrl(HttpUrlParams.users + "/" + id + HttpUrlParams.invitationCode);
    }

    /**
     * 扫描邀请二维码
     *
     * @return
     */
    public static String getInvitationCheck() {
        return getSAServerUrl(HttpUrlParams.invitationCheck);
    }

    /**
     * 检测SA绑定情况
     *
     * @return
     */
    public static String getCheckBindSA() {
        return getSAServerUrl(HttpUrlParams.checkBindSa);
    }

    /**
     * 房间/区域列表
     *
     * @return
     */
    public static String getLocations() {
        return getACServerUrl(HttpUrlParams.locations);
    }

    /**
     * 修改设备
     *
     * @param id
     * @return
     */
    public static String getUpdateDeviceName(String id) {
        return getACServerUrl(HttpUrlParams.devices + "/" + id);
    }

    /**
     * 场景列表
     *
     * @return
     */
    public static String getScene() {
        return getACServerUrl(HttpUrlParams.scenes);
    }

    /**
     * 场景的执行
     *
     * @param id
     * @return
     */
    public static String getExecute(int id) {
        return getACServerUrl(HttpUrlParams.scenes + "/" + id + "/" + HttpUrlParams.execute);
    }

    /**
     * 场景日志
     *
     * @return
     */
    public static String getSceneLog() {
        return getACServerUrl(HttpUrlParams.scene_logs);
    }

    /**
     * 插件详情
     *
     * @return
     */
    public static String getPluginsDetail() {
        return getACServerUrl(HttpUrlParams.plugins);
    }

    /**
     * 获取验证码
     *
     * @return
     */
    public static String getCaptcha() {
        return getSCServerUrl(HttpUrlParams.captcha);
//        return getSCServerNoAreaIdUrl(HttpUrlParams.captcha);
    }

    /**
     * 获取验证码
     *
     * @return
     */
    public static String getRegister() {
        return getSCServerUrl(HttpUrlParams.users);
    }

    /**
     * 登录
     *
     * @return
     */
    public static String getLogin() {
        return getSCServerUrl(HttpUrlParams.sessions_login);
    }

    /**
     * 云端家庭/物业列表
     *
     * @return
     */
    public static String getSCAreasUrl() {
        return getSCServerUrl(HttpUrlParams.areas);
    }

    /**
     * 登出
     *
     * @return
     */
    public static String getLogout() {
        return getSCServerUrl(HttpUrlParams.sessions_logout);
    }

    /**
     * 云用户
     *
     * @return
     */
    public static String getSCUsers() {
        return getSCServerNeedBindIdUrl(HttpUrlParams.users);
    }

    /**
     * 云用户 没有header
     * @return
     */
    public static String getSCUsersWithoutHeader(){
        HttpConfig.clearHeader();
        return getSCServerUrlWithoutHead(HttpUrlParams.users);
    }

    /**
     * 绑定云端
     *
     * @return
     */
    public static String getBindCloud() {
        return getSAServerUrl(HttpUrlParams.cloud_bind);
    }

    /**
     * 云端家庭
     *
     * @return
     */
    public static String getScAreas() {
        return getSCServerUrl(HttpUrlParams.areas);
    }

    /**
     * 获取 SCOPE Token（sa）
     *
     * @return
     */
    public static String getScopes() {
        if (!TextUtils.isEmpty(HomeUtil.getSaToken())) {
            HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken());
        }
        return getSAServerUrl(HttpUrlParams.scopes);
    }

    /**
     * 获取 SCOPE Token（sa）
     *
     * @return
     */
    public static String getScopesToken() {
        return getSAServerUrl(HttpUrlParams.scopes_token);
    }

    /**
     * 转移拥有者
     * @param userId
     * @return
     */
    public static String getTransferOwner(int userId){
        return getACServerUrl(HttpUrlParams.users + "/"+userId+"/"+HttpUrlParams.transfer_owner);
    }

}
