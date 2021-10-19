package com.app.main.framework.imageutil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Base64;
import android.util.Size;

import com.app.main.framework.baseutil.LogUtil;
import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.fileutil.BaseFileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BaseBitmapUtil {
    /**
     * 创建一个图片
     *
     * @param contentColor 内部填充颜色
     * @param strokeColor  描边颜色
     * @param radius       圆角
     * @param strokeWidth  描边的宽度
     */
    public static GradientDrawable createDrawable(int contentColor, int strokeColor, int radius, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable(); // 生成Shape
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT); // 设置矩形
        drawable.setColor(contentColor);// 内容区域的颜色
        drawable.setStroke(strokeWidth, strokeColor); // 四周描边,描边后四角真正为圆角，不会出现黑色阴影
        drawable.setCornerRadius(radius); // 设置四角都为圆角
        return drawable;
    }

    /**
     * @param contentColor 图片的颜色
     * @param radius       圆角角度
     * @return
     */
    public static GradientDrawable createDrawable(int contentColor, int radius) {
        return createDrawable(contentColor, contentColor, radius, 1);
    }

    /**
     * 创建一个图片选择器
     *
     * @param normalState  普通状态的图片
     * @param pressedState 按压状态的图片
     */
    public static StateListDrawable createSelector(Drawable normalState, Drawable pressedState) {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressedState);
        bg.addState(new int[]{android.R.attr.state_enabled}, normalState);
        bg.addState(new int[]{}, normalState);
        return bg;
    }


    public static Bitmap getSmallBitmap(String filePath) {
        return getSmallBitmap(filePath, 720);
    }

    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth) {
        //看图片是否有旋转
        Bitmap bitmap = null;

        int degree = readPictureDegree(filePath);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int hight = reqWidth * options.outHeight / options.outWidth;
        options.inSampleSize = calculateInSampleSize(options, reqWidth, hight);
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);

        if (degree != 0) {
            bitmap = rotaingImageView(degree, bitmap);

            LogUtil.d("图片旋转了%d度" + degree);
        }

        return bitmap;
    }


    /**
     * 根据路径获得图片并压缩，返回bitmap用于显示
     */
    public static Bitmap zoomBitmapFromBitmap(Bitmap oldBitmap, float rate) {

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(rate, rate);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix,
                true);
        return newbm;
    }


    /**
     * 获取Drawable的大小
     *
     * @param drawable
     * @return int 单位 byte
     */
    public static int getDrawableSize(Drawable drawable) {
        if (drawable == null) {
            return 0;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return getBitmapSize(bitmap);
    }

    /**
     * 获取Bitmap图片的大小
     *
     * @param bitmap
     * @return int 单位 byte
     * todo 不知道为啥计算不对的，以后再看下
     */
    public static int getBitmapSize(Bitmap bitmap) {

//        return bitmap.getAllocationByteCount();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 图片压缩，质量压缩法
     *
     * @param bitmap  //原图
     * @param maxSize //压缩后能够达到的最大尺寸, 单位KB
     */
    public static Bitmap compressBmpToBytes(Bitmap bitmap, int maxSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        LogUtil.i("baos.toByteArray().length----%dKB" + baos.toByteArray().length / 1024);
        while ((baos.toByteArray().length / 1024) > maxSize && options > 10) {
            baos.reset();
            options -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            LogUtil.i("baos.toByteArray().length----%dKB" + baos.toByteArray().length / 1024);
        }

        byte[] bytes = baos.toByteArray();
        Bitmap compressBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        BaseFileUtil.close(baos);
        return compressBitmap;
    }

    /**
     * 质量压缩，覆盖原来的图片
     *
     * @param filePath
     * @param maxSize
     * @return
     */
    public static Bitmap compressBmpToBytes(String filePath, int maxSize) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
        LogUtil.i("baos.toByteArray().length1----%dKB" + baos.toByteArray().length / 1024);
        while ((baos.toByteArray().length / 1024) > maxSize && options > 10) {
            baos.reset();
            options -= 5;
            LogUtil.i("baos.toByteArray().length3----%dKB" + options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            LogUtil.i("baos.toByteArray().length2----%dKB" + baos.toByteArray().length / 1024);
        }

        byte[] bytes = baos.toByteArray();
        Bitmap compressBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        BaseFileUtil.close(baos);

        //覆盖原来图片路径
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            compressBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        } catch (FileNotFoundException | NullPointerException e) {
            e.printStackTrace();
        }
        BaseFileUtil.close(outputStream);
        return compressBitmap;
    }

    /**
     * @param reqWidth  压缩后的宽
     * @param reqHeight 压缩后的高
     * @return 图片的压缩比
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }


    /**
     * 图片转成string
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] appicon = baos.toByteArray();// 转为byte数组
        return Base64.encodeToString(appicon, Base64.DEFAULT);
    }

    /**
     * 将字符串转换成Bitmap类型
     */
    public static Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);

            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public static Size getRealImageSize(String path) {
        int orientation = getImageOrientation(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_270:
                return new Size(height, width);
            default:
                return new Size(width, height);
        }
    }

    public static int getImageOrientation(String imageLocalPath) {
        try {
            ExifInterface exifInterface = new ExifInterface(imageLocalPath);
            return exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
            return ExifInterface.ORIENTATION_NORMAL;
        }
    }

    /**
     * 获取图片旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;

    }


    /**
     * 图片旋转
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(angle);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }


    /*
     * bitmapToDrawble
     * */
    public static Drawable bitmapToDrawble(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(UiUtil.getContext().getResources(), bitmap);
        return drawable;
    }

    public static Bitmap getLocalBitmap(String url) {
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(url, options);
            return bm;  ///把流转化为Bitmap图片

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
