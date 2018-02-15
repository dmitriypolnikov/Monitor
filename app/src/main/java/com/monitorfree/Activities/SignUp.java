package com.monitorfree.Activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.UserModel.User;
import com.monitorfree.Util.GlobalKeys;
import com.monitorfree.databinding.ActivitySignUpBinding;

import javax.inject.Inject;


public class SignUp extends AppCompatActivity implements GlobalKeys {

    @Inject
    MyApp myApp;


    @Inject
    User user;

    @Inject
    UserRequests userRequests;

    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        MyApp.component().inject(this);
    }

    public void clickSignUp(View view) {

        switch (view.getId()) {
            case R.id.tvLogin:
                myApp.go(SignUp.this, Login.class);
                break;

            case R.id.btnRegister:

                if (binding.etName.getText().toString() == null || binding.etName.getText().equals("")) {
                    binding.etName.setError("Please Enter Name");
                } else if (binding.etEmail.getText().toString() == null || binding.etEmail.getText().equals("")) {
                    binding.etEmail.setError("Please Enter Email");
                } else if (myApp.isValidEmail(binding.etEmail.getText().toString()) == false) {
                    binding.etEmail.setError("Invalid Email");
                } else if (binding.etPassword.getText().toString() == null || binding.etPassword.getText().equals("")) {
                    binding.etPassword.setError("Please Enter Password");
                } else if (binding.etPassword.getText().length() < 8) {
                    binding.etPassword.setError("Password must be 8-20 chars");
                } else {
                    userRequests.funSignUp(myApp, binding, SignUp.this);
                }

                break;
        }
    }
}
