package com.monitorfree.Activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.UserModel.User;
import com.monitorfree.databinding.ActivityForgotPasswordBinding;

import javax.inject.Inject;

public class ForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;

    @Inject
    MyApp myApp;

    @Inject
    UserRequests userRequests;


    @Inject
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        MyApp.component().inject(this);

        binding.setUser(user);



    }


    public void clickForgot(View view) {

        switch (view.getId()) {

            case R.id.btnSend:


                userRequests.funForgotPass(myApp,user,binding,ForgotPassword.this);

                break;
        }
    }
}
