package com.a3k.credittocash.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.a3k.credittocash.R;
import com.a3k.credittocash.models.User;
import com.a3k.credittocash.service.FireAuthService;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class AccountVerificationActivity extends BaseActivity {

    FireAuthService fireAuthService;
    private TextView descText;
    private Button registerAgainButton, verifiedButton, resendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_verification);

        try {
            getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        }catch (Exception e){
            e.printStackTrace();
        }

        fireAuthService = new FireAuthService(this);
        descText = findViewById(R.id.descText);
        registerAgainButton = findViewById(R.id.registerAgainButton);
        verifiedButton = findViewById(R.id.verifiedButton);
        resendButton = findViewById(R.id.resendButton);

        verifiedButton.setOnClickListener(v -> {
            afterUserVerified();
        });
        resendButton.setOnClickListener(v -> {
            sendVerificationEmail();
        });
        registerAgainButton.setOnClickListener(v -> {
            fireAuthService.logoutUser();
            routing.navigateAndClear(LoginEmail.class);
        });

        boolean emailSent = (boolean)routing.getParam("isEmailSent");

        FirebaseUser user = fireAuthService.getCurrentUser();

        if(!emailSent){
            verifiedButton.setVisibility(View.GONE);
            String desStr = "We could not send you verification email to "+user.getEmail()+" at this moment, request you to pleas try again later. If email entered by you is incorrect kindly Reset Registration.";
            descText.setText(desStr);
            uiUtils.showLongSnakeBar("Could not send you verification email, please retry.");
        }else {

            String desStr = "We have sent you an email to "+user.getEmail()+" with account verification link, kindly check your inbox and click on verification link";
            descText.setText(desStr);
        }
    }

    private void afterUserVerified(){

        fireAuthService.reloadCurrentUser()
                .addOnSuccessListener(aVoid -> {

                    FirebaseUser user = fireAuthService.getCurrentUser();

                    if(user.isEmailVerified()){
                        createUser();
                    }else {
                        uiUtils.showLongSnakeBar("Please verify your email first, check your inbox!");
                    }

                })
                .addOnFailureListener(e -> {
                    uiUtils.showLongSnakeBar("Could not verify your account at this time, please check your email address.");
                });
    }

    private void createUser(){

        FirebaseUser firebaseUser = fireAuthService.getCurrentUser();
        User user = getUserObj(firebaseUser);
        showLoading();
        fireStoreService.setData(getString(R.string.user_coll), user.getUserId(), user)
            .addOnSuccessListener(aVoid -> {
                hideLoading();
                routing.navigateAndClear(BaseProfileActivity.class);

            })
            .addOnFailureListener(e -> {
                uiUtils.showLongSnakeBar("Something went wrong, please try again later.");
                hideLoading();
            });
    }
    private User getUserObj(FirebaseUser firebaseUser){

        User user = new User();
        user.setName("");
        user.setUserId(fireAuthService.getUserId());
        user.setPaymentMethods(new ArrayList<>());
        user.setEmail(fireAuthService.getCurrentUser().getEmail());

        user.setPicUrl("");

        return user;
    }
    private void sendVerificationEmail(){



        FirebaseUser user = fireAuthService.getCurrentUser();

        showLoading();


        fireAuthService.sendEmailVerification(user)
                .addOnSuccessListener(aVoid -> {
                    hideLoading();
                    uiUtils.showLongSnakeBar("Verification email sent to "+user.getEmail());
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    uiUtils.showLongSnakeBar("Failed to send you verification email at this moment, please try again later.");
                });
    }
}