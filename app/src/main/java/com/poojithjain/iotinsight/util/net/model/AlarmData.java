package com.poojithjain.iotinsight.util.net.model;

import com.orm.SugarRecord;

import java.util.Date;
import java.util.List;

/**
 * Created by poojith on 2/25/17.
 */

public class AlarmData extends SugarRecord {
    int days;
    int snoozeCount;
    String alarmTime;

    public AlarmData() {

    }

    public AlarmData(int alarmDays, int count, String time) {
        days = alarmDays;
        snoozeCount = count;
        alarmTime = time;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getSnoozeCount() {
        return snoozeCount;
    }

    public void setSnoozeCount(int snoozeCount) {
        this.snoozeCount = snoozeCount;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }
}
