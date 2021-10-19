package com.yctc.zhiting.entity.mine;

public class DevicesBean {


    /**
     * id : 5844
     * logo_url : http://dummyimage.com/'200x200'/79a3f2')
     * name : 务基上
     */

    private int id;
    private String logo_url;
    private String name;
    private boolean is_sa;
    private String plugin_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public boolean isIs_sa() {
        return is_sa;
    }

    public void setIs_sa(boolean is_sa) {
        this.is_sa = is_sa;
    }

    public String getPlugin_url() {
        return plugin_url;
    }

    public void setPlugin_url(String plugin_url) {
        this.plugin_url = plugin_url;
    }
}
