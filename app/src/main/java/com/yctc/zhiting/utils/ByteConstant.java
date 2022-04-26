package com.yctc.zhiting.utils;

public class ByteConstant {

    // 发送hello包数据，固定
    public static final byte[] SEND_HELLO_DATA = {(byte) 0x21, (byte) 0x31, (byte) 0x00, (byte) 0x20, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};


    public static final byte[] TOKEN_HEAD_DATA = {(byte) 0x21, (byte) 0x31, (byte) 0x00, (byte) 0x20, (byte) 0xFF, (byte) 0xFE}; //设备token 包头，包长，预留字节固定

    public static final byte[] SER_DATA = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}; // 序列号 固定

    public static final byte[] GET_DEV_INFO_HEAD_Data = {(byte) 0x21, (byte) 0x31}; // 获取设备信息包头固定

    public static final byte[] GET_DEV_INFO_PRE_Data = {(byte) 0xFF, (byte) 0xFF}; // 获取设备信息预留字节固定
}
