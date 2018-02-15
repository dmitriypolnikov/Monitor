package com.monitorfree.Activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.UserModel.User;
import com.monitorfree.databinding.ActivityLoginBinding;

import javax.inject.Inject;

public class Login extends AppCompatActivity {

    @Inject
    MyApp myApp;

//    @Inject
//    User user;

    @Inject
    UserRequests userRequests;

    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        MyApp.component().inject(this);
    }

    public void clickLogin(View view) {

        switch (view.getId()) {
            case R.id.tvSignUp:
                myApp.go(Login.this, SignUp.class);
                break;

            case R.id.txtVwFPass:
                myApp.go(Login.this, ForgotPassword.class);
                break;

            case R.id.btnLogin:
                if (binding.etEmail.getText().toString() == null || binding.etEmail.getText().equals("")) {
                    binding.etEmail.setError("Please Enter Email");
                } else if (myApp.isValidEmail(binding.etEmail.getText().toString()) == false) {
                    binding.etEmail.setError("Invalid Email");
                } else if (binding.etPassword.getText().toString() == null || binding.etPassword.getText().equals("")) {
                    binding.etPassword.setError("Please Enter Password");
                } else if (binding.etPassword.getText().length() < 8) {
                    binding.etPassword.setError("Password must be 8-20 chars");
                } else {
                    userRequests.funLogin(myApp, binding, Login.this);
                }

                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
