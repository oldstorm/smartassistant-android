package com.app.main.framework.entity;

public class ChannelEntity extends BaseEntity {
    private String host;//临时通道域名
    private long expires_time;//端口过期时间,单位秒
    private long generate_channel_time;//生成临时通道的时间

    public long getCreate_channel_time() {
        return generate_channel_time;
    }

    public ChannelEntity setGenerate_channel_time(long generate_channel_time) {
        this.generate_channel_time = generate_channel_time;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ChannelEntity setHost(String host) {
        this.host = host;
        return this;
    }

    public long getExpires_time() {
        return expires_time;
    }

    public ChannelEntity setExpires_time(long expires_time) {
        this.expires_time = expires_time;
        return this;
    }
}
