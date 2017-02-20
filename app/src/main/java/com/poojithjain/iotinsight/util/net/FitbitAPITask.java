package com.poojithjain.iotinsight.util.net;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.poojithjain.iotinsight.util.AppConstants;
import com.poojithjain.iotinsight.util.FitbitDevice;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

/**
 * Created by Poojith on 20-02-2017.
 */

public class FitbitAPITask extends AsyncTask {
    private Context context;
    private FitbitAPI api;

    public FitbitAPITask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        api = AppConstants.initAPI();
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
//        if (aalarm) {
//            api.getAlarm().execute();
//        } else if (devices) {
            try {
                Response<List<FitbitDevice>> res = api.getDevices().execute();
                Log.d("api", res.body().toString());
                Toast.makeText(context, res.body().get(0).getDeviceVersion(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
        return null;
    }
}
