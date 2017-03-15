package com.poojithjain.iotinsight;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.BarChartView;
import com.db.chart.view.LineChartView;
import com.poojithjain.iotinsight.util.app.AlarmService;
import com.poojithjain.iotinsight.util.app.DeviceService;
import com.poojithjain.iotinsight.util.app.AppConstants;
import com.poojithjain.iotinsight.util.app.AppSharedPreferences;
import com.poojithjain.iotinsight.util.net.api.FitbitAPITask;
import com.poojithjain.iotinsight.util.net.data.RequestType;
import com.poojithjain.iotinsight.util.net.model.AlarmData;
import com.poojithjain.iotinsight.util.net.model.BatteryLevel;
import com.poojithjain.iotinsight.util.net.model.DeviceData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private Context context = this;
    //TODO replace placeholder info
    private String deviceInfo;
    private String date = "August 2016";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout layout = (LinearLayout) findViewById(R.id.content_main);

        if (getIntent().getBooleanExtra("refresh", false)) {
            Snackbar.make(layout, "Refreshing data ...", Snackbar.LENGTH_LONG).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.putExtra("refresh", true);
                startActivity(intent);
            }
        });


        redirectAuthURI();
        handleBatteryService();
        handleAlarmService();
        handleBatteryStats();
        handleAlarmStats();
        // TODO get user info from API.
        userInfo();

    }

    private void handleAlarmStats() {
        List<AlarmData> alarmData = AlarmData.listAll(AlarmData.class);

        // Dynamically display data
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.alarmParentLayout);

        // Handle total
        int count = alarmData.size();
        int totalBuzzCount = 0;
        int totalAlarmDays = 0;
        List<Integer> alarmCounts = new ArrayList<>();

        for (AlarmData alarm : alarmData) {
            totalBuzzCount += alarm.getSnoozeCount();
            totalAlarmDays = totalAlarmDays | alarm.getDays();
            alarmCounts.add((AppConstants.getHumanAlarmDays(alarm.getDays()).length()  + 1) / 4);
        }

        View v = layoutInflater.inflate(R.layout.alarm_item, null);

        TextView numAlarmsTextView = (TextView) v.findViewById(R.id.numAlarms);
        if (count == 1)
            numAlarmsTextView.setText(String.format("%d alarm", count));
        else
            numAlarmsTextView.setText(String.format("%d alarms", count));

        numAlarmsTextView.setTextColor(getResources().getColor(R.color.Teal));

        TextView totalAlarmDaysTextView = (TextView) v.findViewById(R.id.alarmsDays);
        String totalStringDays = AppConstants.getHumanAlarmDays(totalAlarmDays);
        totalAlarmDaysTextView.setText("Alarm set on: " + totalStringDays);

        TextView totalBuzzStatsTextView = (TextView) v.findViewById(R.id.buzzStats);
        totalBuzzStatsTextView.setText(AppConstants.getBuzzStats(totalBuzzCount, alarmCounts));


        insertPoint.addView(v, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        // handle individual
        for (AlarmData alarm : alarmData) {
            View view = layoutInflater.inflate(R.layout.alarm_item, null);
            int buzzCount = 0;
            int alarmDays = 0;

            buzzCount += alarm.getSnoozeCount();
            alarmDays = alarmDays | alarm.getDays();

            TextView timeTextView = (TextView) view.findViewById(R.id.numAlarms);
            timeTextView.setText(alarm.getAlarmTime());
            timeTextView.setTextColor(getResources().getColor(R.color.Teal));

            TextView alarmDaysTextView = (TextView) view.findViewById(R.id.alarmsDays);
            String stringDays = AppConstants.getHumanAlarmDays(alarmDays);
            alarmDaysTextView.setText("Alarm set on: " + stringDays);

            TextView buzzStatsTextView = (TextView) view.findViewById(R.id.buzzStats);
            buzzStatsTextView.setText(AppConstants.getBuzzStats(buzzCount, stringDays));
            insertPoint.addView(view, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    private void handleBatteryStats() {

        long priorTime = System.currentTimeMillis() - (10 * 60 * 60 * 1000);

        List<DeviceData> results = DeviceData.find(DeviceData.class, "last_Sync_Time > ? ", String.valueOf(priorTime));

        if(results != null) {
            Map<String, Float> chartData = new HashMap<>();
            Map<String, Float> syncData = new HashMap<>();

//            Log.e("Device info", deviceInfo);
            Log.e("Status", "CIMUNG HEREEEE");
            long lastSyncTime = results.get(0).getLastSyncTime();

            Log.e("Another status", "YOOOOO HEREEEE");

            for (DeviceData result : results) {
                Calendar calendar = Calendar.getInstance();
                long time = result.getLastSyncTime();
                calendar.setTimeInMillis(time);
                String thisKey = String.format("%d ", calendar.get(Calendar.HOUR_OF_DAY));

                Float thisValue = (float) BatteryLevel.valueOf(result.getBattery()).getValue();

                deviceInfo = result.getDeviceVersion();

                if (lastSyncTime != time) {
                    if(syncData.containsKey(thisKey)) {
                        syncData.put(thisKey, syncData.get(thisKey) + 1);
                    } else {
                        syncData.put(thisKey, 1.0f);
                    }
                    lastSyncTime = time;
                }
                chartData.put(thisKey, thisValue);
            }
            Log.e("Size of result", String.valueOf(chartData.size()));


            LineChartView chartView = (LineChartView) findViewById(R.id.battery_linechart);
            LineSet dataset = new LineSet();

            Map<String, Float> treeMap = new TreeMap<>(chartData);

            for (String key : treeMap.keySet()) {
                dataset.addPoint(key, chartData.get(key));
            }

            dataset.setColor(getResources().getColor(R.color.Crimson));
            dataset.setDotsColor(getResources().getColor(R.color.Yellow));
            dataset.setDotsStrokeColor(getResources().getColor(R.color.MidnightBlue));
            dataset.setDotsStrokeThickness(9f);
            dataset.setThickness(14f);
            chartView.setAxisBorderValues(0, 100, 20);
            chartView.addData(dataset);
            chartView.show();
//
            BarChartView barChartView = (BarChartView) findViewById(R.id.sync_barchart);
            BarSet barSet = new BarSet();

            Map<String, Float> treeSyncMap = new TreeMap<>(syncData);

            Log.e("Length", String.valueOf(syncData.size()));

            for (String key : treeSyncMap.keySet()) {
                barSet.addBar(key, syncData.get(key));
                Log.e("Bar value", String.valueOf(syncData.get(key)));
            }
//
            barSet.setColor(getResources().getColor(R.color.LimeGreen));
            barChartView.setBarBackgroundColor(getResources().getColor(R.color.Silver));
            barChartView.setAxisBorderValues(0,10,2);
            barChartView.setBarSpacing(22f);
            barChartView.addData(barSet);
            barChartView.setRoundCorners(28f);
            barChartView.show();
        }
    }

    private void userInfo() {
        String device = "\n Device name: " + deviceInfo;
        String functioning = "\n Device active since: " + date;
        String placeHolderUserInfo = "\n Owner: Narendra Nath Joshi \n Location: Pittsburgh, PA";
        TextView text = (TextView) findViewById(R.id.userInfo);
        text.setText(device + functioning + placeHolderUserInfo);
    }

    private void handleBatteryService() {
        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(getApplicationContext(), DeviceService.class), PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUp) {
            Calendar cal = Calendar.getInstance();
            Intent serviceIntent = new Intent(MainActivity.this, DeviceService.class);
            PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, serviceIntent, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // schedule every minute
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60 * 1000, pintent);
        }
    }

    private void handleAlarmService() {
        boolean alarmUp = (PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(getApplicationContext(), DeviceService.class), PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUp) {
            Calendar cal = Calendar.getInstance();
            Intent serviceIntent = new Intent(MainActivity.this, AlarmService.class);
            PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, serviceIntent, 0);
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            // schedule every minute
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 24 * 60 * 60 * 1000, pintent);
        }
    }

    private void redirectAuthURI() {
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            // Collect auth code and store it
            SharedPreferences.Editor editor = AppSharedPreferences.getInstance().editor(context);
            editor.putString("authCode", uri.getQueryParameter("code").trim());
            editor.commit();
            Log.i("Auth code", AppSharedPreferences.getInstance().getSharedPreferences(context).getString("authCode", ""));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            new FitbitAPITask(context, RequestType.Devices).execute();
            return true;

        } else if (id == R.id.action_login) {
//            if (!AppSharedPreferences.getInstance().getSharedPreferences(context).getBoolean("loginStatus", false)) {
                String url = "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=2285P6&prompt=consent&scope=settings profile heartrate";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(url));

            try {
                Thread.sleep(8 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new FitbitAPITask(context, RequestType.AccessToken).execute();
                return true;
//            } else {
//                Toast.makeText(context, "You are already logged in", Toast.LENGTH_LONG).show();
//                return true;
//            }
        } else if (id == R.id.action_alarm) {
            new FitbitAPITask(context, RequestType.Alarm).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
