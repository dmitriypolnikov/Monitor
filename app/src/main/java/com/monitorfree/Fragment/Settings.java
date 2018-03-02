package com.monitorfree.Fragment;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.monitorfree.MyApp;
import com.monitorfree.R;
import com.monitorfree.databinding.FragmentSettingsBinding;

import javax.inject.Inject;

import static com.monitorfree.Util.GlobalKeys.USER_EMAIL;


/**
 * Created by jassi on 30-07-2016.
 */

public class Settings extends Fragment {


    FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        binding.txtemail.setText(MyApp.instance.getKey(USER_EMAIL));
        binding.txtDeviceName.setText(Build.MODEL);

        return binding.getRoot();
    }
}
