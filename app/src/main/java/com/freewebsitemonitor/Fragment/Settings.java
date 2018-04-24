package com.freewebsitemonitor.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.LoginManager;
import com.freewebsitemonitor.Activities.CallBackSuccess;
import com.freewebsitemonitor.Activities.Login;
import com.freewebsitemonitor.Activities.Main2Activity;
import com.freewebsitemonitor.RequestModel.UserRequests;
import com.freewebsitemonitor.MyApp;
import com.freewebsitemonitor.R;

import static com.freewebsitemonitor.Util.GlobalKeys.ACCOUNT_TYPE;
import static com.freewebsitemonitor.Util.GlobalKeys.USER_EMAIL;
import com.freewebsitemonitor.databinding.*;

/**
 * Created by jassi on 30-07-2016.
 */

public class Settings extends Fragment implements CallBackSuccess {

    UserRequests userRequests = new UserRequests();

    FragmentSettingsBinding binding;

    CallBackSuccess callBackSuccess;

    Main2Activity parent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        callBackSuccess = this;

        binding.txtemail.setText(MyApp.instance.getKey(USER_EMAIL));

        binding.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog("Delete ?", "Do you want to delete this account?");
            }
        });

        return binding.getRoot();
    }

    private void alertDialog(String title, String message) {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(getContext());
        newDialog.setTitle(title);
        newDialog.setMessage(message);
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                MyApp.instance.jobDispatcher.cancelAll();

                String accountType = MyApp.instance.getKey(ACCOUNT_TYPE);

                if (accountType.equals("1")) {
                    //FB logout
                    LoginManager.getInstance().logOut();
                }

                userRequests.sendAccountDelete(MyApp.instance, getContext(), callBackSuccess);
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
    public void success(Object object) {
        Log.d("delete ---", "account");

        MyApp.instance.logOut();

        Intent intent = new Intent(getContext(), Login.class);
        startActivity(intent);
    }
}
