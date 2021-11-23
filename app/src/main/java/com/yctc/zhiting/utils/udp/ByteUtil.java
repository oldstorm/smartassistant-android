package com.yctc.zhiting.utils.udp;

import com.yctc.zhiting.utils.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Formatter;

public class ByteUtil {
    public static  byte[] byteMergerAll(byte[]... values){
        int len_byte = 0;
        for (int i=0; i<values.length; i++){
            len_byte += values[i].length;
        }
        byte[] total_byte = new byte[len_byte];
        int countLength = 0;
        for (int i=0; i<values.length; i++){
            byte[] b = values[i];
            System.arraycopy(b, 0, total_byte, countLength, b.length);
            countLength += b.length;
        }
        return total_byte;
    }

    public static byte[] intToByte(int num)
    {
        byte[] result=null;
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        DataOutputStream dos=new DataOutputStream(bos);
        try {
            dos.writeInt(num);
            result=bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 把16进制字符串转换成字节数组
     * @param hex
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) ((toByte(achar[pos]) << 4 | toByte(achar[pos + 1])) & 0xff);
        }
        return result;
    }

    // byte[] 转化十六进制的字符串
    public static String bytesToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static int toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * crc
     * @param buf
     * @param len
     * @return
     */
   public static int alex_crc16(byte[] buf, int len) {
        int i, j;
        int c, crc = 0xFFFF;
        for (i = 0; i < len; i++) {
            c = buf[i] & 0x00FF;
            crc ^= c;
            for (j = 0; j < 8; j++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else
                    crc >>= 1;
            }
        }
        return (crc);
    }


    /**
     * 把整型用长度为2的字节数组表示
     * @param i
     * @return
     */
    public static byte[] intToByte2(int i) {
        byte[] targets = new byte[2];
        targets[1] = (byte) (i & 0xFF);
        targets[0] = (byte) (i >> 8 & 0xFF);
        return targets;
    }

    /**
     * 将长度为32的md5字符转为长度为16的字节数组
     * @param md5/
     * @return
     */
    public static byte[] md5Str2Byte(String md5){
        byte[] result=new  byte [16];
        int  m=0;
        if (md5.length()==32) {
            for (int j = 0; j < 32; j += 2) {
                int num = Integer.valueOf(md5.substring(j, j + 2), 16);
                if (num > 127) {
                    result[m] = (byte) (num - 256);
                    System.out.print((num - 256) + " ");
                } else {
                    result[m] = (byte) (num);
                }
                m++;
            }
        }
        return result;
    }
}
