package com.poojithjain.iotinsight.util.app;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPreferences {
    private static AppSharedPreferences appSharedPreferences;

    // singleton holder
    public static AppSharedPreferences getInstance() {
        if (appSharedPreferences == null) {
            appSharedPreferences = new AppSharedPreferences();
        }
        return appSharedPreferences;
    }

    private String iotInsightPrefs = "iotInsightPrefs";

    public SharedPreferences.Editor editor(Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(iotInsightPrefs, Context.MODE_PRIVATE);

        return sharedpreferences.edit();
    }

    public SharedPreferences getSharedPreferences(Context context) {

        return context.getSharedPreferences(iotInsightPrefs, Context.MODE_PRIVATE);
    }
}