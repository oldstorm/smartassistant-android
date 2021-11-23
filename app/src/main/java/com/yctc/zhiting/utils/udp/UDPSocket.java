package com.yctc.zhiting.utils.udp;

import android.util.Log;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * upd socket通信
 */
public class UDPSocket {

    private final String TAG = UDPSocket.class.getSimpleName();
    // 单个CPU线程池大小
    private static final int POOL_SIZE = 5;
    private static final int BUFFER_LENGTH = 1024;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];

    private String ipAddress; // ip地址
    private int portNum;  // 端口号
    private volatile DatagramSocket client;  // socket客户端
    private DatagramPacket receivePacket;  // 数据接收包
    private volatile boolean isThreadRunning = false;
    private ExecutorService mThreadPool;
    private Thread clientThread;

    private OnReceiveCallback receiveCallback;

    public OnReceiveCallback getReceiveCallback() {
        return receiveCallback;
    }

    public void setReceiveCallback(OnReceiveCallback receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public UDPSocket(String ipAddress, int portNum) {
        this.ipAddress = ipAddress;
        this.portNum = portNum;
        init();

    }

    public UDPSocket(String ipAddress, int portNum, OnReceiveCallback receiveCallback) {
        this.ipAddress = ipAddress;
        this.portNum = portNum;
        this.receiveCallback = receiveCallback;
        init();
    }

    private void init(){
        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
    }

    public void startUDPSocket() {
        if (client != null) return;
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            client = new DatagramSocket(portNum);

            if (receivePacket == null) {
                // 创建接受数据的 packet
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }

            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启接收数据的线程
     */
    private void startSocketThread() {
        if (clientThread!=null){
            clientThread.interrupt();
            clientThread = null;
        }
        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiveMessage();
            }
        });
        isThreadRunning = true;
        clientThread.start();
    }

    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (isThreadRunning) {
            try {
                if (client != null && !client.isClosed()) {
                    client.receive(receivePacket);
                }
                Log.d(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                stopUDPSocket();
                e.printStackTrace();
                return;
            }

            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String strReceive = null;
            try {
                strReceive = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            //回调接收到的 json 信息
            if(receiveCallback!=null){
                System.out.println("响应地址："+receivePacket.getAddress().getHostAddress());
                System.out.println("响应长度："+receivePacket.getLength());
                receiveCallback.onReceiveByteData(receivePacket.getAddress().getHostAddress(), receivePacket.getPort(), receivePacket.getData(), receivePacket.getLength());
                receiveCallback.onReceive(strReceive);
            }

            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }

    /**
     * 发送消息
     * @param msgByte
     */
    public void sendMessage(byte[] msgByte, String address){
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress serverAddress = InetAddress.getByName(address);
                    DatagramPacket packet = new DatagramPacket(msgByte, msgByte.length, serverAddress, portNum);
                    client.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopUDPSocket() {
        isThreadRunning = false;
        receivePacket = null;
        if (clientThread != null) {
            clientThread.interrupt();
            clientThread = null;
        }
        if (client != null && !client.isClosed()) {
            client.close();
            client.disconnect();
            client = null;
        }
    }

    public interface OnReceiveCallback{
        /**
         *
         * @param address 响应地址
         * @param port    响应端口
         * @param data    字节数据
         * @param length  字节长度
         */
        void onReceiveByteData(String address, int port, byte[] data, int length);
        void onReceive(String msg);
    }
}
