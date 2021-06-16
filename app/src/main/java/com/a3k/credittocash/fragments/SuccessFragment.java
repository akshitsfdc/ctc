package com.a3k.credittocash.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a3k.credittocash.R;

public class SuccessFragment extends Fragment {


    private boolean success;
    private ImageView imageIndicator;
    private TextView msgText;

    public SuccessFragment(boolean success){
        this.success = success;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_success, container, false);
        initObjects(view);
        initValues();
        return view;
    }

    private void initObjects(View view){
       imageIndicator = view.findViewById(R.id.imageIndicator);
       msgText = view.findViewById(R.id.msgText);
    }
    private void initValues(){

        if(this.success){
            imageIndicator.setImageResource(R.drawable.ic_baseline_success);
            String msgStr = "Transaction Successful !";
            msgText.setText(msgStr);
        }else {
            imageIndicator.setImageResource(R.drawable.ic_twotone_error_24);
            String msgStr = "Transaction Failed !";
            msgText.setText(msgStr);
        }
    }

}