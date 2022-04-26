package com.yctc.zhiting.entity.mine;

public class RemoveHCBean {

    // 1 正在移除| 2 移除出错| 3 移除成功
    private int remove_status;

    public int getRemove_status() {
        return remove_status;
    }

    public void setRemove_status(int remove_status) {
        this.remove_status = remove_status;
    }
}
