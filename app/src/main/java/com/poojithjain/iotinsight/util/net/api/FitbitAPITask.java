package com.poojithjain.iotinsight.util.net.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.orm.SugarContext;
import com.poojithjain.iotinsight.util.app.AppConstants;
import com.poojithjain.iotinsight.util.app.AppSharedPreferences;
import com.poojithjain.iotinsight.util.net.data.AuthResponseBody;
import com.poojithjain.iotinsight.util.net.model.AlarmData;
import com.poojithjain.iotinsight.util.net.model.DeviceData;
import com.poojithjain.iotinsight.util.net.data.FitbitAlarms;
import com.poojithjain.iotinsight.util.net.data.FitbitDevice;
import com.poojithjain.iotinsight.util.net.data.RequestType;
import com.poojithjain.iotinsight.util.net.data.TrackerAlarm;

import org.joda.time.DateTime;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        try {

            if (requestType == RequestType.Alarm) {
                Response<FitbitAlarms> alarms = api.getAlarm(trackerID).execute();
                FitbitAlarms alarm = alarms.body();
                TrackerAlarm trackerAlarm1 = alarm.getTrackerAlarms().get(0);

                List<String> alarmDays = trackerAlarm1.getWeekDays();

                Log.d("alarmDays", String.valueOf(alarmDays.size()));

                String[] weekDays = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

                int weekCount = 0;

                for(String weekDay : weekDays) {
                    weekCount = weekCount << 1;

                    if(alarmDays.contains(weekDay)) {
                        weekCount = weekCount | 1;
                    }
                }

                String[] time = trackerAlarm1.getTime().split("-");

                Log.d("WeekCount", String.valueOf(weekCount));
                AlarmData alarmData = new AlarmData(weekCount, trackerAlarm1.getSnoozeCount(), time[0]);
                alarmData.save();

//                AlarmData testData = AlarmData.findById(AlarmData.class, 1);
//                Log.d("Alarm time", testData.getAlarmTime());
//                Log.d("Days", String.valueOf(testData.getDays()));
//                Log.d("Snooze count", String.valueOf(testData.getSnoozeCount()));

            } else if (requestType == RequestType.Devices) {
                Response<List<FitbitDevice>> res = api.getDevices().execute();
                List<FitbitDevice> devices = res.body();
                FitbitDevice device = devices.get(0);
                trackerID = Integer.parseInt(device.getId());

                String deviceVersion = "Fitbit " + device.getDeviceVersion();
                String battery = device.getBattery();
                Date date = new DateTime(device.getLastSyncTime()).toDate();
                Date currentDate = new Date();

                DeviceData deviceData = new DeviceData(deviceVersion, battery, date, currentDate);
                deviceData.save();

                DeviceData testData = DeviceData.findById(DeviceData.class, 1);

                Log.d("Battery", testData.getBattery());
                DateFormat df1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Log.i("Sync time", df1.format(testData.getLastSyncTime()));
                Log.i("Tracker ID", String.valueOf(trackerID));
                Log.i("Creation time", df1.format(testData.getCreationTime()));

            } else if (requestType == RequestType.AccessToken) {
                Thread.sleep(8000);
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
