package com.poojithjain.iotinsight.util.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import com.poojithjain.iotinsight.util.net.api.FitbitAPI;

import java.io.IOException;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Poojith on 19-02-2017.
 */

public class AppConstants {
    public static String[] tabTitles = new String[]{"Battery", "Sync", "Alarm"};
    private static final String BASE_URL = "https://api.fitbit.com/";
    private static String authorizationCode;
    public static String[] weekDays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

    public static String getAuthorizationCode() {
        return authorizationCode;
    }

    public static void setAuthorizationCode(String authCode) {
        authorizationCode = authCode;
    }

    /**
     * Gets the first run auth for getting tokens
     *
     * @return
     */
    public static String getB64Auth() {
        String clientID = "2285P6";
        String secret = "95f9df7548b48d371d646070f95cb0ac";
        String source = clientID + ":" + secret;
        String B64String = String.format(
                "Basic %s", Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));
        return B64String;
    }

    /**
     * Gets auth for normal requests
     *
     * @param accessToken
     * @return
     */
    public static String getAccessToken(String tokenType, String accessToken) {
        String authBearer = String.format(
                "%s %s", tokenType, accessToken);
        return authBearer;
    }

    public static FitbitAPI initAPI(final Context context) {
        final SharedPreferences sharedPrefs = AppSharedPreferences.getInstance().getSharedPreferences(context);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder ongoing = chain.request().newBuilder();

                // toggle between auth code and access token
                if (sharedPrefs.getBoolean("firstRun", true)) {
                    String authorizationCode = getB64Auth();
                    System.out.println("Inside IF CONDITIOOOOOOOOON");
                    Log.d("Auth constants", authorizationCode);
                    ongoing.addHeader("Authorization", authorizationCode);
                    ongoing.addHeader("Content-Type", "application/x-www-form-urlencoded");
                    SharedPreferences.Editor editor = AppSharedPreferences.getInstance().editor(context);
                    editor.putBoolean("firstRun", false);
                    editor.commit();

                } else {
                    System.out.println("Inside ELSEEEEE CONDITIOOOOOOOOON");
                    Log.i("Token type", sharedPrefs.getString("tokenType", ""));
                    Log.i("Access token", sharedPrefs.getString("accessToken", ""));

                    ongoing.addHeader("Authorization",
                            getAccessToken(
                                    sharedPrefs.getString("tokenType", ""),
                                    sharedPrefs.getString("accessToken", "")
                            )
                    );
                    Log.i("Bearer token", getAccessToken(
                            sharedPrefs.getString("tokenType", ""),
                            sharedPrefs.getString("accessToken", "")
                    ));
                }
                return chain.proceed(ongoing.build());
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(FitbitAPI.class);
    }

    public static Float getBatteryValue(int value) {
        int min = value - 10 < 0 ? 0 : value - 10;
        int max = value;

        Random rand = new Random();
        return (float) rand.nextInt((max - min) + 1) + min;
    }

    public static String getHumanAlarmDays(int alarmDays) {
        StringBuilder result = new StringBuilder();
        String bin = String.format("%07d", Integer.parseInt(Integer.toBinaryString(alarmDays)));

        Log.e("int", String.valueOf(alarmDays));
        Log.e("bin", bin.toString());

        for (int i = 0; i < weekDays.length; i++) {
            if (bin.charAt(i) == '1') {
                result.append(weekDays[i].substring(0, 3) + ",");
            }
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static String getBuzzStats(int buzzCount, String totalStringDays) {
        int dayCount = (totalStringDays.length() + 1) / 4;

        String weekLine = String.format("Buzzes %d times weekly", (buzzCount + 1) * dayCount);
        String dayLine = String.format("Buzzes average %.2f times daily", (buzzCount + 1) * dayCount / 7.0f);

        return String.format("%s\n%s", dayLine, weekLine);
    }
}
