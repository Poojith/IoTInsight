package com.poojithjain.iotinsight.util;

import com.poojithjain.iotinsight.util.net.FitbitAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Poojith on 19-02-2017.
 */

public class AppConstants {
    public static String[] tabTitles;
    final static String BASE_URL = "https://api.fitbit.com/1/";

    static {
        tabTitles = new String[]{"Battery", "Sync", "Alarm"};
    }

    public static FitbitAPI initAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(FitbitAPI.class);
    }
}
