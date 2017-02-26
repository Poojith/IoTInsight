package com.poojithjain.iotinsight.util.app;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.poojithjain.iotinsight.util.net.api.FitbitAPITask;
import com.poojithjain.iotinsight.util.net.data.RequestType;

public class APIService extends Service {

    private boolean isRunning;

    public APIService() {
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
        isRunning = true;
        new FitbitAPITask(APIService.this, RequestType.Devices).execute();
        return Service.START_STICKY;
    }
}
