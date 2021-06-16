package com.a3k.credittocash.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.a3k.credittocash.service.FireAuthService;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity {

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }catch (Exception e){
            e.printStackTrace();
        }

        fireAuthService = new FireAuthService();
        user = fireAuthService.getCurrentUser();

        Log.d(TAG, "onCreate: user >>> "+user);
        int SPLASH_DISPLAY_LENGTH = 3000;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                routeUser();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void routeUser(){

        if(user == null){
            routing.navigate(LoginEmail.class, true);
        }else {
            if(!user.isEmailVerified()){
                sendVerificationEmail();
            }else {
                routing.navigate(Home.class, true);
            }

        }
    }
    private void sendVerificationEmail(){

        fireAuthService.sendEmailVerification(user)
                .addOnSuccessListener(aVoid -> {
                    navigateToEmailVerification(true);
                })
                .addOnFailureListener(e -> {
                    navigateToEmailVerification(false);
                });

    }
    private void navigateToEmailVerification(boolean isEmailSent){

        routing.appendParams("isEmailSent", isEmailSent);
        routing.navigate(AccountVerificationActivity.class, true);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}