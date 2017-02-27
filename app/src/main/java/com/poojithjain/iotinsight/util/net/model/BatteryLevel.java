package com.poojithjain.iotinsight.util.net.model;

/**
 * Created by poojith on 2/26/17.
 */

public enum BatteryLevel {
    High(90),
    Medium(60),
    Low(30),
    Empty(0);

    public int val;

   BatteryLevel(int val) {
        this.val = val;
    }

    public int getValue() {
        return this.val;
    }
}
