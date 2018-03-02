package com.monitorfree.Activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.Util.GlobalKeys;
import com.monitorfree.databinding.ActivityMyProfileBinding;

import javax.inject.Inject;
import javax.microedition.khronos.opengles.GL;

public class MyProfile extends AppCompatActivity implements GlobalKeys {


    @Inject
    MyApp myApp;

    ActivityMyProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile);
        MyApp.component().inject(this);

        binding.tvEmail.setText(myApp.getKey(USER_EMAIL));
        binding.tvName.setText(myApp.getKey(USER_NAME));

        binding.include.ivDelete.setVisibility(View.GONE);
        binding.include.ivPaused.setVisibility(View.GONE);

        binding.include.imgVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.include.txtVw.setText("My Profile");

    }
}
