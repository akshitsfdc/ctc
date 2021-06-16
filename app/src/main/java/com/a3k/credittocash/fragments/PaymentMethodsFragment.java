
package com.a3k.credittocash.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.a3k.credittocash.R;
import com.a3k.credittocash.activities.Home;
import com.a3k.credittocash.adapters.PaymentMethodsRecyclerViewAdapter;
import com.a3k.credittocash.models.PaymentMethod;

import java.util.List;

public class PaymentMethodsFragment extends Fragment {




    private AppCompatActivity currentActivity;
    private RecyclerView recyclerView;
    private View backButton, emptyLayout;

    public PaymentMethodsFragment(){

    }

    public PaymentMethodsFragment(AppCompatActivity currentActivity) {
        this.currentActivity = currentActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_payment_methods, container, false);

        initObjects(view);
        setInitValues();
        setListeners();
        return view;
    }

    private void initObjects(View view){

        recyclerView = view.findViewById(R.id.recyclerView);
        backButton = view.findViewById(R.id.backButton);
        emptyLayout = view.findViewById(R.id.emptyLayout);

    }
    private void setInitValues(){



        recyclerView.setLayoutManager(new LinearLayoutManager(currentActivity));

        setAdapter(((Home)currentActivity).user.getPaymentMethods());


    }

    public void setAdapter(List<PaymentMethod> paymentMethods){

        recyclerView.setAdapter(new PaymentMethodsRecyclerViewAdapter(paymentMethods, currentActivity, this));

        if(paymentMethods.size() == 0){
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }



    }
    private void setListeners(){
        backButton.setOnClickListener(v -> {
            closeSelf();
        });

    }
    private void closeSelf(){
        try {
            if(((Home)this.currentActivity).paymentFragment != null){
                ((Home)this.currentActivity).paymentFragment.setPaymentMethodAdapter();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        ((Home)this.currentActivity).routing.hideFragment(currentActivity.getString(R.string.payment_methods_tag));

    }
}