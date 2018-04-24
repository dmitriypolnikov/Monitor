package com.freewebsitemonitor.Fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.freewebsitemonitor.Activities.CallBackSuccess;
import com.freewebsitemonitor.Activities.Main2Activity;
import com.freewebsitemonitor.RequestModel.UserRequests;
import com.freewebsitemonitor.UserModel.AddMonitor;
import com.freewebsitemonitor.UserModel.MonitorStatus;
import com.freewebsitemonitor.BackgroundService.DemoService;
import com.freewebsitemonitor.CustomAdapter.CustomAdapterMonitor;
import com.freewebsitemonitor.MyApp;
import com.freewebsitemonitor.R;
import com.freewebsitemonitor.RequestModel.RootMonitorList;
import com.freewebsitemonitor.UserModel.*;
import com.freewebsitemonitor.databinding.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

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

    List<com.freewebsitemonitor.UserModel.AddMonitor> allMonitorList = new ArrayList<com.freewebsitemonitor.UserModel.AddMonitor>();
    List<com.freewebsitemonitor.UserModel.AddMonitor> downMonitorList = new ArrayList<com.freewebsitemonitor.UserModel.AddMonitor>();
    List<com.freewebsitemonitor.UserModel.AddMonitor> upMonitorList = new ArrayList<com.freewebsitemonitor.UserModel.AddMonitor>();
    List<com.freewebsitemonitor.UserModel.AddMonitor> pauseMonitorList = new ArrayList<com.freewebsitemonitor.UserModel.AddMonitor>();

    public static boolean m_bPause = false;

    Main2Activity parent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        MyApp.component().inject(this);

        parent = (Main2Activity) getActivity();

        binding.relAll.setOnClickListener(this);
        binding.relDown.setOnClickListener(this);
        binding.relUp.setOnClickListener(this);
        binding.relPaused.setOnClickListener(this);

        callBackSuccess = this;
        binding.recyVwMonitor.setLayoutManager(new LinearLayoutManager(getActivity()));

        return binding.getRoot();
    }

    @Override
    public void onResume() {

        if (myApp.isConnectingToInternet()) {
            allMonitorList.clear();
            downMonitorList.clear();
            upMonitorList.clear();
            pauseMonitorList.clear();

            myApp.globalMonitorList.clear();
            myApp.globalBGMonitorList.clear();

            userRequests.getMonitor(myApp, binding, getActivity(), callBackSuccess);
        }

        super.onResume();
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
                setRecyclerViewData(3);     //paused
                break;
        }
    }

    @Override
    public void success(Object object) {

        RootMonitorList rootMonitorList = (RootMonitorList) object;

        myApp.globalBGMonitorList = rootMonitorList.getData();

        monitorListSort(rootMonitorList.getData());

        if (myApp.globalMonitorList.size() == 0) {
            binding.txtNoMonitor.setVisibility(View.VISIBLE);

            parent.onWizard();
        } else {
            binding.txtNoMonitor.setVisibility(View.GONE);
        }

        for (int i = 0 ; i < myApp.globalMonitorList.size() ; i++) {

            ArrayList<MonitorStatus> arrStatus = myApp.globalMonitorList.get(i).getStatus();

            allMonitorList.add(myApp.globalMonitorList.get(i));

            if (myApp.globalMonitorList.get(i).getActive().equals("1")) {
                if (arrStatus.size() > 0) {

                    if (arrStatus.get(0).getStatus().equals("1")) {
                        upMonitorList.add(myApp.globalMonitorList.get(i));
                    } else if (arrStatus.get(0).getStatus().equals("2")) {
                        downMonitorList.add(myApp.globalMonitorList.get(i));
                    }
                }
            } else if (myApp.globalMonitorList.get(i).getActive().equals("-1")){
                pauseMonitorList.add(myApp.globalMonitorList.get(i));
            }

            myApp.setKey(String.valueOf(myApp.globalMonitorList.get(i).getId()), arrStatus.get(0).getStatus());

            //start for all monitors using Jobscheduler after login
            if (myApp.isFirstLogin) {

                com.freewebsitemonitor.UserModel.AddMonitor addMonitor = myApp.globalMonitorList.get(i);

                Bundle myExtrasBundle = new Bundle();
                myExtrasBundle.putString("job_monitorID", addMonitor.getId());
                myExtrasBundle.putString("job_monitorName", addMonitor.getName());
                myExtrasBundle.putString("job_address", addMonitor.getAddress());
                myExtrasBundle.putString("job_keywords", addMonitor.getKeywords());
                myExtrasBundle.putString("job_port", addMonitor.getPort());
                myExtrasBundle.putString("job_type", addMonitor.getType());
                myExtrasBundle.putString("job_active", addMonitor.getActive());

                myExtrasBundle.putString("job_interval", addMonitor.getInterval());
                myExtrasBundle.putString("job_startDate", addMonitor.getStartDate());

                int interval_time = Integer.valueOf(addMonitor.getInterval());

                final Job.Builder builder =
                        myApp.jobDispatcher
                                .newJobBuilder()
                                .setTag(addMonitor.getId())
                                .setLifetime(Lifetime.FOREVER)
                                .setService(DemoService.class)
                                .setTrigger(Trigger.executionWindow(60, 60 * interval_time))     //interval time 5 mins
                                .setRecurring(true)
//                                        .setReplaceCurrent(false)
//                                        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                                .setConstraints(
                                        // only run on an unmetered network
                                        Constraint.ON_ANY_NETWORK
                                );

                builder.setExtras(myExtrasBundle);

                myApp.jobDispatcher.mustSchedule(builder.build());

            }
        }

        if (!m_bPause) {
            setRecyclerViewData(0);
        } else {

            m_bPause = !m_bPause;
            setRecyclerViewData(3);
        }
    }

    private void setRecyclerViewData(int status) {

        List<com.freewebsitemonitor.UserModel.AddMonitor> tempMonitorLists = new ArrayList<com.freewebsitemonitor.UserModel.AddMonitor>();

        switch (status) {
            case 0:     //total
                binding.relSubAll.setBackgroundResource(R.drawable.round_border2);
                binding.relSubDown.setBackgroundResource(R.drawable.round_border);
                binding.relSubUp.setBackgroundResource(R.drawable.round_border);
                binding.relSubPause.setBackgroundResource(R.drawable.round_border);

                tempMonitorLists = allMonitorList;

                break;
            case 1:     //active

                binding.relSubAll.setBackgroundResource(R.drawable.round_border);
                binding.relSubDown.setBackgroundResource(R.drawable.round_border);
                binding.relSubUp.setBackgroundResource(R.drawable.round_border2);
                binding.relSubPause.setBackgroundResource(R.drawable.round_border);

                tempMonitorLists = upMonitorList;
                break;
            case 2:     //unactive
                binding.relSubAll.setBackgroundResource(R.drawable.round_border);
                binding.relSubDown.setBackgroundResource(R.drawable.round_border2);
                binding.relSubUp.setBackgroundResource(R.drawable.round_border);
                binding.relSubPause.setBackgroundResource(R.drawable.round_border);

                tempMonitorLists = downMonitorList;
                break;
            case 3:
                binding.relSubAll.setBackgroundResource(R.drawable.round_border);
                binding.relSubDown.setBackgroundResource(R.drawable.round_border);
                binding.relSubUp.setBackgroundResource(R.drawable.round_border);
                binding.relSubPause.setBackgroundResource(R.drawable.round_border2);

                tempMonitorLists = pauseMonitorList;
                break;
        }

//        if (allMonitorList.size() > 0) {
//            binding.txtVwTotal.setVisibility(View.VISIBLE);
//            binding.txtVwTotal.setText(String.valueOf(allMonitorList.size()));
//        }

//        if (upMonitorList.size() > 0) {
//            binding.txtVwActive.setVisibility(View.VISIBLE);
//            binding.txtVwActive.setText(String.valueOf(upMonitorList.size()));
//        }

        if (downMonitorList.size() > 0) {
            binding.txtVwDown.setVisibility(View.VISIBLE);
            binding.txtVwDown.setText(String.valueOf(downMonitorList.size()));
        }

//        if (pauseMonitorList.size() > 0) {
//            binding.txtVwDown.setVisibility(View.VISIBLE);
//            binding.txtVwPause.setText(String.valueOf(pauseMonitorList.size()));
//        }

        CustomAdapterMonitor customAdapterMonitor = new CustomAdapterMonitor(myApp.getContext(), tempMonitorLists);
        binding.recyVwMonitor.setAdapter(customAdapterMonitor);
    }

    private void monitorListSort(ArrayList<com.freewebsitemonitor.UserModel.AddMonitor> data) {
        List<com.freewebsitemonitor.UserModel.AddMonitor> upMonitorList = new ArrayList<com.freewebsitemonitor.UserModel.AddMonitor>();
        List<com.freewebsitemonitor.UserModel.AddMonitor> downMonitorList = new ArrayList<AddMonitor>();

        for (int i = 0 ; i < data.size() ; i++) {
            ArrayList<MonitorStatus> arrStatus = data.get(i).getStatus();

            if (data.get(i).getActive().equals("1")) {
                if (arrStatus.size() > 0) {
                    if (arrStatus.get(0).getStatus().equals("1")) {
                        upMonitorList.add(data.get(i));
                    } else if (arrStatus.get(0).getStatus().equals("2")) {
                        myApp.globalMonitorList.add(data.get(i));
                    }
                }
            } else {
                if (arrStatus.size() > 0) {
                    downMonitorList.add(data.get(i));
                }
            }

        }

        for (int j = 0 ; j < upMonitorList.size() ; j++) {
            myApp.globalMonitorList.add(upMonitorList.get(j));
        }

        for (int k = 0 ; k < downMonitorList.size() ; k++) {
            myApp.globalMonitorList.add(downMonitorList.get(k));
        }
    }


}
