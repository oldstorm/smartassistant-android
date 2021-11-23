package com.yctc.zhiting.entity;

import com.app.main.framework.httputil.request.Request;

public class FindCertificateBean extends Request {
    private FindCertificateItemBean user_credential_found_setting;

    public FindCertificateItemBean getUser_credential_found_setting() {
        return user_credential_found_setting;
    }

    public void setUser_credential_found_setting(FindCertificateItemBean user_credential_found_setting) {
        this.user_credential_found_setting = user_credential_found_setting;
    }

    public static class FindCertificateItemBean extends Request{
        private boolean user_credential_found;

        public boolean isUser_credential_found() {
            return user_credential_found;
        }

        public void setUser_credential_found(boolean user_credential_found) {
            this.user_credential_found = user_credential_found;
        }
    }
}
