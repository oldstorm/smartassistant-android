package com.yctc.zhiting.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.yctc.zhiting.R;

import java.util.List;

public class TagTextView extends AppCompatTextView {

    private Context mContext;
    private TextView tvTag;
    private StringBuffer contentBuffer;

    public TagTextView(@NonNull Context context) {
        this(context, null);
    }

    public TagTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setContentAndTag(String content, List<String> tags){
        contentBuffer = new StringBuffer();
        contentBuffer.append(content);
        for (String item : tags){ // 将每个tag的内容添加到content后边，之后用drawable替代这些tag所占的位置
            contentBuffer.append(item);
        }

        SpannableString spannableString = new SpannableString(contentBuffer);
        int contentLen = content.length();
        for (int i=0; i<tags.size(); i++){
            String item = tags.get(i);
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_tag_text, null);
            tvTag = view.findViewById(R.id.tv_tag);
            tvTag.setText(item);
            Bitmap bitmap = convertViewToBitmap(view);
            Drawable d = new BitmapDrawable(bitmap);
            d.setBounds(0, 0, tvTag.getWidth(), tvTag.getHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM); // 图片将对齐底部边线
            int startIndex;
            int endIndex;
            startIndex = contentLen + getLastLength(tags, i);
            endIndex = startIndex+item.length();
            contentLen = contentLen+item.length();
            spannableString.setSpan(span, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        setText(spannableString);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    private static Bitmap convertViewToBitmap(View view){
        view.measure(View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    private int getLastLength(List<String> list, int maxLength){
        int length = 0;
        for (int i=0; i<maxLength; i++){
            length += list.get(i).length();
        }
        return length;
    }
}
