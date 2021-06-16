package com.a3k.credittocash.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a3k.credittocash.R;
import com.a3k.credittocash.activities.BaseProfileActivity;
import com.a3k.credittocash.activities.Home;
import com.a3k.credittocash.adapters.TransactionsRecyclerViewAdapter;
import com.a3k.credittocash.models.Transaction;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private AppCompatActivity currentActivity;

    private ImageView profilePic, editImg;
    private TextView userName, userEmail, cashifiedAmount;
    private RecyclerView recentRecyclerView;
    private int recentLimit = 10;
    private List<Transaction> recentTransactions;

    private ListenerRegistration transactionListener;

    private View emptyLayout;

    public HomeFragment() {
    }




    public HomeFragment(AppCompatActivity currentActivity) {

        this.currentActivity = currentActivity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initObjects(view);
        return view;
    }

    private void initObjects(View view) {

        profilePic = view.findViewById(R.id.profilePic);
        editImg = view.findViewById(R.id.editImg);
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        cashifiedAmount = view.findViewById(R.id.cashifiedAmount);
        emptyLayout = view.findViewById(R.id.emptyLayout);

        editImg.setOnClickListener(v -> {
            ((Home)currentActivity).routing.navigate(BaseProfileActivity.class, false);
        });

        recentRecyclerView = view.findViewById(R.id.recentRecyclerView);

        recentRecyclerView.setLayoutManager(new LinearLayoutManager(currentActivity));

        recentRecyclerView.addItemDecoration(new DividerItemDecoration(currentActivity,
                DividerItemDecoration.VERTICAL));


        recentTransactions = new ArrayList<>();

        loadRecentTransactions();

        setUserProfile();

    }

    private void setUserProfile(){
        Home home = (Home)currentActivity;
        if(home.user == null){
            return;
        }
        home.showRemoteImage(home.user.getPicUrl(), profilePic);
        if(home.user.getName().length() > 0){
            userName.setText(home.user.getName());

        }else {
            userName.setText("No Name");
        }

        userEmail.setText(home.user.getEmail());

        String cashifiedString;
        if(home.user.getTotalMoneyCashed() > 0){
            DecimalFormat f = new DecimalFormat("##.00");
            String amtStr =  f.format(home.user.getTotalMoneyCashed());

            cashifiedString = ""+getString(R.string.rs_symbol)+" "+amtStr;
        }else {
            cashifiedString = ""+getString(R.string.rs_symbol)+" "+0 ;
        }

        cashifiedAmount.setText(cashifiedString);
    }

    private void setAdapter(List<Transaction> transactions){

        recentRecyclerView.setAdapter(new TransactionsRecyclerViewAdapter(transactions, currentActivity));

        if(transactions.size() == 0){
            recentRecyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }else {
            recentRecyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }


    }

    private void loadRecentTransactions() {

        Home home = ((Home) currentActivity);

        String collectionPath = currentActivity.getString(R.string.user_coll) + "/" + home.user.getUserId() + "/" + getString(R.string.user_transaction);

        home.showLoading();
        transactionListener = home.fireStoreService.getTransactions(collectionPath, recentLimit,

                (value, error) -> {

                    home.hideLoading();

                    if (error != null) {
                        return;
                    }

                    if (value == null) {
                        return;
                    }

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                Transaction transaction = dc.getDocument().toObject(Transaction.class);
                                recentTransactions.add(transaction);
                                break;
                            case MODIFIED:
                                Transaction tranUp = dc.getDocument().toObject(Transaction.class);
                                updateTransaction(tranUp);;
                                break;
                            case REMOVED:
                                Transaction tranDelete = dc.getDocument().toObject(Transaction.class);
                                deleteTransaction(tranDelete);
                                break;
                        }
                    }

                    setAdapter(recentTransactions);
                });

    }

    private void updateTransaction(Transaction transactionUpdate){

        Transaction transaction = getTransactionById(transactionUpdate.getTransactionId());

        if(transaction == null){
            return;
        }

        transaction.setServiceCharge(transactionUpdate.getServiceCharge());
        transaction.setTransactionId(transactionUpdate.getTransactionId());
        transaction.setFinalAmount(transactionUpdate.getFinalAmount());
        transaction.setCurrency(transactionUpdate.getCurrency());
        transaction.setPaymentMethod(transactionUpdate.getPaymentMethod());
        transaction.setAmount(transactionUpdate.getAmount());
        transaction.setStatus(transactionUpdate.getStatus());
        transaction.setTransactionTime(transactionUpdate.getTransactionTime());

    }
    private void deleteTransaction(Transaction transactionUpdate){

        Transaction transaction = getTransactionById(transactionUpdate.getTransactionId());

        if(transaction == null){
            return;
        }

       this.recentTransactions.remove(transaction);

    }
    private Transaction getTransactionById(String transactionId){

        for (Transaction tran : this.recentTransactions
        ) {
            if(TextUtils.equals(transactionId, tran.getTransactionId())){
               return tran;
            }
        }

        return null;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        if(transactionListener != null){
            transactionListener.remove();
        }
    }
}