package com.a3k.credittocash.adapters;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.a3k.credittocash.R;
import com.a3k.credittocash.activities.Home;
import com.a3k.credittocash.fragments.PaymentMethodsFragment;
import com.a3k.credittocash.models.PaymentMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentMethodsRecyclerViewAdapter extends RecyclerView.Adapter<PaymentMethodsRecyclerViewAdapter.ViewHolder> {


    private List<PaymentMethod> paymentMethods;
    private AppCompatActivity currentActivity;
    private Fragment currentFragment;
    private PaymentMethodsRecyclerViewAdapter.ViewHolder holder;
    private PaymentMethod paymentMethod;

    public PaymentMethodsRecyclerViewAdapter(List<PaymentMethod> paymentMethods
            , AppCompatActivity currentActivity
            ,Fragment currentFragment){

        this.currentFragment = currentFragment;
        this.paymentMethods = paymentMethods;
        this.currentActivity = currentActivity;

    }

    @NonNull
    @Override
    public PaymentMethodsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_methods_layout, parent, false);
        return new PaymentMethodsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentMethodsRecyclerViewAdapter.ViewHolder holder, int position) {
        this.paymentMethod = paymentMethods.get(position);
        this.holder = holder;
        distributeByType();
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

        holder.upiMethodLayout.setVisibility(View.VISIBLE);
        holder.nickNameUpi.setText(paymentMethod.getNickName());
        holder.upiText.setText(paymentMethod.getUpiId());
        String methodId = paymentMethod.getMethodId();
        holder.upiDelete.setOnClickListener(v -> {
            deleteMethod(getIndexOfPaymentMethod(methodId));
        });

    }
    private void paytmSetup(){

        holder.paytmMethodLayout.setVisibility(View.VISIBLE);
        holder.nickNamePayTm.setText(paymentMethod.getNickName());
        holder.paytmText.setText(paymentMethod.getPayTmNumber());
        String methodId = paymentMethod.getMethodId();
        holder.paytmDelete.setOnClickListener(v -> {
            deleteMethod(getIndexOfPaymentMethod(methodId));
        });
    }
    private void paypalSetup(){

        holder.paypalMethodLayout.setVisibility(View.VISIBLE);
        holder.nickNamePaypal.setText(paymentMethod.getNickName());
        holder.paypalText.setText(paymentMethod.getPaypalId());
        String methodId = paymentMethod.getMethodId();
        holder.paypalDelete.setOnClickListener(v -> {
            deleteMethod(getIndexOfPaymentMethod(methodId));
        });
    }
    private void accountSetup(){

        holder.bankDetailsMethod.setVisibility(View.VISIBLE);
        holder.nickNameAccount.setText(paymentMethod.getNickName());
        holder.accountNumberText.setText(paymentMethod.getAccountNumber());
        holder.holderNameText.setText(paymentMethod.getBeneficiaryName());
        holder.ifscCodeText.setText(paymentMethod.getIfscCode());
        holder.accountTypePicker.setText(paymentMethod.getAccountType());
        String methodId = paymentMethod.getMethodId();
        holder.accountDelete.setOnClickListener(v -> {
            deleteMethod(getIndexOfPaymentMethod(methodId));
        });
    }

    private void deleteMethod(int index){

        if(index == -1){
            return;
        }
        Home home = (Home)currentActivity;

        PaymentMethod paymentMethod = this.paymentMethods.remove(index);


        Map<String, Object> map = new HashMap<>();
        map.put(currentActivity.getString(R.string.payment_method_field), this.paymentMethods);

        home.showLoading();
        home.fireStoreService.updateData(currentActivity.getString(R.string.user_coll), home.user.getUserId(), map)
                .addOnSuccessListener(aVoid -> {
                    home.uiUtils.showShortSnakeBar("Payment method has been removed");
                    home.hideLoading();
                    ((PaymentMethodsFragment)currentFragment).setAdapter(this.paymentMethods);
                })
                .addOnFailureListener(e -> {
                    home.uiUtils.showShortSnakeBar("Error! Can not remove this method at this time kindly try again later");
                    home.hideLoading();
                    this.paymentMethods.add(index, paymentMethod);
                });

    }

    private int getIndexOfPaymentMethod(String methodId){

        for (int i = 0; i < this.paymentMethods.size(); ++i){
            if(TextUtils.equals(methodId, this.paymentMethods.get(i).getMethodId())){
                return i;
            }
        }

        return -1;
    }
    @Override
    public int getItemCount() {
        return paymentMethods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View upiMethodLayout, paytmMethodLayout, paypalMethodLayout, bankDetailsMethod;
        private Button upiDelete, paytmDelete, paypalDelete, accountDelete;
        private TextView nickNameUpi, nickNamePayTm, nickNamePaypal, nickNameAccount;
        private EditText upiText, paytmText, paypalText, accountNumberText, holderNameText, ifscCodeText;
        private AutoCompleteTextView accountTypePicker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            upiMethodLayout = itemView.findViewById(R.id.upiMethodLayout);
            paytmMethodLayout = itemView.findViewById(R.id.paytmMethodLayout);
            paypalMethodLayout = itemView.findViewById(R.id.paypalMethodLayout);
            bankDetailsMethod = itemView.findViewById(R.id.bankDetailsMethod);

            upiDelete = itemView.findViewById(R.id.upiDelete);
            paytmDelete = itemView.findViewById(R.id.paytmDelete);
            paypalDelete = itemView.findViewById(R.id.paypalDelete);
            accountDelete = itemView.findViewById(R.id.accountDelete);



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
    }
}
