package com.poojithjain.iotinsight.util.net.data;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.poojithjain.iotinsight.util.net.data.TrackerAlarm;

public class FitbitAlarms {

    @SerializedName("trackerAlarms")
    @Expose
    private List<TrackerAlarm> trackerAlarms = null;

    public FitbitAlarms() {
    }

    public FitbitAlarms(List<TrackerAlarm> trackerAlarms) {
        super();
        this.trackerAlarms = trackerAlarms;
    }

    public List<TrackerAlarm> getTrackerAlarms() {
        return trackerAlarms;
    }

    public void setTrackerAlarms(List<TrackerAlarm> trackerAlarms) {
        this.trackerAlarms = trackerAlarms;
    }

}