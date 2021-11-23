package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

import java.util.List;

public class AddOrUpdatePluginRequest extends Request {

    private List<String> plugins;

    public AddOrUpdatePluginRequest(List<String> plugins) {
        this.plugins = plugins;
    }

    public List<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<String> plugins) {
        this.plugins = plugins;
    }
}
