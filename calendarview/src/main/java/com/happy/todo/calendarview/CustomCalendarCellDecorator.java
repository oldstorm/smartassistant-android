package com.happy.todo.calendarview;

import android.graphics.Color;
import android.util.Log;

import java.util.Date;

/**
 * Created by cxk on 2018/1/9.
 */

public class CustomCalendarCellDecorator implements CalendarCellDecorator {
    @Override
    public void decorate(CalendarCellView cellView, Date date) {
        RangeState rangeState = cellView.getRangeState();
        Log.d("dadadadad","rangeState : " + rangeState.toString() + " ------ isSelectable : " + cellView.isSelectable());
        switch (rangeState) {
            case NONE:
                cellView.getDayOfMonthTextView().setTextColor(Color.parseColor("#3F4663"));
                break;
            case FIRST:
            case LAST:
                if(cellView.isSelectable()){
                    cellView.getDayOfMonthTextView().setTextColor(Color.WHITE);
                }else {
                    cellView.getDayOfMonthTextView().setTextColor(Color.parseColor("#3F4663"));
                }
                break;
            //            case NONE:
//                if (cellView.isSelected()) {
//                    cellView.setBackgroundResource(R.drawable.custom_calendar_bg_selector);
//                } else {
//                    cellView.setBackgroundResource(0);
//                }
//                break;
//
//            case FIRST:
//                cellView.setBackgroundResource(R.drawable.calendar_bg_circle_left);
//                break;
//
//            case MIDDLE:
//                cellView.setBackgroundResource(R.color.blue);
//                break;
//
//            case LAST:
//                cellView.setBackgroundResource(R.drawable.calendar_bg_circle_right);
//                break;
//
//            default:
//                cellView.setBackgroundResource(0);
//                break;
        }
    }
}
