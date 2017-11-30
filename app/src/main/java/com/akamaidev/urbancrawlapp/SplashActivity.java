package com.akamaidev.urbancrawlapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.akamaidev.urbancrawlapp.helpers.Analytics;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/*
 * Copyright 2017 Akamai Technologies, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class SplashActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 3000;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            SplashActivity.this.finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash);

        mContentView = findViewById(R.id.app_first_name);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, DELAY_MILLIS);

        Analytics.logEvent(this, "App (Splash Activity) Opened");
    }
}
