package com.poojithjain.iotinsight.util.net.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FitbitDevice {

    @SerializedName("battery")
    @Expose
    private String battery;
    @SerializedName("deviceVersion")
    @Expose
    private String deviceVersion;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lastSyncTime")
    @Expose
    private String lastSyncTime;
    @SerializedName("type")
    @Expose
    private String type;

    public FitbitDevice() {
    }

    public FitbitDevice(String battery, String deviceVersion, String id, String lastSyncTime, String type) {
        super();
        this.battery = battery;
        this.deviceVersion = deviceVersion;
        this.id = id;
        this.lastSyncTime = lastSyncTime;
        this.type = type;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(String lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}