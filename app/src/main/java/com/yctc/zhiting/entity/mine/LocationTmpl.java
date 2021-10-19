package com.yctc.zhiting.entity.mine;


/**
 * 房间/位置默认勾选列表
 */
public class LocationTmpl {

    /**
     * name : fugiat ex nostrud
     * chosen : true
     */

    private String name;
    private boolean chosen;

    public LocationTmpl(String name, boolean chosen) {
        this.name = name;
        this.chosen = chosen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }
}
