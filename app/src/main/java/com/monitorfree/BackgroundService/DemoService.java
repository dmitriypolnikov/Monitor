package com.monitorfree.BackgroundService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.monitorfree.Activities.Login;
import com.monitorfree.Activities.Main2Activity;
import com.monitorfree.Activities.MonitorInfo;
import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.Util.MonitorUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * Created by Dmitriy on 2/26/2018.
 */

public class DemoService extends JobService {

    private static final int NOTI_PRIMARY1 = 1100;

    @Inject
    MyApp myApp;

    @Inject
    UserRequests userRequests;

    String mobileDateTime;
    String previousStatus;
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

        String  monitorID = extras.getString("job_monitorID");
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

        previousStatus = myApp.getKey(monitorID);

        deviceName = Build.MODEL;
        ipAddress = MonitorUtil.getIPAddress();

        if (myApp.isConnectingToInternet()) {

//            new StatusCheckTask().execute(monitorID, monitorName, address, keywords, port, type, active, interval, startDate);

            if (type.equals("1")) {    //Http monitor

                if (active.equals("1")) {

                    boolean isGet = MonitorUtil.isHttpConnection(address, "1", "");

                    if (isGet) {
                        Log.d("Http monitor", "true");

                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                    } else {
                        Log.d("Http monitor", "false");

                        isGet = MonitorUtil.isHttpConnection(address, "1", "");
                        if (isGet) {
                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                        } else {
                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                        }
                    }
                } else {
                    Log.d("Http monitor", "No still start");
                }

            } else if (type.equals("2")) {     //Ping monitor

                if (active.equals("1")) {

                    boolean isGet = MonitorUtil.isPingMonitor(address);
                    if (isGet) {
                        Log.d("Ping monitor", "true");

                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                    } else {
                        Log.d("Ping monitor", "false");

                        isGet = MonitorUtil.isPingMonitor(address);

                        if (isGet) {
                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                        } else {
                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                        }
                    }
                } else {
                    Log.d("Ping monitor", "No still start");
                }

            } else if (type.equals("3")) {     //Keyword monitor

                if (active.equals("1")) {

                    boolean isGetKeyword = MonitorUtil.isHttpConnection(address, "3", keywords);
                    if (isGetKeyword) {
                        Log.d("Keyword monitor", "true");

                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                    } else {
                        Log.d("Keyword monitor", "false");

                        isGetKeyword = MonitorUtil.isHttpConnection(address, "3", keywords);

                        if (isGetKeyword) {
                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                        } else {
                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                        }
                    }

                } else {
                    Log.d("Keyword monitor", "No still start");
                }

            } else if (type.equals("4")) {     //Port monitor
                if (active.equals("1")) {

                    boolean isGet = MonitorUtil.isPortMonitor(address, port);
                    if (isGet) {
                        Log.d("Port monitor", "true");

                        sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                    } else {
                        Log.d("Port monitor", "false");

                        isGet = MonitorUtil.isPortMonitor(address, port);
                        if (isGet) {
                            sendStatus("1", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                        } else {
                            sendStatus("2", monitorID, monitorName, address, keywords, port, type, interval, startDate);
                        }
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

//    public void startNotification(String message, String monitorID, String monitorName, String address, String keywords, String port, String type, String interval, String startDate, String status) {
//
//        Intent notificationIntent = new Intent(this, MonitorInfo.class);
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("notification", true);
//        bundle.putString("noti_name", monitorName);
//        bundle.putString("noti_id", monitorID);
//
//        bundle.putString("noti_startDate", startDate);
//        bundle.putString("noti_status", status);
//        bundle.putString("noti_active", "1");
//
//        bundle.putString("noti_interval", interval);
//        bundle.putString("noti_address", address);
//        bundle.putString("noti_type", type);
//        bundle.putString("noti_keywords", keywords);
//        bundle.putString("noti_port", port);
//
//        notificationIntent.putExtras(bundle);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
////        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
////                Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        PendingIntent contentIntent = PendingIntent.getActivity(this, Integer.valueOf(monitorID), notificationIntent, PendingIntent.FLAG_ONE_SHOT);
//
//        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_DEFAULT);
//
//            // Configure the notification channel.
//            notificationChannel.setDescription("Channel description");
//            notificationChannel.enableLights(true);
//            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
//            notificationChannel.enableVibration(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        Notification notification = new NotificationCompat.Builder(myApp.getContext(), NOTIFICATION_CHANNEL_ID)
//                .setContentTitle("Monitorfree")
//                .setContentText(message)
//                .setSmallIcon(R.drawable.logo)
//                .setAutoCancel(true)
//                .setVisibility(Notification.VISIBILITY_PUBLIC)
//                .setDefaults(Notification.DEFAULT_SOUND)
//                .setContentIntent(contentIntent)
//                .build();
//
//        notificationManager.notify(Integer.valueOf(monitorID), notification);
//
//    }

//    class StatusCheckTask extends AsyncTask<String, Void, Boolean> {
//
//        String monitorID, monitorName, address, keywords, port, type, active, interval, startDate;
//
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//
//        }
//
//        @Override
//        protected Boolean doInBackground(String... param) {
//
//            monitorID = param[0];
//            monitorName = param[1];
//            address = param[2];
//            keywords = param[3];
//            port = param[4];
//            type = param[5];
//            active = param[6];
//            interval = param[7];
//            startDate = param[8];
//
//            if (type.equals("1")) {    //Http monitor
//
//                if (active.equals("1")) {
//
//                    boolean isGet = MonitorUtil.isHttpConnection(address, "1", "");
//
//                    if (isGet) {
//                        Log.d("Http monitor", "true");
//
//                        sendOnlineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                    } else {
//                        Log.d("Http monitor", "false");
//
//                        isGet = MonitorUtil.isHttpConnection(address, "1", "");
//                        if (isGet) {
//                            sendOnlineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                        } else {
//                            sendOfflineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                        }
//                    }
//                } else {
//                    Log.d("Http monitor", "No still start");
//                }
//
//            } else if (type.equals("2")) {     //Ping monitor
//
//                if (active.equals("1")) {
//
//                    boolean isGet = MonitorUtil.isPingMonitor(address);
//                    if (isGet) {
//                        Log.d("Ping monitor", "true");
//
//                        sendOnlineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                    } else {
//                        Log.d("Ping monitor", "false");
//
//                        isGet = MonitorUtil.isPingMonitor(address);
//
//                        if (isGet) {
//                            sendOnlineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                        } else {
//                            sendOfflineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                        }
//                    }
//                } else {
//                    Log.d("Ping monitor", "No still start");
//                }
//
//            } else if (type.equals("3")) {     //Keyword monitor
//
//                if (active.equals("1")) {
//
//                    boolean isGetKeyword = MonitorUtil.isHttpConnection(address, "3", keywords);
//                    if (isGetKeyword) {
//                        Log.d("Keyword monitor", "true");
//
//                        sendOnlineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                    } else {
//                        Log.d("Keyword monitor", "false");
//
//                        isGetKeyword = MonitorUtil.isHttpConnection(address, "3", keywords);
//
//                        if (isGetKeyword) {
//                            sendOnlineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                        } else {
//                            sendOfflineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                        }
//                    }
//
//                } else {
//                    Log.d("Keyword monitor", "No still start");
//                }
//
//            } else if (type.equals("4")) {     //Port monitor
//                if (active.equals("1")) {
//
//                    boolean isGet = MonitorUtil.isPortMonitor(address, port);
//                    if (isGet) {
//                        Log.d("Port monitor", "true");
//
//                        sendOnlineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                    } else {
//                        Log.d("Port monitor", "false");
//
//                        isGet = MonitorUtil.isPortMonitor(address, port);
//                        if (isGet) {
//                            sendOnlineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                        } else {
//                            sendOfflineStatus(monitorID, monitorName, address, keywords, port, type, interval, startDate);
//                        }
//                    }
//                } else {
//                    Log.d("Port monitor", "No still start");
//                }
//            }
//
//            return true;
//        }
//    }

    public void sendStatus(String status, String monitorID, String monitorName, String address, String keywords, String port, String type, String interval, String startDate) {

        if (myApp.isConnectingToInternet()) {

            userRequests.funSendStatus(monitorID, myApp, status, mobileDateTime, deviceName, ipAddress, previousStatus, monitorName, address, keywords, port, type, interval, startDate);
        }
    }

}
