package com.monitorfree.CustomAdapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monitorfree.R;
import com.monitorfree.databinding.CustomCheckpointsBinding;
import com.monitorfree.databinding.CustomMonitorBinding;

/**
 * Created by jassi on 01-08-2016.
 */

public class CustomAdapterCheckpoints extends RecyclerView.Adapter<CustomAdapterCheckpoints.Holder> {

    Context context;

    public CustomAdapterCheckpoints(Context context) {
        this.context = context;


    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_checkpoints, parent, false);
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

        CustomCheckpointsBinding binding;

        public Holder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


//
}
