package com.monitorfree.Fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monitorfree.R;
import com.monitorfree.databinding.FragmentFaqsBinding;
import com.monitorfree.databinding.FragmentSettingsBinding;


/**
 * Created by jassi on 30-07-2016.
 */

public class FAQs extends Fragment {

    FragmentFaqsBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_faqs, container, false);

        return binding.getRoot();
    }



}
