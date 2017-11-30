package com.akamaidev.urbancrawlapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akamaidev.urbancrawlapp.helpers.Analytics;
import com.akamaidev.urbancrawlapp.helpers.Helper;
import com.akamaidev.urbancrawlapp.helpers.ImageLoader;
import com.akamaidev.urbancrawlapp.helpers.LogEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

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

public class PhotoViewerActivity extends AppCompatActivity {

    public static final String KEY_IMG_URL = "KEY_IMAGE_URL";

    ImageViewTouch imageView;

    String imgUrl = null;

    long totalLoadTime = 0;
    Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        if(getIntent().getExtras() != null){
            imgUrl = getIntent().getExtras().getString(KEY_IMG_URL);
        }

        if(imgUrl != null){
            imageView = (ImageViewTouch)findViewById(R.id.photogallery_imageviewer);
            ImageLoader.loadImage(this, imgUrl, imageView);
        }else{
            this.finish();
        }

        helper = new Helper();

        Analytics.logEvent(this, "Photo Viewer Activity Opened");
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        if(helper.getDevMode(this)){
            findViewById(R.id.log_overlay).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.log_overlay).setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogEvent(LogEvent logEvent){
        ((TextView)findViewById(R.id.log_tv)).append("\n"+logEvent.getStatus()+" "+logEvent.getUrl()+" in "+logEvent.getTimeTakenInMillis());
        totalLoadTime+=logEvent.getTimeTakenInMillis();
        ((TextView)findViewById(R.id.log_tv_total_time)).setText("Total Load Time : "+(totalLoadTime)+" milliseconds");

        ((ScrollView)findViewById(R.id.log_overlay_scroller)).post(new Runnable() {
            @Override
            public void run() {
                ((ScrollView)findViewById(R.id.log_overlay_scroller)).fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
}
