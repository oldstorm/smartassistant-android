package com.yctc.zhiting.config;

import android.text.TextUtils;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.TraceUtil;
import com.app.main.framework.config.HttpBaseUrl;
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

    //public static String baseSCUrl = "http://192.168.22.84:8082";//测试服务SC
    //public static String baseSCUrl = "http://192.168.22.110:9097";//力宏测试服务SC
    //public static String baseSAUrl = "http://192.168.22.106:37965";//测试服务器SA
    //public static String baseSCUrl = "http://192.168.22.76:9097";//测试服务SC

    public static final String API = "/api/";
    public static String baseSAUrl = HttpBaseUrl.baseSAUrl;//测试服务器SA
    public static String baseSCUrl = HttpBaseUrl.baseSCUrl;//测试服务SC
    public static String apiSCUrl = baseSCUrl + API;//测试服务SC api
    public static String apiSAUrl = baseSAUrl + API + HttpBaseUrl.VERSION;//测试服务器SA api

    //只走SA
    private static String getSAServerUrl(String url) {
        if (!TextUtils.isEmpty(HomeUtil.getSaToken())) {
            HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken());
        }
        if (url.contains(Constant.NO_AREA_ID)) {//不需要添加area_id接口
            url = url.replace(Constant.NO_AREA_ID, "");
            HttpConfig.clearHear(HttpConfig.AREA_ID);
        } else {
            long homeId = HomeUtil.getHomeId();
            HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(homeId));
        }
        HttpConfig.addAreaIdHeader(HttpConfig.TRACEPARENT, getTraceParentVal());
        return apiSAUrl + HttpBaseUrl.VERSION + url;
    }

    //只走SC
    private static String getSCServerUrl(String url) {
        LogUtil.e("getSCServerUrl=" + url);
        if (url.contains(Constant.NO_AREA_ID)) {//不需要添加area_id接口
            url = url.replace(Constant.NO_AREA_ID, "");
            HttpConfig.clearHear(HttpConfig.AREA_ID);
        } else {
            long homeId = HomeUtil.getHomeId();
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
        }
        HttpConfig.addAreaIdHeader(HttpConfig.TRACEPARENT, getTraceParentVal());
        return apiSCUrl + HttpBaseUrl.VERSION + url;
    }

    public static String getSCServerNeedBindIdUrl(String url) {
        long homeId = HomeUtil.getHomeId();
        if (UserUtils.isLogin() && HomeUtil.isHomeIdThanZero() && homeId > 0) {
            HttpConfig.addAreaIdHeader(HttpConfig.AREA_ID, String.valueOf(homeId));
        } else {//移除
            HttpConfig.clearHear(HttpConfig.AREA_ID);
        }
        if (!TextUtils.isEmpty(HomeUtil.getSaToken())) {
            HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken());
        } else {//移除
            HttpConfig.clearHear(HttpConfig.TOKEN_KEY);
        }
        HttpConfig.addAreaIdHeader(HttpConfig.TRACEPARENT, getTraceParentVal());
        return apiSCUrl + HttpBaseUrl.VERSION + url;
    }

    //根据条件判断走SA/SC
    private static String getACServerUrl(String url) {
        if (HomeUtil.isSAEnvironment()) {
            return getSAServerUrl(url);
        } else {
            if (UserUtils.isLogin() && HomeUtil.getHomeId() > 0) {//登陆&不在SA环境
                return getSCServerUrl(url);
            } else {//没有登陆SC
                HttpConfig.addAreaIdHeader(HttpConfig.TOKEN_KEY, HomeUtil.getSaToken());
                return getSAServerUrl(url);
            }
        }
    }

    /**
     * 没有header sc
     *
     * @param url
     * @return
     */
    private static String getSCServerUrlWithoutHead(String url) {
        return apiSCUrl + HttpBaseUrl.VERSION + url;
    }

    /**
     * 生成traceparent
     * @return
     */
    public static String getTraceParentVal(){
        String traceparentVal = "00-" + TraceUtil.getHexdiglc(32) + "-" + TraceUtil.getHexdiglc(16) + "-01";
        LogUtil.e("traceparentVal=========="+traceparentVal);
        return traceparentVal;
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
        return getACServerUrl(HttpUrlParams.areas + Constant.NO_AREA_ID);
    }

    /**
     * 家庭/物业详情
     *
     * @return
     */
    public static String getAreaDetailUrl() {
        return getACServerUrl(HttpUrlParams.areas);
    }

    /**
     * 房间/位置默认勾选列表
     *
     * @return
     */
    public static String getLocation_tmpl() {
        return getACServerUrl(HttpUrlParams.location_tmpl);
    }

    /**
     * 用户列表
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
    public static String getExitHomeCompany(long id, int user_id) {
        return getACServerUrl(HttpUrlParams.areas + "/" + id + "/" + HttpUrlParams.users + "/" + user_id);
    }

    /**
     * 删除家庭
     *
     * @param id 家庭id
     * @return
     */
    public static String getDelHomeCompany(long id) {
        return getACServerUrl(HttpUrlParams.areas + "/" + id + Constant.NO_AREA_ID);
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
     *
     * @return
     */
    public static String getSCUsersWithoutHeader() {
        HttpConfig.clearHeader();
        return getSCServerUrlWithoutHead(HttpUrlParams.users);
    }

    /**
     * sa用户
     *
     * @return
     */
    public static String getSAUsers() {
        return getSAServerUrl(HttpUrlParams.users);
    }

    /**
     * sa用户
     *
     * @return
     */
    public static String getACUsers() {
        return getACServerUrl(HttpUrlParams.users);
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
     * 云端家庭
     *
     * @return
     */
    public static String getScAreasNoHeader() {
        return getSCServerUrlWithoutHead(HttpUrlParams.areas);
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
        return getACServerUrl(HttpUrlParams.scopes);
    }

    /**
     * 获取 SCOPE Token（sa）
     *
     * @return
     */
    public static String getScopesToken() {
        return getACServerUrl(HttpUrlParams.scopes_token);
    }

    /**
     * 转移拥有者
     *
     * @param userId
     * @return
     */
    public static String getTransferOwner(int userId) {
        return getACServerUrl(HttpUrlParams.users + "/" + userId + "/" + HttpUrlParams.transfer_owner);
    }

    /**
     * 通过sc找回sa的用户凭证
     *
     * @param id 用户id
     * @return
     */
    public static String getSAToken(int id) {
        HttpConfig.clearHear(HttpConfig.AREA_ID);
        HttpConfig.clearHear(HttpConfig.TOKEN_KEY);
        return apiSCUrl + HttpUrlParams.users + "/" + id + "/" + HttpUrlParams.sa_token;
    }

    /**
     * 上传插件
     *
     * @return
     */
    public static String getUploadPlugin() {
        return getSAServerUrl(HttpUrlParams.upload_plugins);
    }

    /**
     * 设备分类
     *
     * @return
     */
    public static String getDeviceType() {
        return getACServerUrl(HttpUrlParams.device_types);
    }

    /**
     * 获取设备access_token
     *
     * @return
     */
    public static String getDeviceAccessToken() {
        return apiSCUrl + HttpUrlParams.device_access_token + Constant.ONLY_SC;
    }

    /**
     * 找回凭证配置
     *
     * @return
     */
    public static String getFindCertificate() {
        return getACServerUrl(HttpUrlParams.find_certificate);
    }

    /**
     * 获取第三方绑定验证码
     *
     * @return
     */
    public static String getVerificationCode() {
        return getACServerUrl(HttpUrlParams.verification_code);
    }

    /**
     * 获取更新版本版本信息
     *
     * @return
     */
    public static String getSoftWareVersion() {
        return getACServerUrl(HttpUrlParams.software_version);
    }

    public static String getFirmWareLatestVersion() {
        return getACServerUrl(HttpUrlParams.firmware_latest_version);
    }

    /**
     * 获取当前版本信息
     *
     * @return
     */
    public static String getCurrentVersion() {
        return getACServerUrl(HttpUrlParams.current_version);
    }

    public static String getCurrentFirmWareVersion() {
        return getACServerUrl(HttpUrlParams.current_firmware_version);
    }

    /**
     * 升级软件
     *
     * @return
     */
    public static String getUpgradeSoftWare() {
        return getACServerUrl(HttpUrlParams.upgrade_software);
    }

    public static String getUpdateFirmWare() {
        return getACServerUrl(HttpUrlParams.update_firm_ware);
    }

    /**
     * 安装/更新插件
     *
     * @param name
     * @return
     */
    public static String getAddOrUpdatePlugin(String name) {
        return getSAServerUrl(HttpUrlParams.brand + "/" + name + "/" + HttpUrlParams.plugins);
    }

    /**
     * 品牌创作插件
     *
     * @return
     */
    public static String getCreatePluginList() {
        return getACServerUrl(HttpUrlParams.create_plugin);
    }

    /**
     * 设备一级分类
     *
     * @return
     */
    public static String getDeviceFirstType() {
        return getACServerUrl(HttpUrlParams.device_types_major);
    }

    /**
     * 设备二级分类
     *
     * @return
     */
    public static String getDeviceSecondType() {
        return getACServerUrl(HttpUrlParams.device_types_minor);
    }

    /**
     * 部门
     *
     * @return
     */
    public static String getDepartments() {
        return getACServerUrl(HttpUrlParams.departments);
    }

    /**
     * 获取家庭迁移地址
     *
     * @return
     */
    public static String getMigration() {
        return getACServerUrl(HttpUrlParams.migration);
    }

    /**
     * 迁移云端家庭到本地
     *
     * @return
     */
    public static String getCloudMigration() {
        return getACServerUrl(HttpUrlParams.cloud_migration);
    }

    /**
     * 获取扩展列表
     *
     * @return
     */
    public static String getExtensions() {
        return getACServerUrl(HttpUrlParams.extensions);
    }

    /**
     * 忘记密码
     *
     * @return
     */
    public static String getForgetPwd() {
        return getSCServerUrl(HttpUrlParams.forget_password);
    }

    /**
     * 注销家庭列表地址
     *
     * @return
     */
    public static String getUnregisterAreas(int userId) {
        return getSCServerUrl(HttpUrlParams.users + "/" + userId + "/" + HttpUrlParams.unregister_areas);
    }

    /**
     * 删除SA
     *
     * @return
     */
    public static String getDeleteSaUrl(long homeId) {
        return getACServerUrl(HttpUrlParams.areas + "/" + homeId + "/" + HttpUrlParams.delete_sa);
    }

    /**
     * 上传头像
     *
     * @param sc
     * @return
     */
    public static String getUploadAvatarUrl(boolean sc) {
        if (sc) {
            return apiSCUrl + HttpUrlParams.users + "/" + UserUtils.getCloudUserId() + "/" + HttpUrlParams.file_upload + Constant.ONLY_SC;
        } else {
            return getACServerUrl(HttpUrlParams.files);
        }
    }

    /**
     * app升级版本信息
     *
     * @return
     */
    public static String getAppVersionInfo() {
        return getSCServerUrl(HttpUrlParams.update_apk_url);
    }

    /**
     * 设备图标
     *
     * @return
     */
    public static String getLogo(int id) {
        return getACServerUrl(HttpUrlParams.devices + "/" + id + "/" + HttpUrlParams.logo);
    }

    /**
     * @return
     */
    public static String getCheck() {
        return getACServerUrl(HttpUrlParams.check);
    }

    /**
     * 获取SA支持的最低Api版本
     *
     * @return
     */
    public static String getSupportApi() {
        return getSCServerUrl(HttpUrlParams.common_service_software_support_api);
    }

    /**
     * 获取App支持的最低Api版本
     *
     * @return
     */
    public static String getAppSupportApi() {
        return getSCServerUrl(HttpUrlParams.common_service_app_support_api);
    }

    /**
     * 第三方云绑定列表
     *
     * @return
     */
    public static String getThirdParty(boolean sc) {
        return sc ? (getSCServerUrl(HttpUrlParams.cloud_list) + Constant.ONLY_SC) : getACServerUrl(HttpUrlParams.apps);
    }

    /**
     * 解绑第三方云
     *
     * @return
     */
    public static String getCloudUnbind(String appId, String areaId) {
        return getACServerUrl(HttpUrlParams.apps + "/" + appId + "/" + HttpUrlParams.apps_unbind_areas + "/" + areaId);
    }

    /**
     * 问题反馈-获取列表
     *
     * @param userId
     * @return
     */
    public static String getFeedbackList(int userId) {
        return getSCServerUrlWithoutHead(HttpUrlParams.users + "/" + userId + "/" + HttpUrlParams.feedbacks) + Constant.ONLY_SC;
    }

    /**
     * 问题反馈-获取详情
     *
     * @param userId
     * @param feedbackId
     * @return
     */
    public static String getFeedbackDetail(int userId, int feedbackId) {
        return getSCServerUrlWithoutHead(HttpUrlParams.users + "/" + userId + "/" + HttpUrlParams.feedbacks + "/" + feedbackId) + Constant.ONLY_SC;
    }

    /**
     * 反馈上传文件
     * @return
     */
    public static String getFileUpload() {
        return getSCServerUrlWithoutHead(HttpUrlParams.users + "/" + UserUtils.getCloudUserId() + "/" + HttpUrlParams.file_upload) + Constant.ONLY_SC;
    }
}
