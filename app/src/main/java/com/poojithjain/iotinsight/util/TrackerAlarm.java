package com.poojithjain.iotinsight.util;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackerAlarm {

    @SerializedName("alarmId")
    @Expose
    private Integer alarmId;
    @SerializedName("deleted")
    @Expose
    private Boolean deleted;
    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("recurring")
    @Expose
    private Boolean recurring;
    @SerializedName("snoozeCount")
    @Expose
    private Integer snoozeCount;
    @SerializedName("snoozeLength")
    @Expose
    private Integer snoozeLength;
    @SerializedName("syncedToDevice")
    @Expose
    private Boolean syncedToDevice;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("vibe")
    @Expose
    private String vibe;
    @SerializedName("weekDays")
    @Expose
    private List<String> weekDays = null;

    public TrackerAlarm() {
    }

    public TrackerAlarm(Integer alarmId, Boolean deleted, Boolean enabled, Boolean recurring, Integer snoozeCount, Integer snoozeLength, Boolean syncedToDevice, String time, String vibe, List<String> weekDays) {
        super();
        this.alarmId = alarmId;
        this.deleted = deleted;
        this.enabled = enabled;
        this.recurring = recurring;
        this.snoozeCount = snoozeCount;
        this.snoozeLength = snoozeLength;
        this.syncedToDevice = syncedToDevice;
        this.time = time;
        this.vibe = vibe;
        this.weekDays = weekDays;
    }

    public Integer getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Integer alarmId) {
        this.alarmId = alarmId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }

    public Integer getSnoozeCount() {
        return snoozeCount;
    }

    public void setSnoozeCount(Integer snoozeCount) {
        this.snoozeCount = snoozeCount;
    }

    public Integer getSnoozeLength() {
        return snoozeLength;
    }

    public void setSnoozeLength(Integer snoozeLength) {
        this.snoozeLength = snoozeLength;
    }

    public Boolean getSyncedToDevice() {
        return syncedToDevice;
    }

    public void setSyncedToDevice(Boolean syncedToDevice) {
        this.syncedToDevice = syncedToDevice;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVibe() {
        return vibe;
    }

    public void setVibe(String vibe) {
        this.vibe = vibe;
    }

    public List<String> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<String> weekDays) {
        this.weekDays = weekDays;
    }
}

