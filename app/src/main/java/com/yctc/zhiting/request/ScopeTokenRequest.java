package com.yctc.zhiting.request;

import com.app.main.framework.httputil.request.Request;

import java.util.List;

public class ScopeTokenRequest extends Request {
    private List<String> scopes;

    public ScopeTokenRequest(List<String> scopes) {
        this.scopes = scopes;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public void setScopes(List<String> scopes) {
        this.scopes = scopes;
    }
}
