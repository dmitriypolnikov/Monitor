package com.monitorfree.Activities;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.monitorfree.CustomAdapter.CustomAdapterCheckpoints;
import com.monitorfree.CustomAdapter.CustomAdapterEvents;
import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.databinding.ActivityMonitorInfoBinding;

import java.util.ArrayList;

import javax.inject.Inject;

public class MonitorInfo extends AppCompatActivity {


    ActivityMonitorInfoBinding binding;

    @Inject
    MyApp myApp;

    ArrayList<String> labels = new ArrayList<String>();
    String[] timeSlot = {"2", "4", "6", "8", "10"};
    String monitorName, startDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_monitor_info);
        MyApp.component().inject(this);


        monitorName = getIntent().getStringExtra("name");
        startDate = getIntent().getStringExtra("startDate");

        binding.tvMonitorName.setText(monitorName);
        binding.txtVwStartDate.setText(startDate);

        labels.add("2016");
        labels.add("2015");
        labels.add("2014");
        labels.add("2013");
        labels.add("2012");

        ArrayList<BarEntry> bargroup1 = new ArrayList<>();
        bargroup1.add(new BarEntry(8f, 0));
        bargroup1.add(new BarEntry(2f, 1));
        bargroup1.add(new BarEntry(5f, 2));
        bargroup1.add(new BarEntry(20f, 3));
        bargroup1.add(new BarEntry(15f, 4));


        BarDataSet bardataset = new BarDataSet(bargroup1, "cell");
        bardataset.setColor(Color.parseColor("#68b668"));

        BarData data = new BarData(labels, bardataset);
        binding.barchart.setData(data);

        binding.include.imgVw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.include.txtVw.setText("Monitor");

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(myApp.getContext(), LinearLayoutManager.HORIZONTAL, false);

        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(myApp.getContext(), LinearLayoutManager.HORIZONTAL, false);

        binding.recyVwCheckPoint.setLayoutManager(layoutManager);
        binding.recyVwEvents.setLayoutManager(layoutManager2);

        binding.recyVwEvents.setAdapter(new CustomAdapterEvents(myApp.getContext()));
        binding.recyVwCheckPoint.setAdapter(new CustomAdapterCheckpoints(myApp.getContext()));
        binding.recyVwCheckPoint.setNestedScrollingEnabled(false);
        binding.recyVwEvents.setNestedScrollingEnabled(false);


        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlot);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.spnr.setAdapter(aa);

        binding.linLastTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.spnr.performClick();

            }
        });


        binding.spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                binding.txtTimeSlot.setText("For Last " + timeSlot[position] + " hours");


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

}
