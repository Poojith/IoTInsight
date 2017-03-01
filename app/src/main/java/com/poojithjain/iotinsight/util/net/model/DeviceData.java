package com.poojithjain.iotinsight.util.net.model;

import com.orm.SugarRecord;

import java.util.Date;

/**
 * Created by poojith on 2/25/17.
 */

public class DeviceData extends SugarRecord {
    String deviceVersion;
    String battery;
    long lastSyncTime;
    long creationTime;

    public DeviceData() {

    }

    public DeviceData(String deviceVersion, String battery, long date, long currentDate) {
        this.deviceVersion = deviceVersion;
        this.battery = battery;
        this.lastSyncTime = date;
        this.creationTime = currentDate;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public long getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }
}
