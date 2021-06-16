package com.a3k.credittocash.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.a3k.credittocash.R;
import com.a3k.credittocash.models.PaymentMethod;
import com.a3k.credittocash.models.Transaction;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

public class TransactionDetailsFragment extends Fragment {

    private AppCompatActivity currentActivity;

    private TextView statusValue, amountValue, serviceChargeValue, totalAmountValue, tIdValue, tTimeValue, rReasonValue;
    private View upiMethodLayout, paytmMethodLayout, paypalMethodLayout, bankDetailsMethod;
    private TextView nickNameUpi, nickNamePayTm, nickNamePaypal, nickNameAccount;
    private EditText upiText, paytmText, paypalText, accountNumberText, holderNameText, ifscCodeText;
    private AutoCompleteTextView accountTypePicker;

    private View rejectionLayout;

    private PaymentMethod paymentMethod;

    private Transaction transaction;

    public TransactionDetailsFragment(AppCompatActivity currentActivity, Transaction transaction){
        this.currentActivity = currentActivity;
        this.transaction = transaction;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_details, container, false);

        paymentMethod = transaction.getPaymentMethod();

        initObjects(view);
        initValues();
        return view;
    }

    private void initObjects(View itemView){


        rejectionLayout = itemView.findViewById(R.id.rejectionLayout);
        rReasonValue = itemView.findViewById(R.id.rReasonValue);

        statusValue = itemView.findViewById(R.id.statusValue);
        amountValue = itemView.findViewById(R.id.amountValue);
        serviceChargeValue = itemView.findViewById(R.id.serviceChargeValue);
        totalAmountValue = itemView.findViewById(R.id.totalAmountValue);
        tIdValue = itemView.findViewById(R.id.tIdValue);
        tTimeValue = itemView.findViewById(R.id.tTimeValue);

        upiMethodLayout = itemView.findViewById(R.id.upiMethodLayout);
        paytmMethodLayout = itemView.findViewById(R.id.paytmMethodLayout);
        paypalMethodLayout = itemView.findViewById(R.id.paypalMethodLayout);
        bankDetailsMethod = itemView.findViewById(R.id.bankDetailsMethod);

        nickNameUpi = itemView.findViewById(R.id.nickNameUpi);
        nickNamePayTm = itemView.findViewById(R.id.nickNamePayTm);
        nickNamePaypal = itemView.findViewById(R.id.nickNamePaypal);
        nickNameAccount = itemView.findViewById(R.id.nickNameAccount);

        upiText = itemView.findViewById(R.id.upiText);
        paytmText = itemView.findViewById(R.id.paytmText);
        paypalText = itemView.findViewById(R.id.paypalText);
        accountNumberText = itemView.findViewById(R.id.accountNumberText);
        holderNameText = itemView.findViewById(R.id.holderNameText);
        ifscCodeText = itemView.findViewById(R.id.ifscCodeText);
        accountTypePicker = itemView.findViewById(R.id.accountTypePicker);

    }

    private void initValues(){

        statusValue.setText(transaction.getStatus());

        setStatus(transaction.getStatus(), statusValue);

        DecimalFormat f = new DecimalFormat("##.00");
        String amtStr =  f.format(transaction.getFinalAmount());

        String amountString = currentActivity.getString(R.string.rs_symbol)+" "+amtStr;

        amountValue.setText(amountString);

        String scStr = transaction.getServiceCharge()+"%";
        serviceChargeValue.setText(scStr);

        String tAmStr = currentActivity.getString(R.string.rs_symbol) + " "+transaction.getAmount();
        totalAmountValue.setText(tAmStr);

        tIdValue.setText(transaction.getTransactionId());

        Date date = new Date(transaction.getTransactionTime());
        DateFormat simple = DateFormat.getDateTimeInstance();

        String dateString = simple.format(date);

        tTimeValue.setText(dateString);

        if(TextUtils.equals(transaction.getStatus().toLowerCase(), currentActivity.getString(R.string.status_rejected))){
            rejectionLayout.setVisibility(View.VISIBLE);
            rReasonValue.setText(transaction.getRejectReason());
        }

        distributeByType();

    }
    private void setStatus(String status, TextView statusLabel){

        if(status == null){
            return;
        }
        int resourceId = R.drawable.rounded_corner_label;
        switch (status.toLowerCase()){
            case "pending":{
                resourceId = R.drawable.rounded_corner_label;
                break;
            }
            case "transferred":{
                resourceId = R.drawable.rounded_corner_label_approved;
                break;
            }
            case "rejected":{
                resourceId = R.drawable.rounded_corner_label_rejected;
                break;
            }
        }

        Drawable d = ResourcesCompat.getDrawable(currentActivity.getResources(), resourceId, null);
        statusLabel.setBackground(d);
    }

    private void distributeByType(){

        String type = paymentMethod.getType().toLowerCase();

        switch (type){
            case "upi":{
                upiSetup();
                break;
            }
            case "paytm":{
                paytmSetup();
                break;
            }
            case "paypal":{
                paypalSetup();
                break;
            }
            case "bank transfer":{
                accountSetup();
                break;
            }

        }
    }

    private void upiSetup(){

        upiMethodLayout.setVisibility(View.VISIBLE);
        nickNameUpi.setText(paymentMethod.getNickName());
        upiText.setText(paymentMethod.getUpiId());

    }
    private void paytmSetup(){

        paytmMethodLayout.setVisibility(View.VISIBLE);
        nickNamePayTm.setText(paymentMethod.getNickName());
        paytmText.setText(paymentMethod.getPayTmNumber());

    }
    private void paypalSetup(){

        paypalMethodLayout.setVisibility(View.VISIBLE);
        nickNamePaypal.setText(paymentMethod.getNickName());
        paypalText.setText(paymentMethod.getPaypalId());

    }
    private void accountSetup(){

        bankDetailsMethod.setVisibility(View.VISIBLE);
        nickNameAccount.setText(paymentMethod.getNickName());
        accountNumberText.setText(paymentMethod.getAccountNumber());
        holderNameText.setText(paymentMethod.getBeneficiaryName());
        ifscCodeText.setText(paymentMethod.getIfscCode());
        accountTypePicker.setText(paymentMethod.getAccountType());
    }

}