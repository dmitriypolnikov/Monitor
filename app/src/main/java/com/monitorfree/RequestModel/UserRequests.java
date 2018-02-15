package com.monitorfree.RequestModel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.monitorfree.Activities.CallBackSuccess;
import com.monitorfree.Activities.Login;
import com.monitorfree.Activities.Main2Activity;
import com.monitorfree.Activities.SignUp;
import com.monitorfree.MyApp;
import com.monitorfree.Networking.iRepository;
import com.monitorfree.UserModel.AddMonitor;
import com.monitorfree.UserModel.User;
import com.monitorfree.Util.GlobalKeys;
import com.monitorfree.databinding.ActivityForgotPasswordBinding;
import com.monitorfree.databinding.ActivityLoginBinding;
import com.monitorfree.databinding.ActivitySaveMonitorBinding;
import com.monitorfree.databinding.ActivitySignUpBinding;
import com.monitorfree.databinding.FragmentHomeBinding;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jaspreet on 11/22/17.
 */

public class UserRequests implements GlobalKeys {


    public void funSignUp(MyApp boloTalk, ActivitySignUpBinding binding, Context classContext) {

        boloTalk.showProgress(classContext);
        Call<RootRegister> book = boloTalk.getRetrofitService().signUp(binding.etName.getText().toString(), binding.etPassword.getText().toString(), binding.etEmail.getText().toString(), "Uqj7RG6WqNfCqxvht8MhnkFSv", "f3bd2eda5604b98fdf287f55e789f7ec");
        book.enqueue(new Callback<RootRegister>() {
            @Override
            public void onResponse(Call<RootRegister> call, Response<RootRegister> response) {

                boloTalk.closeProgress();
                RootRegister userLogin2 = response.body();
                int errorType = userLogin2.getStatus().getError();

                if (errorType == 0) {
                    Toast.makeText(boloTalk.getContext(), "Register Succesfully", Toast.LENGTH_SHORT).show();

                    ((SignUp) classContext).onBackPressed();

                } else if (errorType == 101) {
                    Toast.makeText(boloTalk.getContext(), userLogin2.getStatus().getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(boloTalk.getContext(), userLogin2.getStatus().getMsg(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<RootRegister> call, Throwable t) {
                boloTalk.closeProgress();
//                Snackbar mSnackbar = Snackbar.make(binding.textView, "Unable to connect", Snackbar.LENGTH_LONG)
//                        .setAction("RETRY", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                funSignUp(boloTalk, user, binding, classContext);
//                            }
//                        });
//                mSnackbar.setActionTextColor(Color.RED).show();
            }
        });
    }


    public void funAddMonitor(MyApp boloTalk, AddMonitor addMonitor, ActivitySaveMonitorBinding binding, Context classContext, CallBackSuccess callBackSuccess) {

        if (addMonitor.getName() == null || addMonitor.getName().equals("")) {
            binding.edtFriendlyName.setError("Please Enter Name");
        } else if (addMonitor.getAddress() == null || addMonitor.getAddress().equals("")) {
            binding.edtWebAddress.setError("Please Enter Web Address");
        } else if (addMonitor.getStartDate() == null || addMonitor.getStartDate().equals("")) {
            binding.edTxtDate.setError("Please Enter Date");
        } else {

            boloTalk.showProgress(classContext);
            Call<RootRegister> book = boloTalk.getRetrofitService().addMonitor(addMonitor.getStartDate(), addMonitor.getName(), addMonitor.getAddress(), String.valueOf(addMonitor.getInterval()), String.valueOf(addMonitor.getType()), addMonitor.getPort(), addMonitor.getKeywords(), "f3bd2eda5604b98fdf287f55e789f7ec", "Uqj7RG6WqNfCqxvht8MhnkFSv", boloTalk.getKey(USER_HASH));
            book.enqueue(new Callback<RootRegister>() {
                @Override
                public void onResponse(Call<RootRegister> call, Response<RootRegister> response) {

                    boloTalk.closeProgress();
                    RootRegister userLogin2 = response.body();
                    int errorType = userLogin2.getStatus().getError();

                    if (errorType == 0) {

                        addMonitor.setId(userLogin2.getMonitorId());
                        boloTalk.globalMonitorList.add(addMonitor);
                        callBackSuccess.success(userLogin2);
                    }
                    else if (errorType == 202)
                    {
                        Toast.makeText(boloTalk.getContext(), ""+userLogin2.getStatus().getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<RootRegister> call, Throwable t) {
                    boloTalk.closeProgress();
                    Snackbar mSnackbar = Snackbar.make(binding.btnSaveMonitor, "Unable to connect", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    funAddMonitor(boloTalk, addMonitor, binding, classContext, callBackSuccess);
                                }
                            });
                    mSnackbar.setActionTextColor(Color.RED).show();
                }
            });

        }
    }

    public void funSendStatus(String monitorId, MyApp myApp, String status) {

        String key = "Uqj7RG6WqNfCqxvht8MhnkFSv";
        String sig = "f3bd2eda5604b98fdf287f55e789f7ec";
        String hash = myApp.getKey(USER_HASH);

        Call<RootRegister> book = myApp.getRetrofitService().sendStatus(sig, key, hash, monitorId, status);
        book.enqueue(new Callback<RootRegister>() {
            @Override
            public void onResponse(Call<RootRegister> call, Response<RootRegister> response) {

                RootRegister userLogin2 = response.body();
                int errorType = userLogin2.getStatus().getError();
                if (errorType == 0) {
                    //   Toast.makeText(myApp.getContext(), "Monitor added Succesfully", Toast.LENGTH_SHORT).show();
                    Log.d("monID", monitorId);
                    Log.d("monIDD","m "+ monitorId);
                }
            }

            @Override
            public void onFailure(Call<RootRegister> call, Throwable t) {

            }

        });
    }


    public void funLogin(MyApp boloTalk, ActivityLoginBinding binding, Context classContext) {

        boloTalk.showProgress(classContext);
        Call<RootLogin> book = boloTalk.getRetrofitService().login(binding.etPassword.getText().toString(), binding.etEmail.getText().toString(), "Uqj7RG6WqNfCqxvht8MhnkFSv", "f3bd2eda5604b98fdf287f55e789f7ec");
        book.enqueue(new Callback<RootLogin>() {
            @Override
            public void onResponse(Call<RootLogin> call, Response<RootLogin> response) {

                boloTalk.closeProgress();
                RootLogin userLogin2 = response.body();
                int errorType = userLogin2.getStatus().getError();

                if (errorType == 0) {
                    boloTalk.setKey(USER_EMAIL, userLogin2.getData().getUser().getEmail());
                    boloTalk.setKey(USER_NAME, userLogin2.getData().getUser().getFullName());
                    boloTalk.setKey(USER_PASSWORD, userLogin2.getData().getUser().getPassword());
                    boloTalk.setKey(USER_HASH, userLogin2.getData().getUser().getHash());
                    boloTalk.setKey(USER_ID, userLogin2.getData().getUser().getId());

                    Intent intent = new Intent(boloTalk.getContext(), Main2Activity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    classContext.startActivity(intent);


                } else if (errorType == 102) {
                    AlertDialog.Builder newDialog = new AlertDialog.Builder(classContext);
                    newDialog.setTitle("Error ?");
                    newDialog.setMessage(userLogin2.getStatus().getMsg());
                    newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    newDialog.show();
                } else {
                    Toast.makeText(boloTalk.getContext(), userLogin2.getStatus().getMsg(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<RootLogin> call, Throwable t) {
                boloTalk.closeProgress();
//                Snackbar mSnackbar = Snackbar.make(binding.textView, "Unable to connect", Snackbar.LENGTH_LONG)
//                        .setAction("RETRY", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                funLogin(boloTalk, user, binding, classContext);
//                            }
//                        });
//                mSnackbar.setActionTextColor(Color.RED).show();
            }
        });
    }


    public void funForgotPass(MyApp boloTalk, User user, ActivityForgotPasswordBinding binding, Context classContext) {


        if (user.getEmail() == null || user.getEmail().equals("")) {
            binding.etEmail.setError("Please Enter Email");
        } else if (boloTalk.isValidEmail(user.getEmail().toString()) == false) {
            binding.etEmail.setError("Invalid Email");
        } else {

            boloTalk.setKey(USER_EMAIL, user.getEmail());
            boloTalk.setKey(USER_NAME, user.getFullName());
            boloTalk.setKey(USER_PASSWORD, user.getPassword());

            user.setKey("Uqj7RG6WqNfCqxvht8MhnkFSv");
            user.setSig("f3bd2eda5604b98fdf287f55e789f7ec");

            boloTalk.showProgress(classContext);
            Call<RootRegister> book = boloTalk.getRetrofitService().forgotPassword(user.getEmail(), user.getKey(), user.getSig());
            book.enqueue(new Callback<RootRegister>() {
                @Override
                public void onResponse(Call<RootRegister> call, Response<RootRegister> response) {

                    boloTalk.closeProgress();
                    RootRegister userLogin2 = response.body();
                    int errorType = userLogin2.getStatus().getError();

                    if (errorType == 0) {
                        Toast.makeText(boloTalk.getContext(), userLogin2.getStatus().getMsg(), Toast.LENGTH_SHORT).show();

                    } else if (errorType == 106) {
                        Toast.makeText(boloTalk.getContext(), userLogin2.getStatus().getMsg(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<RootRegister> call, Throwable t) {
                    boloTalk.closeProgress();
                    Snackbar mSnackbar = Snackbar.make(binding.etEmail, "Unable to connect", Snackbar.LENGTH_LONG)
                            .setAction("RETRY", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    funForgotPass(boloTalk, user, binding, classContext);
                                }
                            });
                    mSnackbar.setActionTextColor(Color.RED).show();
                }
            });

        }
    }


    public void getMonitor(MyApp boloTalk, FragmentHomeBinding binding, Context classContext, CallBackSuccess callBackSuccess) {

        String key = "Uqj7RG6WqNfCqxvht8MhnkFSv";
        String sig = "f3bd2eda5604b98fdf287f55e789f7ec";
        String hash = boloTalk.getKey(USER_HASH);

        boloTalk.showProgress(classContext);
        Call<RootMonitorList> book = boloTalk.getRetrofitService().getMonitorList(sig, key, hash);
        book.enqueue(new Callback<RootMonitorList>() {
            @Override
            public void onResponse(Call<RootMonitorList> call, Response<RootMonitorList> response) {

                boloTalk.closeProgress();
                RootMonitorList userLogin2 = response.body();
                int errorType = userLogin2.getStatus().getError();

                if (errorType == 0) {
                    callBackSuccess.success(userLogin2);

                } else if (errorType == 102) {
                    Toast.makeText(boloTalk.getContext(), userLogin2.getStatus().getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(boloTalk.getContext(), userLogin2.getStatus().getMsg(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<RootMonitorList> call, Throwable t) {
                boloTalk.closeProgress();
                Snackbar mSnackbar = Snackbar.make(binding.ivDown, "Unable to connect", Snackbar.LENGTH_LONG)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getMonitor(boloTalk, binding, classContext, callBackSuccess);
                            }
                        });
                mSnackbar.setActionTextColor(Color.RED).show();
            }
        });

    }
}



