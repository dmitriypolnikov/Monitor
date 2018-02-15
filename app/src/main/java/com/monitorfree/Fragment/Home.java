package com.monitorfree.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.monitorfree.Activities.CallBackSuccess;
import com.monitorfree.CustomAdapter.CustomAdapterMonitor;
import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.RequestModel.RootMonitorList;
import com.monitorfree.RequestModel.RootMonitorStatus;
import com.monitorfree.RequestModel.UserRequests;
import com.monitorfree.UserModel.*;
import com.monitorfree.UserModel.AddMonitor;
import com.monitorfree.databinding.FragmentHomeBinding;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.monitorfree.Util.GlobalKeys.USER_HASH;

/**
 * Created by jassi on 30-07-2016.
 */

public class Home extends Fragment implements CallBackSuccess, View.OnClickListener {

    FragmentHomeBinding binding;

    @Inject
    UserRequests userRequests;

    @Inject
    MyApp myApp;

    CallBackSuccess callBackSuccess;

    List<AddMonitor> allMonitorList = new ArrayList<AddMonitor>();
    List<AddMonitor> downMonitorList = new ArrayList<AddMonitor>();
    List<AddMonitor> upMonitorList = new ArrayList<AddMonitor>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        MyApp.component().inject(this);

        binding.relAll.setOnClickListener(this);
        binding.relDown.setOnClickListener(this);
        binding.relUp.setOnClickListener(this);
        binding.relPaused.setOnClickListener(this);

        callBackSuccess = this;
        binding.recyVwMonitor.setLayoutManager(new LinearLayoutManager(getActivity()));
//        binding.relSubAll.setBackgroundResource(R.drawable.round_border2);

        userRequests.getMonitor(myApp, binding, getActivity(), callBackSuccess);

        return binding.getRoot();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.relAll:
                setRecyclerViewData(0);     //all
                break;

            case R.id.relDown:
                setRecyclerViewData(2);     //down
                break;

            case R.id.relUp:
                setRecyclerViewData(1);     //up
                break;

            case R.id.relPaused:
                break;
        }
    }

    @Override
    public void success(Object object) {

        RootMonitorList rootMonitorList = (RootMonitorList) object;

        myApp.globalMonitorList = rootMonitorList.getData();

        for (int i = 0 ; i < rootMonitorList.getData().size() ; i++) {

            ArrayList<MonitorStatus> arrStatus = rootMonitorList.getData().get(i).getStatus();

            if (arrStatus.size() > 0) {

                allMonitorList.add(rootMonitorList.getData().get(i));

                if (arrStatus.get(0).getStatus().equals("1")) {
                    upMonitorList.add(rootMonitorList.getData().get(i));
                } else if (arrStatus.get(0).getStatus().equals("2")) {
                    downMonitorList.add(rootMonitorList.getData().get(i));
                }
            }
        }

        setRecyclerViewData(0);
    }

    private void setRecyclerViewData(int status) {

        List<AddMonitor> tempMonitorLists = new ArrayList<AddMonitor>();

        switch (status) {
            case 0:     //total
//                binding.relSubAll.setBackgroundResource(R.drawable.round_border2);
//                binding.relSubDown.setBackgroundResource(R.drawable.round_border);
//                binding.relSubUp.setBackgroundResource(R.drawable.round_border);
//                binding.relSubPause.setBackgroundResource(R.drawable.round_border);

                binding.txtVwTotal.setText(String.valueOf(allMonitorList.size()));
                binding.txtVwActive.setText(String.valueOf(upMonitorList.size()));
                binding.txtVwDown.setText(String.valueOf(downMonitorList.size()));
                tempMonitorLists = allMonitorList;
                break;
            case 1:     //active

//                binding.relSubAll.setBackgroundResource(R.drawable.round_border);
//                binding.relSubDown.setBackgroundResource(R.drawable.round_border);
//                binding.relSubUp.setBackgroundResource(R.drawable.round_border2);
//                binding.relSubPause.setBackgroundResource(R.drawable.round_border);

                tempMonitorLists = upMonitorList;
                break;
            case 2:     //unactive
//                binding.relSubAll.setBackgroundResource(R.drawable.round_border);
//                binding.relSubDown.setBackgroundResource(R.drawable.round_border2);
//                binding.relSubUp.setBackgroundResource(R.drawable.round_border);
//                binding.relSubPause.setBackgroundResource(R.drawable.round_border);

                tempMonitorLists = downMonitorList;
                break;
        }

        CustomAdapterMonitor customAdapterMonitor = new CustomAdapterMonitor(myApp.getContext(), tempMonitorLists);
        binding.recyVwMonitor.setAdapter(customAdapterMonitor);
    }
}
