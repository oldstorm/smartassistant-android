package com.app.main.framework.baseutil;

import android.graphics.Rect;
import android.view.View;

public class KeyBoardUtil {
    private static KeyBoardUtil util = new KeyBoardUtil();
    public static KeyBoardUtil getInstance(){
        return util;
    }
    /**
     * 虚拟键盘高度
     */
    int virtualKeyboardHeight;
    /**
     * 屏幕高度
     */
    int screenHeight;
    /**
     * 屏幕6分之一的高度，作用是防止获取到虚拟键盘的高度
     */
    int screenHeight6;
    View rootView;

    private KeyBoardUtil() {
        /**
         * 获取屏幕的高度,该方式的获取不包含虚拟键盘
         */
        screenHeight = UiUtil.getScreenHeight();
        screenHeight6 = screenHeight / 6;
        rootView = LibLoader.getCurrentActivity().getWindow().getDecorView();
    }

    /**
     * @param listener
     */
    public void setOnKeyboardChangeListener(final KeyboardChangeListener listener) {
        //当键盘弹出隐藏的时候会 调用此方法。
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            /**
             * 回调该方法时rootView还未绘制，需要设置绘制完成监听
             */
            rootView.post(() -> {
                Rect rect = new Rect();
                /**
                 * 获取屏幕底部坐标
                 */
                rootView.getWindowVisibleDisplayFrame(rect);
                /**
                 * 获取键盘的高度
                 */
                int heightDifference = screenHeight - rect.bottom;
                LogUtil.i(heightDifference + "");
                if (heightDifference < screenHeight6) {
                    virtualKeyboardHeight = heightDifference;
                    if (listener != null) {
                        listener.onKeyboardHide();
                    }
                } else {
                    if (listener != null) {
                        listener.onKeyboardShow(heightDifference - virtualKeyboardHeight);
                    }
                }
            });
        });
    }
    /**
     * 软键盘状态切换监听
     */
    public interface KeyboardChangeListener {
        /**
         * 键盘弹出
         *
         * @param keyboardHight 键盘高度
         */
        void onKeyboardShow(int keyboardHight);
        /**
         * 键盘隐藏
         */
        void onKeyboardHide();
    }
}