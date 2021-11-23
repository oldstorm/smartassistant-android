package com.yctc.zhiting.entity.mine;

import com.app.main.framework.entity.BaseEntity;
import com.google.gson.annotations.SerializedName;

public class HomeCompanyBean extends BaseEntity {

    private long id;//云端家庭id
    private long localId; // 本地家庭id
    private int roomAreaCount;//房间个数
    private int location_count;  // 房间数量
    private int role_count;  // 角色数量
    private int user_count; // 成员数量
    @SerializedName("sa_user_id")
    private int user_id; // sa用户id
    private int cloud_user_id; // 云端用户id
    private boolean is_bind_sa;  //是否绑定sa
    private boolean selected; // 标识是否被选中
    private boolean SAEnvironment;//是否在SA环境
    private String name;//家庭名称
    private String sa_lan_address; // sa地址
    private String sa_user_token;  // sa token
    private String ss_id;//wifi id
    private String mac_address;//wifi地址
    private String sc_lan_address;//sc地址
    private long area_id; // sa家庭id
    private boolean isVirtual;//虚拟SA
    private String sa_id; // sa设备id

    public boolean isVirtual() {
        return isVirtual;
    }

    public void setVirtual(boolean virtual) {
        isVirtual = virtual;
    }

    public boolean isSAEnvironment() {
        return SAEnvironment;
    }

    public HomeCompanyBean setSAEnvironment(boolean SAEnvironment) {
        this.SAEnvironment = SAEnvironment;
        return this;
    }

    public String getSc_lan_address() {
        return sc_lan_address;
    }

    public HomeCompanyBean setSc_lan_address(String sc_lan_address) {
        this.sc_lan_address = sc_lan_address;
        return this;
    }

    public String getSs_id() {
        return ss_id;
    }

    public void setSs_id(String ss_id) {
        this.ss_id = ss_id;
    }

    public String getMac_address() {
        return mac_address;
    }

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public HomeCompanyBean() {
    }

    public HomeCompanyBean(String name) {
        this.name = name;
    }

    public HomeCompanyBean(long localId, String name) {
        this.localId = localId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoomAreaCount() {
        return roomAreaCount;
    }

    public void setRoomAreaCount(int roomAreaCount) {
        this.roomAreaCount = roomAreaCount;
    }

    public int getLocation_count() {
        return location_count;
    }

    public void setLocation_count(int location_count) {
        this.location_count = location_count;
    }

    public int getRole_count() {
        return role_count;
    }

    public void setRole_count(int role_count) {
        this.role_count = role_count;
    }

    public int getUser_count() {
        return user_count;
    }

    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    public String getSa_lan_address() {
        return sa_lan_address;
    }

    public void setSa_lan_address(String sa_lan_address) {
        this.sa_lan_address = sa_lan_address;
    }

    public String getSa_user_token() {
        return sa_user_token;
    }

    public void setSa_user_token(String sa_user_token) {
        this.sa_user_token = sa_user_token;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isIs_bind_sa() {
        return is_bind_sa;
    }

    public void setIs_bind_sa(boolean is_bind_sa) {
        this.is_bind_sa = is_bind_sa;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public int getCloud_user_id() {
        return cloud_user_id;
    }

    public void setCloud_user_id(int cloud_user_id) {
        this.cloud_user_id = cloud_user_id;
    }

    public long getArea_id() {
        return area_id;
    }

    public void setArea_id(long area_id) {
        this.area_id = area_id;
    }

    public String getSa_id() {
        return sa_id;
    }

    public void setSa_id(String sa_id) {
        this.sa_id = sa_id;
    }
}
