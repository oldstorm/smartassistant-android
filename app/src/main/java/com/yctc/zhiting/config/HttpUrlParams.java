package com.yctc.zhiting.config;

/**
 * date : 2021/4/2817:54
 * desc :
 * 地址参数
 */
public interface HttpUrlParams {

    /***************** 家庭/公司 ****************/
    String brand = "brands"; //品牌列表sa/sc
    String devices = "devices"; //添加设备sa就是走sa,如果是设备sa/sc
    String areas = "areas"; // 家庭/物业列表sc,其他sa
    String location_tmpl = "location_tmpl"; // 房间/位置默认勾选列表sa
    String users = "users"; // 成员列表sa/sc
    String sync = "sync"; // 数据同步sa

    /***************** 房间/区域 ****************/
    String locations = "locations"; // 房间/位置列表sa/sc

    /***************** 角色 ****************/
    String roles = "roles"; // 角色列表sa/sc
    String permissions = "permissions"; // 获取用户权限sa/sc
    String invitationCode = "/invitation/code"; // 获取邀请二维码sa/sc
    String invitationCheck = "invitation/check"; // 扫描邀请二维码sa/sc
    String checkBindSa = "check"; // 检查SA设备绑定情况 sa
    String scenes = "scenes"; // 场景列表sa/sc
    String execute = "execute"; //场景的执行sa/sc
    String scene_logs = "scene_logs"; //场景日志sa/sc
    String plugins = "plugins"; //插件详情sa/sc

    String captcha = "captcha"; //获取验证码sc
    String sessions_login = "sessions/login"; //登录 sc
    String sessions_logout = "sessions/logout"; //登出 sc
    String cloud_bind = "cloud/bind"; //绑定云端 sa
    String scopes = "scopes"; //获取 SCOPE 列表 sa
    String scopes_token = "scopes/token"; //获取 SCOPE Token sa
    String transfer_owner = "owner"; // 转移拥有者
    String sa_token = "sa_token";  // 通过sc找回sa的用户凭证
    String upload_plugins = "plugins"; //插件详情sa/sc
    String device_types = "device/types"; // 设备型号列表（按分类分组）
    String plugin_detail = "plugins"; // 插件详情
    String device_access_token = "oauth2/access_token"; // 获取设备access_token
    String find_certificate = "setting";//找回凭证配置
    String verification_code = "verification/code";//获取第三方绑定验证码
    String software_version = "supervisor/update/latest";//获取更新版本信息
    String firmware_latest_version = "supervisor/firmware/update/latest";//获取固件最新版本信息
    String upgrade_software = "supervisor/update";//升级软件
    String update_firm_ware = "supervisor/firmware/update";//获取固件升级
    String current_version = "supervisor/update";//当前版本
    String current_firmware_version = "supervisor/firmware/update";//当前固件版本
    String create_plugin = "plugins";//插件列表

    String device_types_major = "device/types/major";//设备一级分类列表
    String device_types_minor = "device/types/minor";//设备二级分类列表

    String company = "company";//公司
    String departments = "departments";//部门列表
    String migration = "migration"; //获取家庭迁移地址
    String cloud_migration = "cloud/migration"; //迁移云端家庭到本地
    String extensions = "extensions"; //迁移云端家庭到本地

    String forget_password = "forget_password"; //忘记密码
    String unregister_areas = "areas"; //账号注销获取家庭列表
    String delete_sa = "device/sa"; //删除sa设备
    String sa = "sa"; //添加sa设备

    String files = "files"; //sa上传文件
    String file_upload = "file/upload"; //sc上传文件

    String update_apk_url = "common/service/app/support/app";//获取APP升级需要信息


    String logo = "logo"; //设备图标
    String check = "check"; //检查SA状态信息
    String common_service_software_support_api = "common/service/software/support/api"; //获取SA支持的最低Api版本
    String common_service_app_support_api = "common/service/app/support/api"; //获取App支持的最低Api版本


    String cloud_list = "cloud/list"; //第三方云绑定列表
    String cloud_unbind = "cloud/unbind"; //解绑第三方云
    String apps = "apps"; //第三方平台列表
    String apps_unbind = "apps/unbind"; //第三方平台列表
    String apps_unbind_areas = "areas"; //第三方平台列表

    String feedbacks = "feedbacks"; //问题反馈-获取列表

}
