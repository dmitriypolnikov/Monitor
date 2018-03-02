package com.monitorfree.CustomAdapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;


import com.monitorfree.Activities.MonitorInfo;
import com.monitorfree.R;
import com.monitorfree.UserModel.AddMonitor;
import com.monitorfree.databinding.CustomMonitorBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jassi on 01-08-2016.
 */

public class CustomAdapterMonitor extends RecyclerView.Adapter<CustomAdapterMonitor.Holder> {

    Context context;
    List<AddMonitor> addMonitorList;

    public CustomAdapterMonitor(Context context, List<AddMonitor> addMonitorList) {
        this.context = context;
        this.addMonitorList = addMonitorList;

    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_monitor, parent, false);
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        holder.binding.crdVwMonitor.setOnClickListener((View) -> {
            Intent intent = new Intent(context, MonitorInfo.class);

            intent.putExtra("name",addMonitorList.get(position).getName());
            intent.putExtra("id",addMonitorList.get(position).getId());
            intent.putExtra("startDate",addMonitorList.get(position).getStartDate());
            intent.putExtra("status", addMonitorList.get(position).getStatus().get(0).getStatus());
            intent.putExtra("active", addMonitorList.get(position).getActive());
            intent.putExtra("position", position);
            intent.putExtra("interval", addMonitorList.get(position).getInterval());


            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        if (addMonitorList.get(position).getActive().equals("1")) {
            if (addMonitorList.get(position).getStatus().get(0).getStatus().equals("1")) {
                holder.binding.imgStatus.setBackgroundResource(R.drawable.ic_up_arrow);
            } else {
                holder.binding.imgStatus.setBackgroundResource(R.drawable.ic_down);
            }
        } else {
            holder.binding.imgStatus.setBackgroundResource(R.drawable.ic_pause1);
        }

        holder.binding.tvLastDate.setText(addMonitorList.get(position).getStatus().get(0).getMobileDateTime());
        holder.binding.tvMonitorName.setText("" + addMonitorList.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return addMonitorList.size();
    }


    class Holder extends RecyclerView.ViewHolder {

        CustomMonitorBinding binding;

        public Holder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


//
}
