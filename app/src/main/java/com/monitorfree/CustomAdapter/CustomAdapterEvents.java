package com.monitorfree.CustomAdapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monitorfree.R;
import com.monitorfree.databinding.CustomEventsBinding;
import com.monitorfree.databinding.CustomMonitorBinding;

/**
 * Created by jassi on 01-08-2016.
 */

public class CustomAdapterEvents extends RecyclerView.Adapter<CustomAdapterEvents.Holder>  {

    Context context;

    public CustomAdapterEvents(Context context) {
        this.context = context;


    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_events, parent, false);
        Holder holder = new Holder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

//        final ModelProfession user = modelProfessionArrayList.get(position);
        // add these lines


    }

    @Override
    public int getItemCount() {
        return 10;
    }


    class Holder extends RecyclerView.ViewHolder {

        CustomEventsBinding binding;

        public Holder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


//
}
