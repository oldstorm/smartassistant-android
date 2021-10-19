package com.yctc.zhiting.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * date : 2021/7/27 15:49
 * desc :
 */
public class CloneUtil {
    /**
     * 复制
     *
     * @return
     */
    public static <T extends Serializable> T clone(T object) {
        try {
            // 说明：调用ByteArrayOutputStream或ByteArrayInputStream对象的close方法没有任何意义
            // 这两个基于内存的流只要垃圾回收器清理对象就能够释放资源，这一点不同于对外资源(如文件流)的释放
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (Exception e) {

        }
        return null;
    }
}
