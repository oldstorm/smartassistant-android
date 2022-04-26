package com.yctc.zhiting.dialog;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.main.framework.dialog.CommonBaseDialog;
import com.happy.todo.calendarview.CalendarCellDecorator;
import com.happy.todo.calendarview.CalendarPickerView;
import com.happy.todo.calendarview.CalendarRowView;
import com.happy.todo.calendarview.CalendarSelectionMode;
import com.happy.todo.calendarview.CustomCalendarCellDecorator;
import com.yctc.zhiting.R;
import com.yctc.zhiting.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class CalendarSelectDialog extends CommonBaseDialog {
    @BindView(R.id.calendar_view)
    CalendarPickerView mCalendarView;
    @BindView(R.id.day_names_header_row)
    CalendarRowView dayNamesHeaderRow;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.tvClear)
    TextView tvClear;
    @BindView(R.id.ivClose)
    ImageView ivClose;
    @Override
    protected void initView(View view) {
        initCalendarPickerView();
        tvClear.setOnClickListener(v -> {
            mCalendarView.clearSelectedDates();
            hideAnim(btnNext);
        });
        ivClose.setOnClickListener(v -> dismiss());

        btnNext.setOnClickListener(v -> {
            if(listener != null && (mCalendarView.getSelectedDates() != null && mCalendarView.getSelectedDates().size() >= 2)) {
                listener.onDateSelected(mCalendarView.getSelectedDates().get(0),mCalendarView.getSelectedDates().get(mCalendarView.getSelectedDates().size() - 1));
            }
            dismiss();
        });
    }

    //初始化日历
    private void initCalendarPickerView() {
        String[] weeks = new String[]{
                getString(R.string.week_title1),
                getString(R.string.week_title2),
                getString(R.string.week_title3),
                getString(R.string.week_title4),
                getString(R.string.week_title5),
                getString(R.string.week_title6),
                getString(R.string.week_title7)};
        for (int offset = 0; offset < 7; offset++) {
            TextView textView = (TextView) dayNamesHeaderRow.getChildAt(offset);
            if(offset == 0 || offset == 6){
                textView.setTextColor(getResources().getColor(R.color.color_F3A934));
            }
            textView.setText(weeks[offset]);
        }

        List<CalendarCellDecorator> decorators = new ArrayList<>();
        CustomCalendarCellDecorator decorator = new CustomCalendarCellDecorator();
        decorators.add(decorator);
        mCalendarView.setDecorators(decorators);
        //默认设置只显示可选择部分的日历
//        mCalendarView.setIsOnlyShowUsefulWeeks(true, false);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        //设置日历为范围模式
        mCalendarView.init(today, nextYear.getTime())
                .inMode(CalendarSelectionMode.RANGE);
        //设置日历默认选中的日期
        /*Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 1);
        mCalendarView.selectDate(c.getTime(), true);*/

        mCalendarView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                if (mCalendarView.getSelectedDates() != null && mCalendarView.getSelectedDates().size() >= 2) {
                    showAnim(btnNext);
                } else {
                    hideAnim(btnNext);
                }
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
    }

    /**
     * 隐藏控件
     *
     * @param view
     */
    public void hideAnim(View view) {
        if (view.getVisibility() != View.VISIBLE) {
            return;
        }

        List<Date> selectedDate = mCalendarView.getSelectedDates();
        if (CollectionUtil.isEmpty(selectedDate) || selectedDate.size() <= 1) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1, 0f);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0, 400f);

            AnimatorSet hideSet = new AnimatorSet();  //组合动画
            hideSet.playTogether(alpha, translationY); //设置动画
            hideSet.setDuration(100);  //设置动画时间
            hideSet.start(); //启动
            hideSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                }
            });
        }

    }


    /**
     * 显示控件
     *
     * @param view
     */
    public void showAnim(View view) {
        if (view.getVisibility() != View.GONE) {
            return;
        }
        List<Date> selectedDates = mCalendarView.getSelectedDates();
        if (!CollectionUtil.isEmpty(selectedDates) && selectedDates.size() > 1) {
            view.setVisibility(View.VISIBLE);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0, 1f);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 400f, 0);
            AnimatorSet showSet = new AnimatorSet();  //组合动画
            showSet.playTogether(alpha, translationY); //设置动画
            showSet.setDuration(400);  //设置动画时间
            showSet.start(); //启动
            showSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    List<Date> selectedDates1 = mCalendarView.getSelectedDates();
                    if (!CollectionUtil.isEmpty(selectedDates1)) {
                        //主动跳转到最后一个所选日期
                        mCalendarView.scrollToDate(selectedDates1.get(selectedDates1.size() - 1), true);
                    }
                }
            });
        }
    }

    @Override
    protected void initArgs(Bundle arguments) {

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_calendar_select;
    }

    @Override
    protected int obtainWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int obtainHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected int obtainGravity() {
        return Gravity.BOTTOM;
    }

    private OnDateSelectedListener listener;

    public void setListener(OnDateSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(Date dateIn, Date dateOut);
    }
}
