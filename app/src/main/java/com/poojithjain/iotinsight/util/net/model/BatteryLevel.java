package com.poojithjain.iotinsight.util.net.model;

/**
 * Created by poojith on 2/26/17.
 */

public enum BatteryLevel {
    High(95),
    Medium(70),
    Low(40),
    Empty(10);

    public int val;

   BatteryLevel(int val) {
        this.val = val;
    }

    public int getValue() {
        return this.val;
    }
}
