package com.akamaidev.urbancrawlapp;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akamai.android.sdk.Logger;
import com.akamai.android.sdk.VocNetworkQualityStatus;
import com.akamai.android.sdk.VocService;
import com.akamaidev.urbancrawlapp.helpers.Analytics;
import com.akamaidev.urbancrawlapp.helpers.Constants;
import com.akamaidev.urbancrawlapp.helpers.Helper;
import com.akamaidev.urbancrawlapp.helpers.ImageLoader;
import com.akamaidev.urbancrawlapp.helpers.LogEvent;
import com.akamaidev.urbancrawlapp.jsonobjs.Place;
import com.akamaidev.urbancrawlapp.presenters.PlaceDetailsPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

public class PlaceDetailsActivity extends AppCompatActivity implements PlaceDetailsPresenter.PlaceDetailsCallback{

    public static String KEY_CITY_NAME = "KEY_CITY_NAME";
    public static String KEY_PLACE_ID = "KEY_PLACE_ID";
    public static String KEY_CITY_THUMB = "KEY_CITY_THUMB";

    Toolbar toolbar;
    Place place;

    int placeId;
    String cityName, cityThumb;

    Helper helper;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    long totalLoadTime = 0;

    VocService vocService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(getIntent().getExtras() != null){
            placeId = getIntent().getExtras().getInt(KEY_PLACE_ID);
            cityName = getIntent().getExtras().getString(KEY_CITY_NAME);
            cityThumb = getIntent().getExtras().getString(KEY_CITY_THUMB);
        }else{
            finish();
            return;
        }

        vocService = VocService.createVocService(getApplicationContext());
        Logger.setLevel(Constants.AkaLogLevel);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        helper = new Helper();
        helper.setupNavDrawer(this,
                drawerLayout,
                (ListView)findViewById(R.id.drawer_list),
                findViewById(R.id.log_overlay),
                (ScrollView)findViewById(R.id.log_overlay_scroller));

        PlaceDetailsPresenter placeDetailsPresenter = new PlaceDetailsPresenter();
        placeDetailsPresenter.getPlaceDetails(placeId, this, this);

        Analytics.logEvent(this, "Place Details Activity Opened");

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

        int networkQuality = vocService.getNetworkQuality();

        switch (networkQuality){
            case VocNetworkQualityStatus.EXCELLENT:
                findViewById(R.id.log_network_quality_view).setBackgroundColor(getResources().getColor(R.color.nq_excellent));
                ((TextView)findViewById(R.id.log_network_quality_tv)).setText("NETWORK QUALITY : EXCELLENT");
                break;
            case VocNetworkQualityStatus.GOOD:
                findViewById(R.id.log_network_quality_view).setBackgroundColor(getResources().getColor(R.color.nq_good));
                ((TextView)findViewById(R.id.log_network_quality_tv)).setText("NETWORK QUALITY : GOOD");
                break;
            case VocNetworkQualityStatus.POOR:
                findViewById(R.id.log_network_quality_view).setBackgroundColor(getResources().getColor(R.color.nq_poor));
                ((TextView)findViewById(R.id.log_network_quality_tv)).setText("NETWORK QUALITY : POOR");
                break;
            case VocNetworkQualityStatus.NA:
                break;
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onPlaceDetailsSuccess(Place place) {
        this.place = place;
        putValuesOnViews(true);
    }

    public void putValuesOnViews(boolean isSuccess){

        ((TextView)findViewById(R.id.place_name)).setText(place.getName());
        ((TextView)findViewById(R.id.place_cityname)).setText(cityName);
        ImageLoader.loadImage(this, cityThumb, ((ImageView)findViewById(R.id.place_thumbnail)));
        ImageLoader.loadImage(this, place.getHeroimage(), ((ImageView)findViewById(R.id.place_heroimage)));
        ((TextView)findViewById(R.id.place_name_small)).setText(cityName+" > "+place.getName());
        ((TextView)findViewById(R.id.place_num_images)).setText(String.valueOf(place.getNumimages())+" images >");
        ((TextView)findViewById(R.id.place_about_label)).setText("About "+place.getName());
        ((TextView)findViewById(R.id.place_about_value)).setText(place.getDescription());
        ((TextView)findViewById(R.id.place_besttime_value)).setText(place.getTimings());
        findViewById(R.id.place_num_images).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoGalleryActivity = new Intent(PlaceDetailsActivity.this, PhotoGalleryActivity.class);
                photoGalleryActivity.putExtra(PhotoGalleryActivity.KEY_PLACE_ID, place.getId());
                photoGalleryActivity.putExtra(PhotoGalleryActivity.KEY_PLACE_NAME, place.getName());
                photoGalleryActivity.putExtra(PhotoGalleryActivity.KEY_PLACE_CITY, cityName);
                photoGalleryActivity.putExtra(PhotoGalleryActivity.KEY_PLACE_THUMB, cityThumb);
                startActivity(photoGalleryActivity);
            }
        });

    }

    @Override
    public void onPlaceDetailsError() {
        Snackbar.make(toolbar, "Some Error occurred", Snackbar.LENGTH_LONG).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_plan_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_settings){
            if(drawerLayout.isDrawerOpen(navigationView)){
                drawerLayout.closeDrawer(Gravity.RIGHT);
            }else{
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
            return true;
        }

        if(helper.handleMenuClick(this, toolbar, item)){
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
