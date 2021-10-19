package com.yctc.zhiting.event;

public class SceneEffectTimeEvent {

    private int time_period;
    private String timeStr;
    private long effect_start_time;
    private long effect_end_time;
    private int repeat_type;
    private String repeat_date;
    private String repeatDateStr;



    public SceneEffectTimeEvent(int time_period, String timeStr, long effect_start_time, long effect_end_time, int repeat_type, String repeat_date, String repeatDateStr) {
        this.time_period = time_period;
        this.timeStr = timeStr;
        this.effect_start_time = effect_start_time;
        this.effect_end_time = effect_end_time;
        this.repeat_type = repeat_type;
        this.repeat_date = repeat_date;
        this.repeatDateStr = repeatDateStr;
    }

    public int getTime_period() {
        return time_period;
    }

    public void setTime_period(int time_period) {
        this.time_period = time_period;
    }

    public String getTimeStr() {
        return timeStr;
    }

    public void setTimeStr(String timeStr) {
        this.timeStr = timeStr;
    }

    public long getEffect_start_time() {
        return effect_start_time;
    }

    public void setEffect_start_time(long effect_start_time) {
        this.effect_start_time = effect_start_time;
    }

    public long getEffect_end_time() {
        return effect_end_time;
    }

    public void setEffect_end_time(long effect_end_time) {
        this.effect_end_time = effect_end_time;
    }

    public int getRepeat_type() {
        return repeat_type;
    }

    public void setRepeat_type(int repeat_type) {
        this.repeat_type = repeat_type;
    }

    public String getRepeat_date() {
        return repeat_date;
    }

    public void setRepeat_date(String repeat_date) {
        this.repeat_date = repeat_date;
    }

    public String getRepeatDateStr() {
        return repeatDateStr;
    }

    public void setRepeatDateStr(String repeatDateStr) {
        this.repeatDateStr = repeatDateStr;
    }
}
