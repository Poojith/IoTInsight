package com.poojithjain.iotinsight.util.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.poojithjain.iotinsight.util.net.api.FitbitAPITask;
import com.poojithjain.iotinsight.util.net.data.RequestType;

public class AlarmService extends Service {

    public AlarmService() {
    }


    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new FitbitAPITask(AlarmService.this, RequestType.Alarm).execute();
        return Service.START_STICKY;
    }
}
