package com.monitorfree.Activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job.Builder;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.monitorfree.BackgroundService.BService;
import com.monitorfree.BackgroundService.DemoService;
import com.monitorfree.BackgroundService.SensorRestarterBroadcastReceiver;
import com.monitorfree.Fragment.*;
import com.monitorfree.Fragment.AddMonitor;
import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.UserModel.*;
import com.monitorfree.Util.GlobalKeys;
import com.monitorfree.databinding.ActivityMain2Binding;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Calendar;

import javax.inject.Inject;

public class Main2Activity extends AppCompatActivity {

    private TextView mTextMessage;

    static Fragment fragment = null;
    static Class fragmentClass = null;
    FragmentManager fm;
    DrawerLayout drawer;
    private boolean doubleBackToExitPressedOnce;

    TextView tvName, tvEmail;

    @Inject
    MyApp myApp;

    ActivityMain2Binding binding;
    public static FirebaseJobDispatcher jobDispatcher;

    Intent mServiceIntent;
    private BService mSensorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        MyApp.component().inject(this);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        setTitle("Dashboard");
        fragmentClass = Home.class;
        replaceFragment();

        tvName = (TextView) findViewById(R.id.tvName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);

        tvName.setText(myApp.getKey(GlobalKeys.USER_NAME));
        tvEmail.setText(myApp.getKey(GlobalKeys.USER_EMAIL));

//        mSensorService = new BService();
//        mServiceIntent = new Intent(this, mSensorService.getClass());
//        if (!isMyServiceRunning(mSensorService.getClass())) {
//            startService(mServiceIntent);
//        }

        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

//        Calendar cal = Calendar.getInstance();
//        Log.d("time", String.valueOf(cal.getTimeInMillis()));
//        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        long interval = 1000 * 60; // 5 minutes in milliseconds
//
//        Intent serviceIntent = new Intent(this, BService.class);
//        // make sure you **don't** use *PendingIntent.getBroadcast*, it wouldn't work
//        PendingIntent servicePendingIntent =
//                PendingIntent.getService(this,
//                        0, // integer constant used to identify the service
//                        serviceIntent,
//                        PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running
//        // there are other options like setInexactRepeating, check the docs
//
//        am.setInexactRepeating(
//                AlarmManager.RTC_WAKEUP,//type of alarm. This one will wake up the device when it goes off, but there are others, check the docs
//                0,
//                interval,
//                servicePendingIntent
//        );



        try {

            String url = "https://h.localtells.com";

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");

            System.out.println("Request URL ... " + url);

            boolean redirect = false;

            // normally, 3xx is redirect
            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                if (status == HttpURLConnection.HTTP_MOVED_TEMP
                        || status == HttpURLConnection.HTTP_MOVED_PERM
                        || status == HttpURLConnection.HTTP_SEE_OTHER)
                    redirect = true;
            }

            System.out.println("Response Code ... " + status);

            if (redirect) {

                // get redirect url from "location" header field
                String newUrl = conn.getHeaderField("Location");

                // get the cookie if need, for login
                String cookies = conn.getHeaderField("Set-Cookie");

                // open the new connnection again
                conn = (HttpURLConnection) new URL(newUrl).openConnection();
                conn.setRequestProperty("Cookie", cookies);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla");
                conn.addRequestProperty("Referer", "google.com");

                System.out.println("Redirect to URL : " + newUrl);

            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer html = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                html.append(inputLine);
            }
            in.close();

            System.out.println("URL Content... \n" + html.toString());
            System.out.println("Done");

        } catch (Exception e) {
            e.printStackTrace();
        }

//        String url = "https://h.localtells.com";
//
//        Response response = null;
//        try {
//            response = Jsoup.connect(url).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(response.statusCode() + " : " + response.url());

    }

//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Log.i ("isMyServiceRunning?", true+"");
//                return true;
//            }
//        }
//        Log.i ("isMyServiceRunning?", false+"");
//        return false;
//    }

    public void replaceFragment() {
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.relFragment, fragment)
                    .commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }


    public void clickMenu(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.txtVwLogout:
                showLogOutDialog();
                break;

            case R.id.profile:
                intent = new Intent(myApp.getContext(), MyProfile.class);
                startActivity(intent);
                break;

            case R.id.txtVwQ:
                setTitle("FAQs");
                fragmentClass = FAQs.class;
                replaceFragment();

                break;

            case R.id.addMonitor:
                setTitle("Add Monitor");
                fragmentClass = AddMonitor.class;
                replaceFragment();

                break;

            case R.id.tabHome:
                setTitle("Dashboard");
                fragmentClass = Home.class;
                replaceFragment();
                break;

            case R.id.tabMonitor:
                setTitle("Monitor");
                fragmentClass = Monitor.class;
                replaceFragment();
                break;

            case R.id.tabSettings:
                setTitle("Settings");
                fragmentClass = Settings.class;
                replaceFragment();

                break;
        }
    }

    void showLogOutDialog() {

        AlertDialog.Builder newDialog = new AlertDialog.Builder(Main2Activity.this);
        newDialog.setTitle("Logout ?");
        newDialog.setMessage("Logout (you will lose the current session)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                myApp.logOut();

                Intent intent = new Intent(myApp.getContext(), Login.class);
                startActivity(intent);
                finish();
                // update profileInfo from here..
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragment instanceof Home) {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                //
                this.doubleBackToExitPressedOnce = true;
                myApp.showSnackBar(getWindow().getDecorView(), "Please click back again to exit");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
                //
            } else {
                fragmentClass = Home.class;
                replaceFragment();
                setTitle("Streams");
            }
        }
        //.............................................//
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.add_monitor) {
            setTitle("Add Monitor");
            fragmentClass = AddMonitor.class;
            replaceFragment();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        Log.i("MAINACT", "onDestroy!");

//        Intent broadcastIntent = new Intent("com.monitorfree.BackgroundService.RestartSensor");
//        sendBroadcast(broadcastIntent);
    }
}