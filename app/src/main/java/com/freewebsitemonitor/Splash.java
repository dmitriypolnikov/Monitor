package com.freewebsitemonitor;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.freewebsitemonitor.Activities.Login;
import com.freewebsitemonitor.Activities.Main2Activity;
import com.freewebsitemonitor.Util.GlobalKeys;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;

public class Splash extends AppCompatActivity implements GlobalKeys {


    @Inject
    MyApp myApp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        MyApp.component().inject(this);

//        PackageInfo info;
//		try {
//		    info = getPackageManager().getPackageInfo("com.freewebsitemonitor", PackageManager.GET_SIGNATURES);
//		    for (Signature signature : info.signatures) {
//		        MessageDigest md;
//		        md = MessageDigest.getInstance("SHA");
//		        md.update(signature.toByteArray());
//		        String something = new String(Base64.encode(md.digest(), 0));
//		        Log.e("hash key", something);
//		    }
//		} catch (PackageManager.NameNotFoundException e1) {
//		    Log.e("name not found", e1.toString());
//		} catch (NoSuchAlgorithmException e) {
//		    Log.e("no such an algorithm", e.toString());
//		} catch (Exception e) {
//		    Log.e("exception", e.toString());
//		}

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
