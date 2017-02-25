package com.poojithjain.iotinsight.util.net.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.poojithjain.iotinsight.util.app.AppConstants;
import com.poojithjain.iotinsight.util.app.AppSharedPreferences;
import com.poojithjain.iotinsight.util.net.data.AuthResponseBody;
import com.poojithjain.iotinsight.util.net.data.FitbitDevice;
import com.poojithjain.iotinsight.util.net.data.RequestType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

/**
 * Created by Poojith on 20-02-2017.
 */

public class FitbitAPITask extends AsyncTask {
    private Context context;
    private FitbitAPI api;
    private RequestType requestType;

    public FitbitAPITask(Context context, RequestType requestType) {
        this.context = context;
        this.requestType = requestType;
    }

    @Override
    protected void onPreExecute() {
        api = AppConstants.getInstance(context).initAPI();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{
            if (requestType == RequestType.Alarm) {
                // TODO get trackerId
                int trackerID = 0; // getTrackerId();
                api.getAlarm(trackerID).execute();

            } else if (requestType == RequestType.Devices) {
                Response<List<FitbitDevice>> res = api.getDevices().execute();
                Log.d("api", res.body().toString());
                Toast.makeText(context, res.body().get(0).getDeviceVersion(), Toast.LENGTH_LONG).show();

            } else if (requestType == RequestType.AccessToken) {
                Map<String, String> body = new HashMap<>();
                body.put("client_id", "2285P6");
                body.put("grant_type", "authorization_code");
                body.put("code", AppConstants.getInstance(context).getAuthorizationCode());

                Response<AuthResponseBody> authResponse = api.getTokens(body).execute();
                AuthResponseBody responseBody = authResponse.body();
                AppSharedPreferences.getInstance().editor(context).putString("accessToken",  responseBody.getAccessToken());
                Log.d("getAccessToken()", responseBody.getAccessToken());
                AppSharedPreferences.getInstance().editor(context).putInt("expiresIn",  responseBody.getExpiresIn());
                Log.d("getExpiresIn()", String.valueOf(responseBody.getExpiresIn()));
                AppSharedPreferences.getInstance().editor(context).putString("refreshToken",  responseBody.getRefreshToken());
                Log.d("getRefreshToken()", responseBody.getRefreshToken());
                AppSharedPreferences.getInstance().editor(context).putString("tokenType",  responseBody.getTokenType());
                Log.d("getTokenType()", responseBody.getTokenType());
                AppSharedPreferences.getInstance().editor(context).putString("userId",  responseBody.getUserId());
                Log.d("getUserId()", responseBody.getUserId());
                Toast.makeText(context, "Did something", Toast.LENGTH_LONG).show();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}
