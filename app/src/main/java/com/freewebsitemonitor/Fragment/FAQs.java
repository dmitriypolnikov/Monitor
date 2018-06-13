package com.freewebsitemonitor.Fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.freewebsitemonitor.CustomAdapter.ExpendableListAdapter;
import com.freewebsitemonitor.R;
import com.freewebsitemonitor.databinding.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by jassi on 30-07-2016.
 */

public class FAQs extends Fragment {

    FragmentFaqsBinding binding;

    String[] caption = {"1. Can I get alerts even when my device is shut down?", "2. Why do you say my site is down, when it is up?", "3. I love this free app, what can I do to help?"};
    String[] subDetails = {"a. No, but this is the magic. We are able to provide free limitless service, because we rely on your device rather than our own (high cost) servers. Sure, you can buy paid monitoring service with one of our competitors, but what would happen when your site is down? You’ll get notified by text message to your phone. The same phone you say is now shut down anyhow.", "a. The Internet is dynamic. One moment a website can be down, and the next second it can be up. If it is only a glitch, such as a momentary power outage, causing the downtime, your site may already be back to full functionality by the time you check it. \n"+
            "b. Timeouts may mean that your website or server is loading so slowly that it causes a timeout error – but when you check things out, the site will succeed in loading.\n"+
            "c. HTTP Error 403 means your device is forbidden from visiting the website. \n"+
            "d. HTTP Errors 500/502/503 may mean that something is wrong with your server that must be corrected.", "a. We created this free app on our spare time. We wanted to save money for people that had to buy expensive monitoring services. The thing that makes us wake up in the morning and keep improving it, is seeing your positive reviews. If you can leave a positive review, and after that share the app on facebook or Twitter, we’ll be honored."};

    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_faqs, container, false);

        ExpendableListAdapter listAdapter;

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add(caption[0]);
        listDataHeader.add(caption[1]);
        listDataHeader.add(caption[2]);

        List<String> first = new ArrayList<String>();
        first.add(subDetails[0]);

        List<String> second = new ArrayList<String>();
        second.add(subDetails[1]);

        List<String> third = new ArrayList<String>();
        third.add(subDetails[2]);

        listDataChild.put(listDataHeader.get(0), first);
        listDataChild.put(listDataHeader.get(1), second);
        listDataChild.put(listDataHeader.get(2), third);

        listAdapter = new ExpendableListAdapter(getActivity(), listDataHeader, listDataChild);
        binding.expendLV.setAdapter(listAdapter);

        return binding.getRoot();
    }
}
