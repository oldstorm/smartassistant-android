package com.yctc.zhiting.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

    public static byte [] hashV2(String filePath) {
        try {
            File file = new File(filePath);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            int bufferSize = 16384;
            byte [] buffer = new byte[bufferSize];
            int sizeRead = -1;
            while ((sizeRead = in.read(buffer)) != -1) {
                digest.update(buffer, 0, sizeRead);
            }
            in.close();
            byte [] hash = null;
            hash = new byte[digest.getDigestLength()];
            hash = digest.digest();
            return hash;
        } catch (IOException ie) {
            ie.printStackTrace();
        } catch (NoSuchAlgorithmException ne) {
            ne.printStackTrace();
        }
        return null;
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                sb.append("0");
            } else if (hex.length() == 8) {
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }

    /**
     * 图片
     *
     * @param fileName
     * @return
     */
    public static boolean isImage(String fileName) {
        return fileName.endsWith(".psd") || fileName.endsWith(".pdd") || fileName.endsWith(".psdt") || fileName.endsWith(".psb")
                || fileName.endsWith(".bmp") || fileName.endsWith(".rle") || fileName.endsWith(".dib") || fileName.endsWith(".gif")
                || fileName.endsWith(".dcm") || fileName.endsWith(".dc3") || fileName.endsWith(".dic") || fileName.endsWith(".eps")
                || fileName.endsWith(".iff") || fileName.endsWith(".tdi") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                || fileName.endsWith(".jpg") || fileName.endsWith(".jpf") || fileName.endsWith(".jpx") || fileName.endsWith(".jp2")
                || fileName.endsWith(".j2c") || fileName.endsWith(".j2k") || fileName.endsWith(".jpc") || fileName.endsWith(".jps")
                || fileName.endsWith(".pcx") || fileName.endsWith(".pdp") || fileName.endsWith(".raw") || fileName.endsWith(".pxr")
                || fileName.endsWith(".png") || fileName.endsWith(".pbm") || fileName.endsWith(".pgm") || fileName.endsWith(".ppm")
                || fileName.endsWith(".pnm") || fileName.endsWith(".pfm") || fileName.endsWith(".pam") || fileName.endsWith(".sct")
                || fileName.endsWith(".tga") || fileName.endsWith(".vda") || fileName.endsWith(".icb") || fileName.endsWith(".vst")
                || fileName.endsWith(".tif") || fileName.endsWith(".tiff") || fileName.endsWith(".mpo") || fileName.endsWith(".webp ")
                || fileName.endsWith(".ico") || fileName.endsWith(".heic");
    }
}
