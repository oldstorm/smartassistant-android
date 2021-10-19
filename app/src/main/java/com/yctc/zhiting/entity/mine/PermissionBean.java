package com.yctc.zhiting.entity.mine;

import android.os.Parcelable;

public class PermissionBean {


    /**
     * permissions : {"add_area":true,"add_device":true,"add_role":false,"control_device":true,"delete_area":false,"delete_device":false,"delete_role":true,"delete_situation_member":true,"get_area":true,"get_role":false,"get_situation_invite_code":false,"update_area_name":false,"update_area_order":false,"update_device":true,"update_role":false,"update_situation_member":false,"update_situation_name":false}
     */

    private PermissionsBean permissions;

    public PermissionsBean getPermissions() {
        return permissions;
    }

    public void setPermissions(PermissionsBean permissions) {
        this.permissions = permissions;
    }

    public static class PermissionsBean  {
        /**
         * add_area : true
         * add_device : true
         * add_role : false
         * control_device : true
         * delete_area : false
         * delete_device : false
         * delete_role : true
         * delete_situation_member : true
         * get_area : true
         * get_role : false
         * get_situation_invite_code : false
         * update_area_name : false
         * update_area_order : false
         * update_device : true
         * update_role : false
         * update_situation_member : false
         * update_situation_name : false
         */

        private boolean add_area;//
        private boolean add_device;//添加设备
        private boolean add_role;//新增角色
        private boolean control_device;//控制设备
        private boolean delete_area;//
        private boolean delete_device;//删除设备
        private boolean delete_role;//删除角色
        private boolean delete_situation_member;
        private boolean get_area;//
        private boolean get_role;//
        private boolean get_area_invite_code;//生成邀请码
        private boolean update_area_name;//修改家庭名称
        private boolean update_area_order;//
        private boolean update_device;//更新设备
        private boolean update_role;//更新角色
        private boolean update_situation_member;//
        private boolean update_situation_name;//
        private boolean update_location_order;//调整房间顺序
        private boolean add_location;//添加房间/区域
        private boolean delete_location;//删除房间
        private boolean update_location_name;//修改房间名称
        private boolean update_area_member_role;//修改成员角色
        private boolean delete_area_member;//删除家庭成员
        private boolean add_scene;//添加场景
        private boolean control_scene;//控制场景
        private boolean get_location;//查看房间详情
        private boolean update_scene; //修改场景
        private boolean delete_scene;// 删除场景

        public boolean isGet_location() {
            return get_location;
        }

        public void setGet_location(boolean get_location) {
            this.get_location = get_location;
        }

        public boolean isAdd_area() {
            return add_area;
        }

        public void setAdd_area(boolean add_area) {
            this.add_area = add_area;
        }

        public boolean isAdd_device() {
            return add_device;
        }

        public void setAdd_device(boolean add_device) {
            this.add_device = add_device;
        }

        public boolean isAdd_role() {
            return add_role;
        }

        public void setAdd_role(boolean add_role) {
            this.add_role = add_role;
        }

        public boolean isControl_device() {
            return control_device;
        }

        public void setControl_device(boolean control_device) {
            this.control_device = control_device;
        }

        public boolean isDelete_area() {
            return delete_area;
        }

        public void setDelete_area(boolean delete_area) {
            this.delete_area = delete_area;
        }

        public boolean isDelete_device() {
            return delete_device;
        }

        public void setDelete_device(boolean delete_device) {
            this.delete_device = delete_device;
        }

        public boolean isDelete_role() {
            return delete_role;
        }

        public void setDelete_role(boolean delete_role) {
            this.delete_role = delete_role;
        }

        public boolean isDelete_situation_member() {
            return delete_situation_member;
        }

        public void setDelete_situation_member(boolean delete_situation_member) {
            this.delete_situation_member = delete_situation_member;
        }

        public boolean isGet_area() {
            return get_area;
        }

        public void setGet_area(boolean get_area) {
            this.get_area = get_area;
        }

        public boolean isGet_role() {
            return get_role;
        }

        public void setGet_role(boolean get_role) {
            this.get_role = get_role;
        }

        public boolean isGet_area_invite_code() {
            return get_area_invite_code;
        }

        public void setGet_area_invite_code(boolean get_area_invite_code) {
            this.get_area_invite_code = get_area_invite_code;
        }

        public boolean isUpdate_area_name() {
            return update_area_name;
        }

        public void setUpdate_area_name(boolean update_area_name) {
            this.update_area_name = update_area_name;
        }

        public boolean isUpdate_area_order() {
            return update_area_order;
        }

        public void setUpdate_area_order(boolean update_area_order) {
            this.update_area_order = update_area_order;
        }

        public boolean isUpdate_device() {
            return update_device;
        }

        public void setUpdate_device(boolean update_device) {
            this.update_device = update_device;
        }

        public boolean isUpdate_role() {
            return update_role;
        }

        public void setUpdate_role(boolean update_role) {
            this.update_role = update_role;
        }

        public boolean isUpdate_situation_member() {
            return update_situation_member;
        }

        public void setUpdate_situation_member(boolean update_situation_member) {
            this.update_situation_member = update_situation_member;
        }

        public boolean isUpdate_situation_name() {
            return update_situation_name;
        }

        public void setUpdate_situation_name(boolean update_situation_name) {
            this.update_situation_name = update_situation_name;
        }

        public boolean isUpdate_location_order() {
            return update_location_order;
        }

        public void setUpdate_location_order(boolean update_location_order) {
            this.update_location_order = update_location_order;
        }

        public boolean isAdd_location() {
            return add_location;
        }

        public void setAdd_location(boolean add_location) {
            this.add_location = add_location;
        }

        public boolean isDelete_location() {
            return delete_location;
        }

        public void setDelete_location(boolean delete_location) {
            this.delete_location = delete_location;
        }

        public boolean isUpdate_location_name() {
            return update_location_name;
        }

        public void setUpdate_location_name(boolean update_location_name) {
            this.update_location_name = update_location_name;
        }

        public boolean isUpdate_area_member_role() {
            return update_area_member_role;
        }

        public void setUpdate_area_member_role(boolean update_area_member_role) {
            this.update_area_member_role = update_area_member_role;
        }

        public boolean isDelete_area_member() {
            return delete_area_member;
        }

        public void setDelete_area_member(boolean delete_area_member) {
            this.delete_area_member = delete_area_member;
        }

        public boolean isAdd_scene() {
            return add_scene;
        }

        public void setAdd_scene(boolean add_scene) {
            this.add_scene = add_scene;
        }

        public boolean isControl_scene() {
            return control_scene;
        }

        public void setControl_scene(boolean control_scene) {
            this.control_scene = control_scene;
        }

        public boolean isUpdate_scene() {
            return update_scene;
        }

        public void setUpdate_scene(boolean update_scene) {
            this.update_scene = update_scene;
        }

        public boolean isDelete_scene() {
            return delete_scene;
        }

        public void setDelete_scene(boolean delete_scene) {
            this.delete_scene = delete_scene;
        }
    }
}
