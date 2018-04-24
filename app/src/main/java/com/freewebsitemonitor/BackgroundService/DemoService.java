package com.freewebsitemonitor.BackgroundService;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.freewebsitemonitor.MyApp;
import com.freewebsitemonitor.RequestModel.UserRequests;
import com.freewebsitemonitor.Util.MonitorUtil;
import com.freewebsitemonitor.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by Dmitriy on 2/26/2018.
 */

public class DemoService extends JobService {

    private static final int NOTI_PRIMARY1 = 1100;
    private static final String TAG = "DemoService";

    @Inject
    MyApp myApp;

    @Inject
    UserRequests userRequests;

    String mobileDateTime;
    String deviceName;
    String ipAddress;

    @Override
    public boolean onStartJob(@NonNull JobParameters job) {
        Log.d("DemoService", "true");

        MyApp.component().inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Bundle extras = job.getExtras();
        assert extras != null;

        String monitorID = extras.getString("job_monitorID");
        String monitorName = extras.getString("job_monitorName");
        String address = extras.getString("job_address");
        String keywords = extras.getString("job_keywords");
        String port = extras.getString("job_port");
        String type = extras.getString("job_type");
        String active = extras.getString("job_active");

        String interval = extras.getString("job_interval");
        String startDate = extras.getString("job_startDate");

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mobileDateTime = df1.format(c.getTime());

        String previousStatus = myApp.getKey(monitorID);

        deviceName = Build.MODEL;
        ipAddress = MonitorUtil.getIPAddress();

        if (myApp.isConnectingToInternet()) {

            if (type.equals("1")) {    //Http monitor

                if (active.equals("1")) {

                    boolean isGet = MonitorUtil.isHttpConnection(address, "1", "");

                    if (isGet) {
                        Log.d("Http monitor", "true");

                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                    } else {
                        Log.d("Http monitor", "Ping check");

                        userRequests.getStatusPing(myApp, monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                    }
                } else {
                    Log.d("Http monitor", "No active");
                }

            } else if (type.equals("2")) {     //Ping monitor

                if (active.equals("1")) {

                    boolean isGet = MonitorUtil.isPingMonitor(address);
                    if (isGet) {
                        Log.d("Ping monitor", "true");

                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                    } else {
                        Log.d("Ping monitor", "false");

                        isGet = MonitorUtil.isPingMonitor(address);

                        if (isGet) {
                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                        } else {
                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                        }
                    }
                } else {
                    Log.d("Ping monitor", "No active");
                }

            } else if (type.equals("3")) {     //Keyword monitor

                if (active.equals("1")) {

                    boolean isGetKeyword = MonitorUtil.isHttpConnection(address, "3", keywords);
                    if (isGetKeyword) {
                        Log.d("Keyword monitor", "true");

                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                    } else {
                        Log.d("Keyword monitor", "Ping check");

                        userRequests.getStatusPing(myApp, monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                    }

                } else {
                    Log.d("Keyword monitor", "No active");
                }

            } else if (type.equals("4")) {     //Port monitor
                if (active.equals("1")) {

                    boolean isGet = MonitorUtil.isPortMonitor(address, port);
                    if (isGet) {
                        Log.d("Port monitor", "true");

                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                    } else {
                        Log.d("Port monitor", "false");

                        isGet = MonitorUtil.isPortMonitor(address, port);
                        if (isGet) {
                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                        } else {
                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
                        }
                    }
                } else {
                    Log.d("Port monitor", "No active");
                }
            }
        }

        return false; // No more work to do
    }

    @Override
    public boolean onStopJob(@NonNull JobParameters job) {
        return false; // No more work to do
    }

//    public void monitorCheck(String connectionQuality, String strtype, String active, String monitorID, String monitorName, String address, String keywords, String port, String type, String interval, String startDate, String previousStatus) {
//        if (!connectionQuality.equals("UNKNOWN") && !connectionQuality.equals("POOR")) {  //Not UNKNOWN, POOR
//
//            if (strtype.equals("1")) {    //Http monitor
//
//                if (active.equals("1")) {
//
//                    MonitorUtil.cnt = 5;
//                    boolean isGet = MonitorUtil.isHttpConnection(address, "1", "");
//
//                    if (isGet) {
//                        Log.d("Http monitor", "true");
//
//                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                    } else {
//                        Log.d("Http monitor", "false");
//
//                        MonitorUtil.cnt = 5;
//                        isGet = MonitorUtil.isHttpConnection(address, "1", "");
//                        if (isGet) {
//                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                        } else {
//                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                        }
//                    }
//                } else {
//                    Log.d("Http monitor", "No still start");
//                }
//
//            } else if (strtype.equals("2")) {     //Ping monitor
//
//                if (active.equals("1")) {
//
//                    boolean isGet = MonitorUtil.isPingMonitor(address);
//                    if (isGet) {
//                        Log.d("Ping monitor", "true");
//
//                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                    } else {
//                        Log.d("Ping monitor", "false");
//
//                        isGet = MonitorUtil.isPingMonitor(address);
//
//                        if (isGet) {
//                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                        } else {
//                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                        }
//                    }
//                } else {
//                    Log.d("Ping monitor", "No still start");
//                }
//
//            } else if (strtype.equals("3")) {     //Keyword monitor
//
//                if (active.equals("1")) {
//
//                    MonitorUtil.cnt = 5;
//                    boolean isGetKeyword = MonitorUtil.isHttpConnection(address, "3", keywords);
//                    if (isGetKeyword) {
//                        Log.d("Keyword monitor", "true");
//
//                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                    } else {
//                        Log.d("Keyword monitor", "false");
//
//                        MonitorUtil.cnt = 5;
//                        isGetKeyword = MonitorUtil.isHttpConnection(address, "3", keywords);
//
//                        if (isGetKeyword) {
//                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                        } else {
//                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                        }
//                    }
//
//                } else {
//                    Log.d("Keyword monitor", "No still start");
//                }
//
//            } else if (strtype.equals("4")) {     //Port monitor
//                if (active.equals("1")) {
//
//                    boolean isGet = MonitorUtil.isPortMonitor(address, port);
//                    if (isGet) {
//                        Log.d("Port monitor", "true");
//
//                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                    } else {
//                        Log.d("Port monitor", "false");
//
//                        isGet = MonitorUtil.isPortMonitor(address, port);
//                        if (isGet) {
//                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                        } else {
//                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                        }
//                    }
//                } else {
//                    Log.d("Port monitor", "No still start");
//                }
//            }
//        }
//    }

    public void sendStatus(String status, String monitorID, String monitorName, String address, String keywords, String port, String type, String interval, String startDate, String previousStatus) {

        if (myApp.isConnectingToInternet()) {

            userRequests.funSendStatus(monitorID, myApp, status, mobileDateTime, deviceName, ipAddress, previousStatus, monitorName, address, keywords, port, type, interval, startDate);
        }
    }

//    private class ConnectionChangedListener
//            implements ConnectionClassManager.ConnectionClassStateChangeListener {
//
//        @Override
//        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
//            mConnectionClass = bandwidthState;
//        }
//    }

//    public void checkNetworkQuality(String strtype, String active, String monitorID, String monitorName, String address, String keywords, String port, String type, String interval, String startDate, String previousStatus) {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url(mURL)
//                .build();
//
//        mDeviceBandwidthSampler.startSampling();
//        client.newCall(request).enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                mDeviceBandwidthSampler.stopSampling();
//                // Retry for up to 10 times until we find a ConnectionClass.
//                if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
//                    mTries++;
//                    checkNetworkQuality(strtype, active, monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//                Log.d(TAG, mConnectionClassManager.getCurrentBandwidthQuality().toString());
//
//                strConnectSpeed = mConnectionClassManager.getCurrentBandwidthQuality().toString();
//
//                monitorCheck(strConnectSpeed, strtype, active, monitorID, monitorName, address, keywords, port, type, interval, startDate, previousStatus);
//
//                mDeviceBandwidthSampler.stopSampling();
//            }
//        });
//    }
}
