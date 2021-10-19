package com.yctc.zhiting.entity.mine;

import java.io.Serializable;
import java.util.List;

public class BrandsBean implements Serializable {
    /**
     * logo_url : http://sa.zhitingtech.com/static/test-sa-test/plugins/light/html/static/img/yeelight.jpg
     * name : yeelight
     * plugin_amount : 1
     * plugins : [{"id":"light_001","name":"light","logo_url":"html/static/img/yeelight.jpg","version":"0.0.1","brand":"yeelight","info":"测试插件","download_url":"https://test.yctc.tech/api/file/originals/id/2009325/fn/yeelight-plugin.zip","visit_url":"www.baidu.com","support_devices":[{"logo_url":"https://tysq2.yctc.tech/api/file/originals/id/2009071/fn/yeelight吸顶灯1.png","model":"ceiling17","name":"yeelight吸顶灯","actions":null},{"logo_url":"https://tysq1.yctc.tech/api/file/originals/id/2009070/fn/yeelight台灯.png","model":"lamp9","name":"yeelight台灯","actions":null}],"is_added":true,"is_newest":true}]
     * is_added : true
     * is_newest : true
     */

    private String logo_url;
    private String name;
    private int plugin_amount;
    private boolean is_added;
    private boolean is_newest;
    private boolean updating; // 是否在更新中
    private List<PluginsBean> plugins;

    private List<SupportDevicesBean> support_devices; // 品牌详情才有

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlugin_amount() {
        return plugin_amount;
    }

    public void setPlugin_amount(int plugin_amount) {
        this.plugin_amount = plugin_amount;
    }

    public boolean isIs_added() {
        return is_added;
    }

    public void setIs_added(boolean is_added) {
        this.is_added = is_added;
    }

    public boolean isIs_newest() {
        return is_newest;
    }

    public void setIs_newest(boolean is_newest) {
        this.is_newest = is_newest;
    }

    public List<SupportDevicesBean> getSupport_devices() {
        return support_devices;
    }

    public void setSupport_devices(List<SupportDevicesBean> support_devices) {
        this.support_devices = support_devices;
    }

    public boolean isUpdating() {
        return updating;
    }

    public void setUpdating(boolean updating) {
        this.updating = updating;
    }

    public List<PluginsBean> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<PluginsBean> plugins) {
        this.plugins = plugins;
    }
}
