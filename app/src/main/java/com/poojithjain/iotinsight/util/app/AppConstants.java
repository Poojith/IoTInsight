package com.poojithjain.iotinsight.util.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.poojithjain.iotinsight.MainActivity;
import com.poojithjain.iotinsight.util.net.api.FitbitAPI;

import java.io.IOException;

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
    private static AppConstants appConstants;
    private static Context context;

    public static AppConstants getInstance(Context context) {
        if (appConstants == null) {
            appConstants = new AppConstants();
            AppConstants.context = context;
        }
        return appConstants;
    }

    public String[] tabTitles = new String[]{"Battery", "Sync", "Alarm"};
    private final String BASE_URL = "https://api.fitbit.com/";
    private String authorizationCode;

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    /**
     * Gets the first run auth for getting tokens
     *
     * @return
     */
    public String getB64Auth () {
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
    public String getAuth (String tokenType, String accessToken) {
        String B64String = String.format(
                "%s %s", tokenType, Base64.encodeToString(accessToken.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));
        return B64String;
    }

    public FitbitAPI initAPI() {
        final SharedPreferences sharedPrefs = AppSharedPreferences.getInstance().getSharedPreferences(context);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder ongoing = chain.request().newBuilder();

                // toggle between auth code and access token
                if (sharedPrefs.getBoolean("firstRun", true)) {
                    String authorizationCode = getB64Auth();
                    ongoing.addHeader("Authorization", authorizationCode);
                    ongoing.addHeader("Content-Type", "application/x-www-form-urlencoded");
                    AppSharedPreferences.getInstance().editor(context).putBoolean("firstRun", false);

                } else {
                    ongoing.addHeader("Authorization",
                            getAuth(
                                    sharedPrefs.getString("tokenType", ""),
                                    sharedPrefs.getString("accessToken", "")
                            )
                    );
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
}
