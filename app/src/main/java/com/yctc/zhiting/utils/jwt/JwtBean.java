package com.yctc.zhiting.utils.jwt;

import java.util.List;

/**
 * 解密二维码的数据
 */
public class JwtBean {

    private JwtHeader jwtHeader;
    private JwtBody jwtBody;

    public JwtHeader getJwtHeader() {
        return jwtHeader;
    }

    public void setJwtHeader(JwtHeader jwtHeader) {
        this.jwtHeader = jwtHeader;
    }

    public JwtBody getJwtBody() {
        return jwtBody;
    }

    public void setJwtBody(JwtBody jwtBody) {
        this.jwtBody = jwtBody;
    }

    public static class JwtHeader{

        /**
         * alg : HS256
         * typ : JWT
         */

        private String alg;
        private String typ;

        public String getAlg() {
            return alg;
        }

        public void setAlg(String alg) {
            this.alg = alg;
        }

        public String getTyp() {
            return typ;
        }

        public void setTyp(String typ) {
            this.typ = typ;
        }
    }

    public static class JwtBody{

        /**
         * area_id : 4886414080622068
         * exp : 1631517376
         * role_ids : [1,2]
         * sa_id : demo-sa
         * uid : 1
         */

        private long area_id;
        private int exp;
        private String sa_id;
        private int uid;
        private int area_type;
        private List<Integer> role_ids;

        public long getArea_id() {
            return area_id;
        }

        public void setArea_id(long area_id) {
            this.area_id = area_id;
        }

        public int getExp() {
            return exp;
        }

        public void setExp(int exp) {
            this.exp = exp;
        }

        public String getSa_id() {
            return sa_id;
        }

        public void setSa_id(String sa_id) {
            this.sa_id = sa_id;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getArea_type() {
            return area_type;
        }

        public void setArea_type(int area_type) {
            this.area_type = area_type;
        }

        public List<Integer> getRole_ids() {
            return role_ids;
        }

        public void setRole_ids(List<Integer> role_ids) {
            this.role_ids = role_ids;
        }
    }
}
