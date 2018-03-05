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

import javax.inject.Inject;

public class Main2Activity extends AppCompatActivity {

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

    public static boolean isFirstLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        MyApp.component().inject(this);

        Intent intent = getIntent();
        isFirstLogin = intent.getBooleanExtra("isFirstLogin", true);

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

        jobDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));

    }

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
                isFirstLogin = false;

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

                //Cancel all jobschedule monitor
                jobDispatcher.cancelAll();

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
    }
}