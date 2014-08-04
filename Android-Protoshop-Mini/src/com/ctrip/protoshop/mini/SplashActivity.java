package com.ctrip.protoshop.mini;

import com.ctrip.protoshop.mini.util.Util;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class SplashActivity extends Activity {

    private View mMiniView;
    private TranslateAnimation mTranslateAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mMiniView = findViewById(R.id.mini_imageView);
        
        MiniApplication.getInstance().cachePath=Util.getLocalRootFile().getAbsolutePath();

        mTranslateAnimation = new TranslateAnimation(0, 0, 200, 0);
        mTranslateAnimation.setDuration(1000);
        mTranslateAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(getApplicationContext(), ProjectListActivity.class));
                finish();
            }
        });
        mMiniView.startAnimation(mTranslateAnimation);

    }

}
