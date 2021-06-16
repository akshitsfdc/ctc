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

import com.a3k.credittocash.R;
import com.a3k.credittocash.activities.Home;
import com.a3k.credittocash.adapters.TransactionsRecyclerViewAdapter;
import com.a3k.credittocash.models.Transaction;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryFragment extends Fragment {


    private AppCompatActivity currentActivity;
    private RecyclerView recentRecyclerView;
    private int recentLimit = 50;
    private List<Transaction> transactions;

    private ListenerRegistration transactionListener;

    private View emptyLayout;

    public TransactionHistoryFragment() {
        // Required empty public constructor
    }

    public TransactionHistoryFragment(AppCompatActivity currentActivity) {

        this.currentActivity = currentActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
        initObjects(view);
        return view;
    }

    private void initObjects(View view){

        emptyLayout = view.findViewById(R.id.emptyLayout);
        recentRecyclerView = view.findViewById(R.id.recentRecyclerView);

        recentRecyclerView.setLayoutManager(new LinearLayoutManager(currentActivity));

        recentRecyclerView.addItemDecoration(new DividerItemDecoration(currentActivity,
                DividerItemDecoration.VERTICAL));
        

        transactions = new ArrayList<>();

        loadRecentTransactions();
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
//                        Log.d("RRTY", "onEvent: called "+value.size());
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
                                transactions.add(transaction);
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

                    setAdapter(transactions);
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

        this.transactions.remove(transaction);

    }
    private Transaction getTransactionById(String transactionId){

        for (Transaction tran : this.transactions
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