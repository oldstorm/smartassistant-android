package com.yctc.zhiting.utils;

import android.util.Base64;

import com.google.gson.Gson;
import com.yctc.zhiting.utils.jwt.JwtBean;

public class JwtUtil {
    public static JwtBean decodeJwt(String jwtStr){
        JwtBean jwtBean = null;
        try {
            String[] split = jwtStr.split("\\.");
            byte[] jwtHeaderByte = Base64.decode(split[0], Base64.URL_SAFE);
            byte[] jwtBodyByte = Base64.decode(split[1], Base64.URL_SAFE);
            String jwtHeaderJson = new String(jwtHeaderByte, "UTF-8");
            String jwtBodyJson = new String(jwtBodyByte, "UTF-8");
            JwtBean.JwtHeader jwtHeader = new Gson().fromJson(jwtHeaderJson, JwtBean.JwtHeader.class);
            JwtBean.JwtBody jwtBody = new Gson().fromJson(jwtBodyJson, JwtBean.JwtBody.class);
            jwtBean = new JwtBean();
            jwtBean.setJwtHeader(jwtHeader);
            jwtBean.setJwtBody(jwtBody);
        }catch (Exception e){
            e.printStackTrace();
        }
       return jwtBean;
    }
}
