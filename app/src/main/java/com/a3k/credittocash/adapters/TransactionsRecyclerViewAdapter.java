package com.a3k.credittocash.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.a3k.credittocash.R;
import com.a3k.credittocash.activities.Home;
import com.a3k.credittocash.fragments.TransactionDetailsFragment;
import com.a3k.credittocash.models.Transaction;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class TransactionsRecyclerViewAdapter extends RecyclerView.Adapter<TransactionsRecyclerViewAdapter.ViewHolder> {

    private List<Transaction> transactions;
    private AppCompatActivity currentActivity;

    public TransactionsRecyclerViewAdapter(List<Transaction> transactions, AppCompatActivity currentActivity){
        this.currentActivity = currentActivity;
        this.transactions = transactions;
    }


    @NonNull
    @Override
    public TransactionsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_recycler_layout, parent, false);
        return new TransactionsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsRecyclerViewAdapter.ViewHolder holder, int position) {

        Transaction transaction = transactions.get(position);

        DecimalFormat f = new DecimalFormat("##.00");
        String amtStr =  f.format(transaction.getFinalAmount());

        String amountString = currentActivity.getString(R.string.rs_symbol)+" "+amtStr;

        holder.amount.setText(amountString);
        holder.transactionId.setText(transaction.getTransactionId());

        setStatus(transaction.getStatus(), holder.statusLabel);

        holder.statusLabel.setText(transaction.getStatus());

        Date date = new Date(transaction.getTransactionTime());
        DateFormat simple = DateFormat.getDateTimeInstance();

        String dateString = simple.format(date);

        holder.dateTime.setText(dateString);

        holder.parent.setOnClickListener(v -> {

            ((Home)currentActivity).routing.openFragment(new TransactionDetailsFragment(currentActivity, transaction),
                    currentActivity.getString(R.string.transaction_details_tag), ((Home)currentActivity).containerFrame.getId());

            ((Home)currentActivity).siteTitle.setText(currentActivity.getString(R.string.transaction_detail_title));

        });

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
    @Override
    public int getItemCount() {
        return this.transactions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView amount, transactionId, dateTime, statusLabel;

        private View parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amount = itemView.findViewById(R.id.amount);
            transactionId = itemView.findViewById(R.id.transactionId);
            dateTime = itemView.findViewById(R.id.dateTime);
            statusLabel = itemView.findViewById(R.id.statusLabel);

            parent = itemView.findViewById(R.id.parent);

        }
    }
}
