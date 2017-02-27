package com.poojithjain.iotinsight;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.Toast;

import com.db.chart.model.LineSet;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout layout = (LinearLayout) findViewById(R.id.content_main);

        if (getIntent().getBooleanExtra("refresh", false)) {
            Snackbar.make(layout, "Refreshing Data ...", Snackbar.LENGTH_SHORT).show();
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

        listenURI();
        handleBatteryService();
        handleAlarmService();
        handleBatteryStats();
        handleAlarmStats();
    }

    private void handleAlarmStats() {
        List<AlarmData> alarmData = AlarmData.listAll(AlarmData.class);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup insertPoint = (ViewGroup) findViewById(R.id.alarmParentLayout);


        // handle total
        int count = alarmData.size();
        int totalBuzzCount = 0;
        int totalAlarmDays = 0;
        for (AlarmData alarm: alarmData) {
            totalBuzzCount += alarm.getSnoozeCount();
            totalAlarmDays = totalAlarmDays | alarm.getDays();
        }

        View v = vi.inflate(R.layout.alarm_item, null);

        TextView numAlarmsTextView = (TextView) v.findViewById(R.id.numAlarms);
        numAlarmsTextView.setText(String.format("%d alarms", count));

        TextView totalAlarmDaysTextView = (TextView) v.findViewById(R.id.alarmsDays);
        String totalStringDays = AppConstants.getHumanAlarmDays(totalAlarmDays);
        totalAlarmDaysTextView.setText(totalStringDays);

        TextView totalBuzzStatsTextView = (TextView) v.findViewById(R.id.buzzStats);
        totalBuzzStatsTextView.setText(AppConstants.getBuzzStats(totalBuzzCount, totalStringDays));

        insertPoint.addView(v, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // handle individual
        for (AlarmData alarm: alarmData) {
            View view = vi.inflate(R.layout.alarm_item, null);
            int buzzCount = 0;
            int alarmDays = 0;

            buzzCount += alarm.getSnoozeCount();
            alarmDays = alarmDays | alarm.getDays();

            TextView timeTextView = (TextView) view.findViewById(R.id.numAlarms);
            timeTextView.setText(alarm.getAlarmTime());

            TextView alarmDaysTextView = (TextView) view.findViewById(R.id.alarmsDays);
            String stringDays = AppConstants.getHumanAlarmDays(alarmDays);
            alarmDaysTextView.setText(stringDays);

            TextView buzzStatsTextView = (TextView) view.findViewById(R.id.buzzStats);
            buzzStatsTextView.setText(AppConstants.getBuzzStats(buzzCount, stringDays));
            insertPoint.addView(view, -1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }


    private void handleBatteryStats() {
        long priorTime = System.currentTimeMillis() - (10 * 60 * 60 * 1000);

        List<DeviceData> results = DeviceData.find(DeviceData.class, "last_Sync_Time > ? ", String.valueOf(priorTime));
        Map<String, Float> chartData = new HashMap<>();

        for (DeviceData result: results) {
            Calendar calendar = Calendar.getInstance();
            long time = result.getLastSyncTime();
            calendar.setTimeInMillis(time);
            String thisKey = String.format("%d hrs", calendar.get(Calendar.HOUR_OF_DAY));
            Float thisValue = AppConstants.getBatteryValue(BatteryLevel.valueOf(result.getBattery()).getValue());

            chartData.put(thisKey, thisValue);
        }

        Log.e("Size of result", String.valueOf(chartData.size()));


        LineChartView chartView = (LineChartView) findViewById(R.id.battery_linechart);
        LineSet dataset = new LineSet();

        Map<String, Float> treeMap = new TreeMap<>(chartData);

        for (String key: treeMap.keySet()) {
            dataset.addPoint(key, chartData.get(key));
        }
//            dataset.addPoint("Test", 45.2f);
//            dataset.addPoint(new Point("TestValue", 23.34f));
        dataset.setColor(Color.BLUE);
        chartView.addData(dataset);
        chartView.setAxisBorderValues(0, 100, 20);
        chartView.show();
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

    private void listenURI() {
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
            if (!AppSharedPreferences.getInstance().getSharedPreferences(context).getBoolean("loginStatus", false)) {
                String url = "https://www.fitbit.com/oauth2/authorize?response_type=code&client_id=2285P6&scope=settings profile heartrate";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(url));

                new FitbitAPITask(context, RequestType.AccessToken).execute();
                return true;
            } else {
                Toast.makeText(context, "You are already logged in", Toast.LENGTH_LONG).show();
                return true;
            }
        } else if (id == R.id.action_alarm) {
            new FitbitAPITask(context, RequestType.Alarm).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
