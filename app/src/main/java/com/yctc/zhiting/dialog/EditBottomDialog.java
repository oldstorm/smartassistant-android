package com.yctc.zhiting.dialog;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.app.main.framework.baseutil.UiUtil;
import com.app.main.framework.baseutil.toast.ToastUtil;
import com.app.main.framework.dialog.CommonBaseDialog;
import com.yctc.zhiting.R;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class EditBottomDialog extends CommonBaseDialog {

    private String title;
    private String hint;
    private String content;

    /**
     * 0 修改昵称
     * 1 添加/修改房间区域
     * 2 修改家庭/公司名称
     */
    private int from;

    @BindView(R.id.tvRoom)
    TextView tvRoom;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvSave)
    TextView tvSave;

    public static EditBottomDialog newInstance(String title, String hint, String content, int from) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("hint", hint);
        args.putString("content", content);
        args.putInt("from", from);

        EditBottomDialog fragment = new EditBottomDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.EditDialog);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_edit_bottom;
    }

    @Override
    protected int obtainWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    protected void initArgs(Bundle arguments) {
        title = arguments.getString("title");
        hint = arguments.getString("hint");
        content = arguments.getString("content");
        from = arguments.getInt("from");
    }

    @Override
    protected void initView(View view) {
        tvRoom.setText(title);
        etContent.setHint(hint);
        switch (from) {
            case 0:
                etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                break;

            case 1:
                etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                break;

            case 2:
                etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(30)});
                break;
        }
        etContent.setText(content);
        etContent.requestFocus();
        showInput(etContent);
        tvSave.setEnabled(!TextUtils.isEmpty(etContent.getText().toString().trim()));
    }

    @OnClick(R.id.ivClose)
    void onClickClose() {
        dismiss();
    }

    @OnClick(R.id.tvSave)
    void onClickSave() {
        String str = etContent.getText().toString();
        if (TextUtils.isEmpty(str.trim())) {
            ToastUtil.show(hint);
            return;
        }
        switch (from) {
            case 0:  // 修改昵称
                if (str.length() < 6) {
                    ToastUtil.show(UiUtil.getString(R.string.mine_greater_six));
                    return;
                }
                break;
            case 1: // 添加房间/区域
                break;
        }
        hideInput(etContent);
        if (clickSaveListener != null) {
            clickSaveListener.onSave(etContent.getText().toString());
            if(isShowing()){
                dismiss();
            }
        }
    }

    @OnTextChanged(R.id.etContent)
    void onContentChange() {
        tvSave.setEnabled(!TextUtils.isEmpty(etContent.getText().toString().trim()));
    }

    private OnClickSaveListener clickSaveListener;

    public EditBottomDialog setClickSaveListener(OnClickSaveListener clickSaveListener) {
        this.clickSaveListener = clickSaveListener;
        return this;
    }

    public interface OnClickSaveListener {
        void onSave(String content);
    }
}
