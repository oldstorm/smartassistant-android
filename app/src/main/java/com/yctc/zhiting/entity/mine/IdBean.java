package com.yctc.zhiting.entity.mine;

public class IdBean {

    private long id;
    private CloudSaUserInfo cloud_sa_user_info;

    public CloudSaUserInfo getCloud_sa_user_info() {
        return cloud_sa_user_info;
    }

    public void setCloud_sa_user_info(CloudSaUserInfo cloud_sa_user_info) {
        this.cloud_sa_user_info = cloud_sa_user_info;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public class CloudSaUserInfo {
        private int id;
        private String token;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
