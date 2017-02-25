package com.poojithjain.iotinsight.util.net.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.poojithjain.iotinsight.util.app.AppConstants;
import com.poojithjain.iotinsight.util.app.AppSharedPreferences;
import com.poojithjain.iotinsight.util.net.data.AuthResponseBody;
import com.poojithjain.iotinsight.util.net.data.FitbitAlarms;
import com.poojithjain.iotinsight.util.net.data.FitbitDevice;
import com.poojithjain.iotinsight.util.net.data.RequestType;
import com.poojithjain.iotinsight.util.net.data.TrackerAlarm;

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
    private static int trackerID;

    public FitbitAPITask(Context context, RequestType requestType) {
        this.context = context;
        this.requestType = requestType;
    }

    @Override
    protected void onPreExecute() {
        api = AppConstants.initAPI(context);
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try{

            if (requestType == RequestType.Alarm) {
                // TODO get trackerId
                Response<FitbitAlarms> alarms = api.getAlarm(trackerID).execute();
                FitbitAlarms alarm = alarms.body();
                TrackerAlarm trackerAlarm = alarm.getTrackerAlarms().get(0);
                Log.i("Alarm time", trackerAlarm.getTime());
                List<String> alarmDays = trackerAlarm.getWeekDays();
                Log.i("Alarm days" , alarmDays.toString());

            } else if (requestType == RequestType.Devices) {
                Response<List<FitbitDevice>> res = api.getDevices().execute();
                List<FitbitDevice> devices = res.body();
                FitbitDevice device = devices.get(0);
                trackerID = Integer.parseInt(device.getId());
                Log.i("Battery", device.getBattery());
                Log.i("Sync time", device.getLastSyncTime());
                Log.i("Tracker ID", String.valueOf(trackerID));

            } else if (requestType == RequestType.AccessToken) {
                Thread.sleep(3000);
                Map<String, String> body = new HashMap<>();
                body.put("client_id", "2285P6");
                body.put("grant_type", "authorization_code");
                String authCode = AppSharedPreferences.getInstance().getSharedPreferences(context).getString("authCode", "").trim();
                body.put("code", authCode);
                Log.d("Check auth code", authCode);

                Response<AuthResponseBody> authResponse = api.getTokens(body).execute();
                AuthResponseBody responseBody = authResponse.body();

                SharedPreferences.Editor editor = AppSharedPreferences.getInstance().editor(context);
                editor.putString("accessToken", responseBody.getAccessToken());
                Log.d("getAccessToken()", responseBody.getAccessToken());
                editor.putInt("expiresIn", responseBody.getExpiresIn());
                Log.d("getExpiresIn()", String.valueOf(responseBody.getExpiresIn()));
                editor.putString("refreshToken", responseBody.getRefreshToken());
                Log.d("getRefreshToken()", responseBody.getRefreshToken());
                editor.putString("tokenType", responseBody.getTokenType());
                Log.d("getTokenType()", responseBody.getTokenType());
                editor.putString("userId", responseBody.getUserId());
                Log.d("getUserId()", responseBody.getUserId());
                editor.putBoolean("loginStatus", true);
                editor.commit();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
