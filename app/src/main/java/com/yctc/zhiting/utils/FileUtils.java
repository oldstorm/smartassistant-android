package com.yctc.zhiting.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

/**
 * author : JIM
 * date : 2021/4/1612:30
 * desc : 文件工具类
 */
public class FileUtils {
    /**
     * 打印错误日志
     *
     * @param ex
     */
    public static String writeErrorLog(Throwable ex,String filePath) {
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.e("example", "崩溃信息\n" + info);
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "errorLog" +getYearMonthDay(System.currentTimeMillis()) + ".txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            assert info != null;
            fileOutputStream.write(info.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    public static String getYearMonthDay(long time) {
        return new SimpleDateFormat("MM-dd-HH:mm").format(time);
    }
}
