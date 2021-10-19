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

}
