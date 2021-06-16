package com.a3k.credittocash.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a3k.credittocash.R;
import com.a3k.credittocash.activities.Home;

public class HowItWorksFragment extends Fragment {

    private TextView slaText;
    private AppCompatActivity currentActivity;

    public HowItWorksFragment(AppCompatActivity currentActivity){
        this.currentActivity = currentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_how_it_works, container, false);
        slaText = view.findViewById(R.id.slaText);

        String slaString = "All payment to the user will be transferred with in "
                +((Home)currentActivity).appInfo.getSla()+
                " "+((Home)currentActivity).appInfo.getSlaUnit()+" (to the selected payment methods at the time of transaction)";
        slaText.setText(slaString);
        return view;
    }
}