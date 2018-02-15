package com.monitorfree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.monitorfree.Activities.Login;
import com.monitorfree.Activities.Main2Activity;
import com.monitorfree.Util.GlobalKeys;

import javax.inject.Inject;

public class Splash extends AppCompatActivity implements GlobalKeys {


    @Inject
    MyApp myApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MyApp.component().inject(this);


        new Thread() {
            public void run() {
                try {
                    sleep(3000);

                    if (myApp.getKey(USER_ID) != null) {
                        Log.d("keyHash",myApp.getKey(USER_HASH));
                        Intent intent = new Intent(myApp.getContext(), Main2Activity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(Splash.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.
                start();
    }
}
