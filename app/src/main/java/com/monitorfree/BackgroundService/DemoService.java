package com.monitorfree.BackgroundService;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.monitorfree.MyApp;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.Util.MonitorUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by Dmitriy on 2/26/2018.
 */

public class DemoService extends JobService {

    @Inject
    MyApp myApp;

    @Inject
    UserRequests userRequests;

    @Override
    public boolean onStartJob(@NonNull JobParameters job) {
        Log.d("DemoService", "true");

        MyApp.component().inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = job.getExtras();
        assert extras != null;

        String monitorID = extras.getString("monitorID");
        String address = extras.getString("address");
        String keywords = extras.getString("keywords");
        String port = extras.getString("port");
        String type = extras.getString("type");
        String active = extras.getString("active");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String mobileDateTime = df1.format(c.getTime());

        if (!myApp.isConnectingToInternet()) {

            Toast.makeText(this, "Network Connection Error!", Toast.LENGTH_SHORT).show();

        } else {

            if (type.equals("1")) {    //Http monitor

                if (active.equals("1")) {
                    boolean isGet = MonitorUtil.isHttpConnection(address, "1", "");

                    if (isGet) {
                        Log.d("Http monitor", "true");
                        userRequests.funSendStatus(monitorID, myApp, "1", mobileDateTime);
                    } else {
                        Log.d("Http monitor", "false");
                        userRequests.funSendStatus(monitorID, myApp, "2", mobileDateTime);
                    }
                } else {
                    Log.d("Http monitor", "No still start");
                }

            } else if (type.equals("2")) {     //Ping monitor

                if (active.equals("1")) {

                    boolean isGet = MonitorUtil.isPingMonitor(address);
                    if (isGet) {
                        Log.d("Ping monitor", "true");
                        userRequests.funSendStatus(monitorID, myApp, "1", mobileDateTime);
                    } else {
                        Log.d("Ping monitor", "false");
                        userRequests.funSendStatus(monitorID, myApp, "2", mobileDateTime);
                    }
                } else {
                    Log.d("Ping monitor", "No still start");
                }

            } else if (type.equals("3")) {     //Keyword monitor

                if (active.equals("1")) {

                    boolean isGetKeyword = MonitorUtil.isHttpConnection(address, "3", keywords);
                    if (isGetKeyword) {
                        Log.d("Keyword monitor", "true");
                        userRequests.funSendStatus(monitorID, myApp, "1", mobileDateTime);
                    } else {
                        Log.d("Keyword monitor", "false");
                        userRequests.funSendStatus(monitorID, myApp, "2", mobileDateTime);
                    }

                } else {
                    Log.d("Keyword monitor", "No still start");
                }

            } else if (type.equals("4")) {     //Port monitor
                if (active.equals("1")) {

                    boolean isGet = MonitorUtil.isPortMonitor(address, port);
                    if (isGet) {
                        Log.d("Port monitor", "true");
                        userRequests.funSendStatus(monitorID, myApp, "1", mobileDateTime);
                    } else {
                        Log.d("Port monitor", "false");
                        userRequests.funSendStatus(monitorID, myApp, "2", mobileDateTime);
                    }
                } else {
                    Log.d("Port monitor", "No still start");
                }
            }
        }

        return false; // No more work to do
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        return false; // No more work to do
    }

}
