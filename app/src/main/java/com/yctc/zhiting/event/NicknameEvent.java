package com.yctc.zhiting.event;

public class NicknameEvent {
    private String nickname;

    public NicknameEvent(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
