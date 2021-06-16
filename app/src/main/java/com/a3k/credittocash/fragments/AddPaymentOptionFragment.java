package com.a3k.credittocash.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.a3k.credittocash.R;
import com.a3k.credittocash.activities.Home;
import com.a3k.credittocash.models.PaymentMethod;
import com.a3k.credittocash.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPaymentOptionFragment extends Fragment {



    private AppCompatActivity currentActivity;
    private AutoCompleteTextView methodPicker, accountTypePicker;
    private PaymentMethod paymentMethod;
    private View bankDetailsMethod, upiMethodLayout, paytmMethodLayout, paypalMethodLayout;
    private EditText nickNameTextField, upiText, paytmText, paypalText, accountNumberText, holderNameText, ifscCodeText;
    private Button addMethodButton;
    private ImageView backButton;
    private List<View> views;
    private String bankAccountType;
    public AddPaymentOptionFragment(){

    }

    public AddPaymentOptionFragment(AppCompatActivity currentActivity) {
        this.currentActivity = currentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =  inflater.inflate(R.layout.fragment_add_payment_option, container, false);

        initObjects(view);
        setInitValues();
        setListeners();
        return view;
    }
    
    private void initObjects(View view){

        paymentMethod = new PaymentMethod();
        views = new ArrayList<>();

        paymentMethod.setMethodId(""+new Date().getTime());

        bankDetailsMethod = view.findViewById(R.id.bankDetailsMethod);
        upiMethodLayout = view.findViewById(R.id.upiMethodLayout);
        paytmMethodLayout = view.findViewById(R.id.paytmMethodLayout);
        paypalMethodLayout = view.findViewById(R.id.paypalMethodLayout);
        upiText = view.findViewById(R.id.upiText);
        paytmText = view.findViewById(R.id.paytmText);
        paypalText = view.findViewById(R.id.paypalText);
        accountNumberText = view.findViewById(R.id.accountNumberText);
        holderNameText = view.findViewById(R.id.holderNameText);
        ifscCodeText = view.findViewById(R.id.ifscCodeText);
        nickNameTextField = view.findViewById(R.id.nickNameTextField);
        addMethodButton = view.findViewById(R.id.addMethodButton);
        backButton = view.findViewById(R.id.backButton);

        accountTypePicker = view.findViewById(R.id.accountTypePicker);
        methodPicker = view.findViewById(R.id.methodPicker);


    }
    private void setInitValues(){

        bankDetailsMethod.setVisibility(View.GONE);
        upiMethodLayout.setVisibility(View.GONE);
        paytmMethodLayout.setVisibility(View.GONE);
        paypalMethodLayout.setVisibility(View.GONE);
        views.add(bankDetailsMethod);
        views.add(upiMethodLayout);
        views.add(paytmMethodLayout);
        views.add(paypalMethodLayout);

        List<String> methodsType = new ArrayList<String>();
        methodsType.add("UPI");
        methodsType.add("PayTM");
        methodsType.add("PayPal");
        methodsType.add("Bank Transfer");

        List<String> accountTypes = new ArrayList<>();
        accountTypes.add("Saving");
        accountTypes.add("Current");

        methodPicker.setAdapter(new ArrayAdapter(currentActivity, R.layout.list_item, methodsType));
        accountTypePicker.setAdapter(new ArrayAdapter(currentActivity, R.layout.list_item, accountTypes));
    }
    private void setListeners(){

        methodPicker.setOnItemClickListener( (parent, view, position, id) -> {
            String methodStr = ((TextView)view).getText().toString();
            paymentMethod.setType(methodStr);
            String nickName = methodStr+" - "+paymentMethod.getMethodId();
            nickNameTextField.setText(nickName);
            viewSelector(methodStr.toLowerCase());
        });

        accountTypePicker.setOnItemClickListener( (parent, view, position, id) -> {
            bankAccountType = ((TextView)view).getText().toString();
        });
        backButton.setOnClickListener(v -> {
            closeSelf();
        });

        addMethodButton.setOnClickListener(v -> {

            if(validate()){
                if(validateFields()){
                    savePaymentMethod();
                }
            }

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
        ((Home)this.currentActivity).routing.hideFragment(currentActivity.getString(R.string.add_payment_tag));
    }
    private void savePaymentMethod(){
        setFinalObject();

        Home home = ((Home)this.currentActivity);

        home.showLoading();

        User user = home.user;

        user.getPaymentMethods().add(this.paymentMethod);

        Map<String, Object> paymentMethodsMap = new HashMap<>();
        paymentMethodsMap.put(currentActivity.getString(R.string.payment_method_field), user.getPaymentMethods());

        home.fireStoreService.updateData(currentActivity.getString(R.string.user_coll),
                user.getUserId(), paymentMethodsMap)
                .addOnSuccessListener(aVoid -> {
                      home.uiUtils.showShortSnakeBar("Payment method added successfully");
                      home.hideLoading();
                      closeSelf();
                })
                .addOnFailureListener(e -> {
                    home.hideLoading();
                    home.uiUtils.showShortSnakeBar("Error adding Payment method, please try again later");
                });

    }

    private boolean validate(){

        boolean valid = true;

        String methodStr = this.paymentMethod.getType();
        String nickNameStr = this.nickNameTextField.getText().toString().trim();

        if(TextUtils.isEmpty(methodStr)){
            this.methodPicker.setError("Required");
            valid = false;
        }else {
            this.methodPicker.setError(null);
        }
        if(TextUtils.isEmpty(nickNameStr)){
            this.nickNameTextField.setError("Required");
            valid = false;
        }else {
            this.nickNameTextField.setError(null);
        }

        return valid;
    }

    private boolean validateFields(){

        String selection = paymentMethod.getType().toLowerCase();

        switch (selection){
            case "upi":{
                return validateUpi();
            }
            case "paytm":{
                return validatePaytm();
            }
            case "paypal":{
                return validatePaypal();
            }
            case "bank transfer":{
                return validateAccount();
            }
            default:{
                return true;
            }

        }
    }
    private void viewSelector(String selection){

        switch (selection){
            case "upi":{
                toggleVisibility(upiMethodLayout);
                break;
            }
            case "paytm":{
                toggleVisibility(paytmMethodLayout);
                break;
            }
            case "paypal":{
                toggleVisibility(paypalMethodLayout);
                break;
            }
            case "bank transfer":{
                toggleVisibility(bankDetailsMethod);
                break;
            }


        }
    }
    private void toggleVisibility(View visibleView){
        visibleView.setVisibility(View.VISIBLE);
        for (View view:
             this.views) {
            if(view.getId() != visibleView.getId()){
                view.setVisibility(View.GONE);
            }

        }

    }

    private boolean validateUpi(){

        String upiStr = this.upiText.getText().toString().trim();

        if(TextUtils.isEmpty(upiStr)){
            this.upiText.setError("Required");
            return false;
        }else {
            this.upiText.setError(null);
        }

        if(!upiStr.contains("@")){
            this.upiText.setError("Invalid");
            return false;
        }else {
            this.upiText.setError(null);
        }

        return true;
    }
    private boolean validatePaytm(){

        String paytmStr = this.paytmText.getText().toString().trim();

        if(TextUtils.isEmpty(paytmStr)){
            this.paytmText.setError("Required");
            return false;
        }else {
            this.paytmText.setError(null);
        }


        return true;
    }
    private boolean validatePaypal(){

        String paypalStr = this.paypalText.getText().toString().trim();

        if(TextUtils.isEmpty(paypalStr)){
            this.paypalText.setError("Required");
            return false;
        }else {
            this.paypalText.setError(null);
        }


        return true;
    }

    private boolean validateAccount(){

        boolean valid = true;

        String accountNoStr = this.accountNumberText.getText().toString().trim();
        String ifscNoStr = this.ifscCodeText.getText().toString().trim();
        String holderNoStr = this.holderNameText.getText().toString().trim();

        if(TextUtils.isEmpty(accountNoStr)){
            this.accountNumberText.setError("Required");
            valid = false;
        }else {
            this.accountNumberText.setError(null);
        }
        if(TextUtils.isEmpty(ifscNoStr)){
            this.ifscCodeText.setError("Required");
            valid = false;
        }else {
            this.ifscCodeText.setError(null);
        }
        if(TextUtils.isEmpty(holderNoStr)){
            this.holderNameText.setError("Required");
            valid = false;
        }else {
            this.holderNameText.setError(null);
        }
        if(TextUtils.isEmpty(bankAccountType)){
            this.accountTypePicker.setError("Required");
            valid = false;
        }else {
            this.accountTypePicker.setError(null);
        }

        return valid;
    }

    private void setFinalObject(){

        String selection = paymentMethod.getType().toLowerCase();

        paymentMethod.setNickName(this.nickNameTextField.getText().toString().trim());

        switch (selection){
            case "upi":{
                paymentMethod.setUpiId(this.upiText.getText().toString().trim());
                paymentMethod.setPaypalId("");
                paymentMethod.setPayTmNumber("");
                removeAccountFromFinalObject();
                break;
            }
            case "paytm":{
                paymentMethod.setUpiId("");
                paymentMethod.setPaypalId("");
                paymentMethod.setPayTmNumber(this.paytmText.getText().toString().trim());
                removeAccountFromFinalObject();
                break;
            }
            case "paypal":{
                paymentMethod.setUpiId("");
                paymentMethod.setPaypalId(this.paypalText.getText().toString().trim());
                paymentMethod.setPayTmNumber("");
                removeAccountFromFinalObject();
                break;
            }
            case "bank transfer":{
                paymentMethod.setUpiId("");
                paymentMethod.setPaypalId("");
                paymentMethod.setPayTmNumber("");

                paymentMethod.setAccountNumber(this.accountNumberText.getText().toString().trim());
                paymentMethod.setBeneficiaryName(this.holderNameText.getText().toString().trim());
                paymentMethod.setIfscCode(this.ifscCodeText.getText().toString().trim());
                paymentMethod.setAccountType(bankAccountType);
                break;
            }

        }
    }

    private void removeAccountFromFinalObject(){
        paymentMethod.setAccountNumber("");
        paymentMethod.setBeneficiaryName("");
        paymentMethod.setIfscCode("");
        paymentMethod.setAccountType("");
    }


}