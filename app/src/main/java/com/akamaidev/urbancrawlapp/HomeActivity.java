package com.akamaidev.urbancrawlapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akamai.android.sdk.Logger;
import com.akamai.android.sdk.VocNetworkQualityStatus;
import com.akamai.android.sdk.VocService;
import com.akamaidev.urbancrawlapp.helpers.Analytics;
import com.akamaidev.urbancrawlapp.helpers.Constants;
import com.akamaidev.urbancrawlapp.helpers.Helper;
import com.akamaidev.urbancrawlapp.helpers.LogEvent;
import com.akamaidev.urbancrawlapp.jsonobjs.CityForList;
import com.akamaidev.urbancrawlapp.presenters.HomePresenter;
import com.akamaidev.urbancrawlapp.helpers.CitiesListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

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

public class HomeActivity extends AppCompatActivity implements CitiesListAdapter.OnRowClickedListener, HomePresenter.CitiesListCallback{

    RecyclerView recyclerView;
    Toolbar toolbar;

    ArrayList<CityForList> citiesList = new ArrayList<CityForList>();

    TextView statusTv;
    CitiesListAdapter adapter;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    long totalLoadTime = 0;

    Helper helper;

    VocService vocService;

    final int PERMISSION_CONS = 121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        recyclerView = (RecyclerView)findViewById(R.id.cities_list);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        adapter = new CitiesListAdapter(citiesList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        findViewById(R.id.cities_list).setVisibility(View.INVISIBLE);

        statusTv = (TextView)findViewById(R.id.cities_status);
        statusTv.setText("Loading Cities..");
        statusTv.setVisibility(View.VISIBLE);

        HomePresenter homePresenter = new HomePresenter();

        homePresenter.getCitiesNames(this, this);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CONS);
        }

        Analytics.logEvent(this, "Home Activity Opened");

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

        if(helper.handleMenuClick(this, recyclerView, item)){
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }

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
    public void onRowClicked(int adapterPosition){
        Intent cityViewerActivity = new Intent(this, CityDetailsActivity.class);
        cityViewerActivity.putExtra(CityDetailsActivity.KEY_CITY_ID, citiesList.get(adapterPosition).getId());
        startActivity(cityViewerActivity);
    }

    @Override
    public void onCitiesListSuccess(ArrayList<CityForList> citiesList){
        this.citiesList = citiesList;
        statusTv.setVisibility(View.GONE);
        findViewById(R.id.cities_list).setVisibility(View.VISIBLE);
        adapter.updateDataSource(citiesList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCitiesListError(){
        statusTv.setText("Some error occurred, please re-open the app");
        findViewById(R.id.cities_list).setVisibility(View.GONE);
    }
}
