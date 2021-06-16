package com.a3k.credittocash.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.a3k.credittocash.R;
import com.a3k.credittocash.fragments.Loading;
import com.a3k.credittocash.fragments.MessageFragment;
import com.a3k.credittocash.service.FireAuthService;
import com.a3k.credittocash.service.FireStoreService;
import com.a3k.credittocash.utils.LocalFileUtils;
import com.a3k.credittocash.utils.Routing;
import com.a3k.credittocash.utils.UIUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


public class BaseActivity extends AppCompatActivity {

    public Routing routing;
    public String TAG;
    public UIUtils uiUtils;
    public LocalFileUtils localFileUtils;
    public FireStoreService fireStoreService;
    public FireAuthService fireAuthService;
    public MessageFragment messageFragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        routing = new Routing(this);
        uiUtils = new UIUtils(this);
        localFileUtils = new LocalFileUtils(this);

        uiUtils.setParentView(android.R.id.content);
        TAG = this.getClass().getName();

        fireStoreService = new FireStoreService();
        fireAuthService = new FireAuthService();

        fragmentManager = getSupportFragmentManager();

    }

    protected void showImage(int imageId, ImageView imageView){
        Glide.with(this).load(imageId).into(imageView);
    }
    public void showRemoteImage(String picUrl, ImageView imageView){
        Glide.with(this).load(picUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        //                holder.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .error(R.drawable.ic_profile_placeholder).fallback(R.drawable.ic_profile_placeholder)
                .into(imageView);
    }
    public void showMsgFragment(){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.msg_frg_tag));

        if(fragment == null){
           messageFragment = new MessageFragment();
            fragmentManager.beginTransaction()
                    .replace(android.R.id.content, messageFragment,getString(R.string.msg_frg_tag))
                    .commit();
        }

    }
    public void hideMsgFragment(){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.msg_frg_tag));

        if(fragment != null){
            try{
                fragmentManager.beginTransaction().remove(fragment).commit();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            View view = getCurrentFocus();
            if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                int scrcoords[] = new int[2];
                view.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                float y = ev.getRawY() + view.getTop() - scrcoords[1];
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                    ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
            return super.dispatchTouchEvent(ev);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public void showLoading(){

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("global_loader");
        if(fragment == null){
            Loading loading = new Loading();
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, loading,"global_loader")
                    .commitAllowingStateLoss();
        }

        //context.findViewById(R.id.loadingIndicator).setVisibility(View.VISIBLE);
    }

    public void hideLoading(){

        try{
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("global_loader");

            if(fragment != null){
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //context.findViewById(R.id.loadingIndicator).setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("global_loader");
        if(fragment == null) {
            super.onBackPressed();
        }
    }

    private void exitWarning(){
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, id) -> killTheApp())
                .setNegativeButton("No", null)
                .show();
    }
    private void killTheApp(){
        super.onBackPressed();
    }
}