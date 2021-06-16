package com.a3k.credittocash.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.a3k.credittocash.R;
import com.a3k.credittocash.activities.Home;
import com.a3k.credittocash.models.PaymentMethod;
import com.a3k.credittocash.models.TAG;
import com.a3k.credittocash.models.Transaction;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentFragment extends Fragment {

    private AppCompatActivity currentActivity;
    private Button makePaymentButton, addMethodButton, removeButton;
    private EditText amountEditText;
    private TextView descriptionText;
    private float serviceChargePer;
    private TextInputLayout outlinedTextField, currencyInputLayout;
    private AutoCompleteTextView paymentMethodPicker, currencyPicker;
    private List<String> supportedCurrencies;
    private Map<String, String> currencyMap;

    private Transaction transaction;

    public PaymentFragment() {
        // Required empty public constructor
        TAG.debug = "PaymentFragment";
    }

    public PaymentFragment(AppCompatActivity currentActivity) {
        this.currentActivity = currentActivity;

        serviceChargePer = ((Home)currentActivity).appInfo.getServiceChargePer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("KK5G", "onDestroy: paymentFragment ");
        ((Home)currentActivity).paymentFragment = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_payment, container, false);
        initObjects(view);
        addEventListeners();
        setupInitValues();
        return  view;
    }

    private void initObjects(View view){

        supportedCurrencies = new ArrayList<>();
        currencyMap = new HashMap<>();

        transaction = new Transaction();

        makePaymentButton = view.findViewById(R.id.makePaymentButton);
        amountEditText = view.findViewById(R.id.amountEditText);
        descriptionText = view.findViewById(R.id.descriptionText);
        outlinedTextField = view.findViewById(R.id.outlinedTextField);
        addMethodButton = view.findViewById(R.id.addMethodButton);
        removeButton = view.findViewById(R.id.removeButton);
        paymentMethodPicker = view.findViewById(R.id.paymentMethodPicker);
        currencyPicker = view.findViewById(R.id.currencyPicker);
        currencyInputLayout = view.findViewById(R.id.currencyInputLayout);

    }

    private void addEventListeners(){

        addMethodButton.setOnClickListener(v -> {
            ((Home)currentActivity).routing.openFragmentOver(new AddPaymentOptionFragment(currentActivity),
                   currentActivity.getString(R.string.add_payment_tag));
        });
        removeButton.setOnClickListener(v -> {
            ((Home)currentActivity).routing.openFragmentOver(new PaymentMethodsFragment(currentActivity),
                    currentActivity.getString(R.string.payment_methods_tag));
        });
        makePaymentButton.setOnClickListener(v -> {
            if(!validate()){
                return;
            }
            ((Home)currentActivity).checkout(transaction);
        });


        amountEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() == 0){
                    descriptionText.setText("");
                    transaction.setAmount(0);
                    transaction.setFinalAmount(0);
                }else {
                    try {
                        long amount = Long.parseLong(s.toString());
                        transaction.setAmount(amount);
                        changeDescriptionText(amount);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        paymentMethodPicker.setOnItemClickListener( (parent, view, position, id) -> {
            String selectedValue = ((TextView)view).getText().toString();
            if(TextUtils.equals(selectedValue.toLowerCase(), "no method")){
                paymentMethodPicker.setText("");
                transaction.setPaymentMethod(null);

            }else {
                Home home = ((Home)currentActivity);
                transaction.setPaymentMethod(home.user.getPaymentMethods().get(position));
            }
            //bankAccountType = ((TextView)view).getText().toString();
        });
        currencyPicker.setOnItemClickListener( (parent, view, position, id) -> {
            transaction.setCurrency(currencyMap.get(((TextView)view).getText().toString()));
        });

    }



    private void setupInitValues(){

        transaction.setServiceCharge(serviceChargePer);

        String finalText = "Service charge "+serviceChargePer+"% will be applied";
        outlinedTextField.setHelperText(finalText);

        supportedCurrencies.add("INR");
        currencyMap.put("INR", "INR");


        currencyPicker.setText(currencyMap.get("INR"));
        transaction.setCurrency(currencyMap.get("INR"));
        String currencyHelperText;
        if(supportedCurrencies.size() > 1){
            currencyHelperText = supportedCurrencies.size()+" currencies supported";
        }else {
            currencyHelperText = supportedCurrencies.size()+" currency supported";
        }

        currencyInputLayout.setHelperText(currencyHelperText);

        setPaymentMethodAdapter();


    }

    public void setPaymentMethodAdapter(){

        paymentMethodPicker.setText("");
        List<String> methods = new ArrayList<String>();
        Home home = ((Home)currentActivity);

        if(home.user.getPaymentMethods().size() == 0){
            methods.add("No Method");
        }
        for (PaymentMethod paymentMethod : home.user.getPaymentMethods()) {
            methods.add(paymentMethod.getNickName());
        }

        paymentMethodPicker.setAdapter(new ArrayAdapter(currentActivity, R.layout.list_item, methods));
    }
    private void changeDescriptionText(long amount){



        float perValue = amount * serviceChargePer / 100;

        float deductedAmount = amount - perValue;

        transaction.setFinalAmount(deductedAmount);

        String finalString;

        finalString = currentActivity.getString(R.string.rs_symbol)+""+deductedAmount+" "+currentActivity.getString(R.string.transaction_deduction_note);
        descriptionText.setText(finalString);


    }

    private boolean validate(){

        boolean valid = true;

        String amount = this.amountEditText.getText().toString().trim();
        String currency = this.currencyPicker.getText().toString().trim();
        String method = this.paymentMethodPicker.getText().toString().trim();

        if(TextUtils.isEmpty(amount)){
            this.amountEditText.setError("Required");
            valid = false;
        }else {
            this.amountEditText.setError(null);
        }
        if(TextUtils.isEmpty(currency)){
            this.currencyPicker.setError("Required");
            valid = false;
        }else {
            this.currencyPicker.setError(null);
        }
        if(TextUtils.isEmpty(method)){
            this.paymentMethodPicker.setError("Required");
            valid = false;
        }else {
            this.paymentMethodPicker.setError(null);
        }

        return valid;
    }


}