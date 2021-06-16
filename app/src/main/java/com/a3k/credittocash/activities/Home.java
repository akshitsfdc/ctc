package com.a3k.credittocash.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.a3k.credittocash.R;
import com.a3k.credittocash.fragments.AddPaymentOptionFragment;
import com.a3k.credittocash.fragments.HomeFragment;
import com.a3k.credittocash.fragments.HowItWorksFragment;
import com.a3k.credittocash.fragments.PaymentFragment;
import com.a3k.credittocash.fragments.PaymentMethodsFragment;
import com.a3k.credittocash.fragments.SuccessFragment;
import com.a3k.credittocash.fragments.TransactionHistoryFragment;
import com.a3k.credittocash.models.AppInfo;
import com.a3k.credittocash.models.Transaction;
import com.a3k.credittocash.models.User;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.ListenerRegistration;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Home extends BaseActivity implements PaymentResultListener {

    private FloatingActionButton creditButton;
    public View containerFrame;
    private BottomAppBar bottomAppBar;
    public AppInfo appInfo;
    public User user;
    public PaymentFragment paymentFragment;
    private Transaction transaction;

    private ListenerRegistration userListener;

    public TextView siteTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Checkout.preload(getApplicationContext());

        initObjects();
        setListeners();

        if(appInfo == null){
            loadAppInfo();
        }


        if(user == null){
            loadUser(fireAuthService.getUserId());
        }

        siteTitle.setText(getString(R.string.home_title));
    }


    private void initObjects(){

        containerFrame = findViewById(R.id.containerFrame);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        creditButton = findViewById(R.id.creditButton);
        siteTitle = findViewById(R.id.siteTitle);

    }
    private void setListeners(){


        creditButton.setOnClickListener(v -> {
            startPaymentFlow();
        });

        bottomAppBar.setOnMenuItemClickListener(item -> {

            switch (item.getItemId()){
                case R.id.methods:
                    openPaymentMethods();
                    break;
                case R.id.addMethods:
                    routing.openFragmentOver(new AddPaymentOptionFragment(this),
                            getString(R.string.add_payment_tag));
                    break;
                case R.id.removeMethods:
                    routing.openFragmentOver(new PaymentMethodsFragment(this),
                            getString(R.string.payment_methods_tag));
                    break;
                case R.id.history_out:
                    siteTitle.setText(getString(R.string.payment_history_title));
                    openPaymentHistory();
                    break;
                case R.id.history:
                    siteTitle.setText(getString(R.string.payment_history_title));
                    openPaymentHistory();
                    break;
                case R.id.hiw:
                    siteTitle.setText(getString(R.string.hiw_title));
                    routing.openFragment(new HowItWorksFragment(this),
                            getString(R.string.hiw_tag), containerFrame.getId());
                    break;
                case R.id.support:
                    sendEmailToSupport(getString(R.string.support_email));
                    break;
                case R.id.share:
                    sendInvitation();
                    break;
                case R.id.logout:
                    logoutNow();
                    break;
            }
            return false;
        });

        bottomAppBar.setNavigationOnClickListener(v -> {

            siteTitle.setText(getString(R.string.app_name));

            Fragment homeFragment = new HomeFragment(this);

            routing.openFragment(homeFragment, getString(R.string.home_tag), containerFrame.getId());

        });
    }


    public void sendInvitation() {

        try {

            String invitationLink = getString(R.string.app_url);
            Intent shareIntent =
                    new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey I am using this amazing app: "+getString(R.string.app_name));
            String shareMessage =
                    "Hey I am using this amazing app: \n\nInstall "+getString(R.string.app_name)+" app from store and login.\n\nThis app provides a very easy way to transfer money from your credit card to your bank or UPI. This application also provide service to take instant cash loan from your credit card \n\nThis is very portable to use, you can get loan or cash anytime and anywhere very easily.\n\n\n"
                            + invitationLink;
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));


        } catch(Exception e) {
//e.toString();
            uiUtils.showShortSnakeBar("Something went wrong try again later!");
        }
    }
    private void openPaymentHistory(){
        routing.openFragment(new TransactionHistoryFragment(this),
                getString(R.string.transaction_history_tag), containerFrame.getId());
    }
    private void logoutNow(){
        fireAuthService.logoutUser();
        routing.navigateAndClear(LoginEmail.class);
    }
    private void openPaymentMethods(){
        routing.openFragmentOver(new PaymentMethodsFragment(this), getString(R.string.payment_methods_tag));
    }
    private void loadAppInfo(){

        showLoading();
        fireStoreService.getData(getString(R.string.info_coll), getString(R.string.info_doc))
                .addOnSuccessListener(documentSnapshot -> {

                    this.appInfo = documentSnapshot.toObject(AppInfo.class);
                    hideLoading();
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                });
    }

    private void startPaymentFlow(){

        siteTitle.setText(getString(R.string.transaction_title));

        paymentFragment = new PaymentFragment(this);
        routing.openFragment(paymentFragment, getString(R.string.payment_tag), containerFrame.getId());
    }

    private void loadUser(String userId){


        userListener = fireStoreService.getDocReference(getString(R.string.user_coll), userId)
           .addSnapshotListener((snapshot, e) -> {

               if (e != null) {
                   Log.w(TAG, "Listen failed.", e);
                   return;
               }

               String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                       ? "Local" : "Server";

               if (snapshot != null && snapshot.exists()) {

                   if(user == null){
                       user = snapshot.toObject(User.class);
                       siteTitle.setText(getString(R.string.app_name));
                       routing.openFragment(new HomeFragment(this), getString(R.string.home_tag), containerFrame.getId());
                       checkForDeviceToken();
                   }else {
                       User userUpdate = snapshot.toObject(User.class);
                       if(userUpdate != null){
                           updateLocalUser(userUpdate);
                       }

                   }


               } else {
                   Log.d(TAG, source + " data: null");
               }

           });


    }

    private void updateLocalUser(User userUpdate){
        this.user.setName(userUpdate.getName());
        this.user.setPicUrl(userUpdate.getPicUrl());
        this.user.setEmail(userUpdate.getEmail());
        this.user.setTotalMoneyCashed(userUpdate.getTotalMoneyCashed());
        this.user.setUserId(userUpdate.getUserId());
        this.user.setPaymentMethods(userUpdate.getPaymentMethods());
    }
    private void checkForDeviceToken(){
        fireStoreService.getToken().addOnSuccessListener(s -> {
            if(user != null){
                if(!TextUtils.equals(s, user.getNotificationToken())){
                    Map<String, Object> map = new HashMap<>();
                    map.put("notificationToken", s);
                    fireStoreService.updateData(getString(R.string.user_coll), user.getUserId(), map);
                }
            }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });


    }
    public void checkout(Transaction transaction){

        if(transaction.getAmount() > 50000){
            uiUtils.showShortSnakeBar("There is a limit of 50,000 per transaction");
            return;
        }
        transaction.setTransactionTime(new Date().getTime());

        this.transaction = transaction;

        long finalAmount = transaction.getAmount() * 100;


        this.transaction.setStatus("Authorised");
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        checkout.setKeyID(appInfo.getPaymentKeyProduction());

        /**
         * Set your logo here
         */
//        checkout.setImage(R.drawable.logo);


        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            JSONObject methods = new JSONObject();

            methods.put("netbanking",false);
            methods.put("card", true);
            methods.put("wallet",false);
            methods.put("upi",false);

            options.put("name", getString(R.string.app_name));
            options.put("description", "Cashify request #"+transaction.getTransactionTime());
            options.put("image", "https://firebasestorage.googleapis.com/v0/b/credittocash-a6b0d.appspot.com/o/credit_to_cash_logo.png?alt=media&token=ba5ddae1-ca6b-44e2-96ec-523e5bc3cf27");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
//            options.put("theme.color", "#3399cc");

            options.put("currency", transaction.getCurrency());
            options.put("amount", finalAmount);//pass amount in currency subunits

            options.put("prefill.email", this.user.getEmail());
            options.put("prefill.contact","");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);
            options.put("method",methods);


            checkout.open(this, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }


    @Override
    public void onPaymentSuccess(String paymentID) {

        Log.d(TAG, "onPaymentSuccess: "+transaction.getTransactionTime());

        runOnUiThread(() -> {

            this.transaction.setTransactionId(paymentID);
            showLoading();
            String tranColPath = getString(R.string.user_coll)+"/"+this.user.getUserId()+"/"+getString(R.string.user_transaction);
            fireStoreService.setData(tranColPath, String.valueOf(transaction.getTransactionTime()), transaction)
                    .addOnSuccessListener(aVoid -> {
                        hideLoading();
                        routing.openFragmentStateLoss(new SuccessFragment(true), getString(R.string.success_fail_tag), containerFrame.getId());

                    })
                    .addOnFailureListener(e -> {
                        hideLoading();
                        routing.openFragmentStateLoss(new SuccessFragment(true), getString(R.string.success_fail_tag), containerFrame.getId());
                    });
        });

    }
    @Override
    public void onPaymentError(int code, String response) {
        routing.openFragmentStateLoss(new SuccessFragment(false), getString(R.string.success_fail_tag), containerFrame.getId());
    }

    @Override
    public void onBackPressed() {

        try {
            if(paymentFragment != null){
                paymentFragment.setPaymentMethodAdapter();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        super.onBackPressed();
    }

    private void sendEmailToSupport(String toEmail){

        if(user == null){
            return;
        }
        String subject = user.getName()+" Query @"+user.getEmail();
        String message = "Hi Support,";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",toEmail, null));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(userListener != null){
            userListener.remove();
            user = null;
        }
    }
}