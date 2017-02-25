package com.poojithjain.iotinsight.util.net.api;

import com.poojithjain.iotinsight.util.net.data.FitbitDevice;
import com.poojithjain.iotinsight.util.net.data.FitbitAlarms;
import com.poojithjain.iotinsight.util.net.data.AuthResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Poojith on 20-02-2017.
 */

public interface FitbitAPI {
    @GET("1/user/-/devices.json")
    Call<List<FitbitDevice>> getDevices();

    @GET("1/user/-/devices/tracker/{trackerId}/alarms.json")
    Call<FitbitAlarms> getAlarm(@Path("trackerId") int trackerId);


    @POST("/oauth2/token")
    @FormUrlEncoded
    Call<AuthResponseBody> getTokens(@FieldMap Map<String, String> body);

}
