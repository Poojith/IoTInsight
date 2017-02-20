package com.poojithjain.iotinsight.util.net;

import com.poojithjain.iotinsight.util.FitbitDevice;
import com.poojithjain.iotinsight.util.FitbitAlarms;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Poojith on 20-02-2017.
 */

public interface FitbitAPI {
    @GET("user/-/devices.json")
    Call<List<FitbitDevice>> getDevices();

    @GET("user/-/devices/tracker/{trackerId}/alarms.json")
    Call<FitbitAlarms> getAlarm(@Path("trackerId") int trackerId);

}
