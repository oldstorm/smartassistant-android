package com.app.main.framework.baseutil.toast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.main.framework.R;

public class MToast {

    private static Toast mToast;

    private MToast(Context context, CharSequence text, int duration) {
        View v = LayoutInflater.from(context).inflate(R.layout.toast_framelayout, null);

        TextView textView = v.findViewById(R.id.message);
        if (textView != null) {
            textView.setText(text);
        }

        mToast = Toast.makeText(context, text, duration);
        mToast.setDuration(duration);
        mToast.setView(v);

    }

    public static void cancel(){
        if(mToast!=null){
            mToast.cancel();
        }
    }

    public static MToast makeText(Context context, CharSequence text, int duration) {
        return new MToast(context, text, duration);
    }

    public void show() {
        if (mToast != null) {
            mToast.show();
        }
    }

    public MToast setText(String text) {
        TextView textView = mToast.getView().findViewById(R.id.message);
        textView.setText(text);
        return this;
    }

    public MToast setGravity(int gravity, int xOffset, int yOffset) {
        if (mToast != null) {
            mToast.setGravity(gravity, xOffset, yOffset);
        }
        return this;
    }

    public MToast setDuration(int duration) {
        if (mToast != null) {
            mToast.setDuration(duration);
        }
        return this;
    }
}