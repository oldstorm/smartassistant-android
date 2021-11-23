package com.yctc.zhiting.entity;

public class ScopeTokenBean {

    /**
     * scope_token : {"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MjgyNDc2NzgsInNhX2lkIjoidGVzdC1zYS10ZXN0Iiwic2NvcGVzIjoidXNlcixhcmVhIiwidWlkIjozfQ.HM_pLMTYw_Yzz4kWQIERWU9FnmP6SM_ejV1M0GMXbAc","expires_in":2592000}
     */

    private STBean scope_token;

    public STBean getScope_token() {
        return scope_token;
    }

    public void setScope_token(STBean scope_token) {
        this.scope_token = scope_token;
    }

    public static class STBean {
        /**
         * token : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2MjgyNDc2NzgsInNhX2lkIjoidGVzdC1zYS10ZXN0Iiwic2NvcGVzIjoidXNlcixhcmVhIiwidWlkIjozfQ.HM_pLMTYw_Yzz4kWQIERWU9FnmP6SM_ejV1M0GMXbAc
         * expires_in : 2592000
         */

        private String token;
        private int expires_in;  // 有效期，单位为秒

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }
    }
}
