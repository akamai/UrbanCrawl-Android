package com.akamaidev.urbancrawlapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.akamaidev.urbancrawlapp.helpers.CityPlacesListAdapter;
import com.akamaidev.urbancrawlapp.helpers.Constants;
import com.akamaidev.urbancrawlapp.helpers.Helper;
import com.akamaidev.urbancrawlapp.helpers.ImageLoader;
import com.akamaidev.urbancrawlapp.helpers.LogEvent;
import com.akamaidev.urbancrawlapp.jsonobjs.City;
import com.akamaidev.urbancrawlapp.presenters.CityDetailsPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

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

public class CityDetailsActivity extends AppCompatActivity implements CityDetailsPresenter.CityDetailsCallback, CityPlacesListAdapter.OnRowClickedListener {

    public static String KEY_CITY_ID = "KEY_CITY_ID";
    City city;
    Toolbar toolbar;
    private final int PERMISSION_CONS = 100;

    long totalLoadTime = 0;

    RecyclerView recyclerView;

    Helper helper;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    VocService vocService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_details);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);

        vocService = VocService.createVocService(getApplicationContext());
        Logger.setLevel(Constants.AkaLogLevel);

        helper = new Helper();
        helper.setupNavDrawer(this,
                drawerLayout,
                (ListView)findViewById(R.id.drawer_list),
                findViewById(R.id.log_overlay),
                (ScrollView)findViewById(R.id.log_overlay_scroller));

        recyclerView = (RecyclerView)findViewById(R.id.home_places_recycler);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);

        int cidyId = getIntent().getExtras().getInt(KEY_CITY_ID, -1);

        if(cidyId == -1){
            finish();
            return;
        }

        totalLoadTime = 0;

        CityDetailsPresenter cityDetailsPresenter = new CityDetailsPresenter();
        cityDetailsPresenter.getCityDetails(cidyId, this, this);

        Analytics.logEvent(this, "City Details Activity Opened");
    }

    @Override
    public void onCityDetailsSuccess(City cityForHome) {
        this.city = cityForHome;
        putValuesOnViews(true);
    }

    @Override
    public void onCityDetailsError() {
        Snackbar.make(toolbar, "Some Error occured", Snackbar.LENGTH_LONG).show();
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

    public void putValuesOnViews(boolean isSuccess){

        ((TextView)findViewById(R.id.home_cityname)).setText(city.getName());
        ((TextView)findViewById(R.id.home_countryname)).setText(city.getCountryname());
        ImageLoader.loadImage(this, city.getThumburl(), ((ImageView)findViewById(R.id.home_city_thumbnail)));
        ImageLoader.loadImage(this, city.getHeroimage(), ((ImageView)findViewById(R.id.home_heroimage)));
        ((TextView)findViewById(R.id.home_cityname_small)).setText(city.getName());
        ((TextView)findViewById(R.id.home_aboutcity_label)).setText("About "+city.getName());
        ((TextView)findViewById(R.id.home_aboutcity_value)).setText(city.getDescription());
        ((TextView)findViewById(R.id.home_besttime_value)).setText(city.getBesttime());
        ((TextView)findViewById(R.id.home_currency_value)).setText(city.getCurrency());
        ((TextView)findViewById(R.id.home_language_value)).setText(city.getLanguage());
        ((TextView)findViewById(R.id.home_population_value)).setText(city.getPopulation());
        requestPermissionAndPopulateDistance();

        CityPlacesListAdapter cityPlacesListAdapter = new CityPlacesListAdapter(this, city.getPlaces(), city.getName(), this);
        recyclerView.setAdapter(cityPlacesListAdapter);

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

    /**
     * Checks if the app has Location permissions.
     * If the app does, proceeds to calculate the distance of a city from the current location
     * If the app doesn't, requests it
     */
    void requestPermissionAndPopulateDistance(){
        //Check and acquire COARSE location permissions, to calculate distance, if not already granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                // show rationale
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CONS);
            }
        }else{
            //If the permission is already acquired, calculate the distance and update the view
            calculateAndPopulateDistance();
        }
    }

    /**
     * Callback to get the result of the permission request.
     * If the person using the app denies (or has denied in the past), the app shows a Snackbar informing about it
     * If the person approves the request, app continues to calculate the distance
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case PERMISSION_CONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    calculateAndPopulateDistance();
                }else{
                    findViewById(R.id.home_distance_container).setVisibility(View.INVISIBLE);
                    Snackbar.make(toolbar, "Location couldn't be determined to calculate distance", Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * Uses a local helper's function to:
     *      a) Acquire current coarse location with any possible provider
     *      b) Calculate the distance of the current city from the above location
     * Once calculated, the distance is displayed n 'xx,yzabc.nn' format
     *
     * If for some reasons the distance couldn't be claculated, N/A (Not Available) is displayed
     */
    void calculateAndPopulateDistance(){

        float distanceResult = helper.calculateDistance(this, city.getLat(), city.getLng());

        if(distanceResult > 0){
            ((TextView)findViewById(R.id.home_distance_value)).setText(String.format(Locale.US,"%,.2f Kms", distanceResult));
        }else{
            ((TextView)findViewById(R.id.home_distance_value)).setText("N/A");
        }
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
            //open drawer
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

    @Override
    public void onRowClicked(int adapterPosition) {
        Intent placeDetails = new Intent(CityDetailsActivity.this, PlaceDetailsActivity.class);
        placeDetails.putExtra(PlaceDetailsActivity.KEY_CITY_NAME, city.getName());
        placeDetails.putExtra(PlaceDetailsActivity.KEY_PLACE_ID, city.getPlaces().get(adapterPosition).getId());
        placeDetails.putExtra(PlaceDetailsActivity.KEY_CITY_THUMB, city.getThumburl());
        startActivity(placeDetails);
    }
}
