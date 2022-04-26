package com.app.main.framework.baseutil;


public class TraceUtil {
    public static final String HEXDIGLC = "0123456789abcdef";

    public static String getHexdiglc(int count) {
        String hexdiglcStr = "";
        for (int i=0; i<count; i++) {
            int random = (int) (Math.random()*15+1);
            hexdiglcStr = hexdiglcStr + HEXDIGLC.charAt(random);
        }
        return hexdiglcStr;
    }
}
