package com.yctc.zhiting.db;

public class DBThread extends Thread {

    public DBThread(Runnable runnable){
        super(runnable, "DBThread");
    }
}