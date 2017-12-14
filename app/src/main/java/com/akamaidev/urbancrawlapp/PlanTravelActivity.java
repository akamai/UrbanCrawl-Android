package com.akamaidev.urbancrawlapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.akamaidev.urbancrawlapp.helpers.Analytics;
import com.akamaidev.urbancrawlapp.helpers.DatePickerFragment;
import com.akamaidev.urbancrawlapp.helpers.Helper;
import com.akamaidev.urbancrawlapp.jsonobjs.CityForList;
import com.akamaidev.urbancrawlapp.presenters.PlanTravelPresenter;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

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

public class PlanTravelActivity extends AppCompatActivity implements PlanTravelPresenter.PlanTravelCallback, DatePickerFragment.OnDateSetListener{

    Toolbar toolbar;

    ArrayList<CityForList> citiesList;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> spinnerValues = new ArrayList<String>();
    DateTime startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_travel);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        PlanTravelPresenter planTravelPresenter = new PlanTravelPresenter();

        startDate = DateTime.now().plusDays(3);
        endDate = startDate.plusDays(15);

        ((TextView)findViewById(R.id.about_version_val)).setText(DateUtils.formatDateTime(this, startDate, DateUtils.FORMAT_SHOW_YEAR));
        findViewById(R.id.about_version_val).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = DatePickerFragment.getInstance(PlanTravelActivity.this,
                        startDate.getDayOfMonth(),
                        startDate.getMonthOfYear()-1,
                        startDate.getYear(),
                        DatePickerFragment.VAL_DIALOG_TYPE_START_DATE);

                datePickerFragment.show(getSupportFragmentManager(), "startDatePicker");
            }
        });

        ((TextView)findViewById(R.id.plantravel_enddate_val)).setText(DateUtils.formatDateTime(this, endDate, DateUtils.FORMAT_SHOW_YEAR));
        findViewById(R.id.plantravel_enddate_val).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = DatePickerFragment.getInstance(PlanTravelActivity.this,
                        endDate.getDayOfMonth(),
                        endDate.getMonthOfYear()-1,
                        endDate.getYear(),
                        DatePickerFragment.VAL_DIALOG_TYPE_END_DATE);

                datePickerFragment.show(getSupportFragmentManager(), "endDatePicker");
            }
        });

        spinnerValues.add("...");

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.uc_simple_spinner_item, spinnerValues);
        arrayAdapter.setDropDownViewResource(R.layout.uc_simple_spinner_dropdown_item);
        arrayAdapter.setNotifyOnChange(true);

        ((Spinner)findViewById(R.id.plantravel_city_spinner)).setAdapter(arrayAdapter);

        findViewById(R.id.plantravel_save_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate.getMillis();
                endDate.getMillis();
                int selectedPos = ((Spinner)findViewById(R.id.plantravel_city_spinner)).getSelectedItemPosition();

                if(spinnerValues.size() > 1){
                    PlanTravelPresenter planTravelPresenter = new PlanTravelPresenter();
                    planTravelPresenter.planTravel(spinnerValues.get(selectedPos), PlanTravelActivity.this);
                    PlanTravelActivity.this.finish();
                }
            }
        });

        planTravelPresenter.getCitiesNames(this, this);

        Analytics.logEvent(this, "Plan Travel Activity Opened");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_plantravel){
            return super.onOptionsItemSelected(item);
        }

        Helper helper = new Helper();
        boolean wasMenuHandled = helper.handleMenuClick(this, toolbar, item);

        if(wasMenuHandled){
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCitiesListError() {

    }

    @Override
    public void onCitiesListSuccess(ArrayList<CityForList> citiesList) {
        this.citiesList = citiesList;

        spinnerValues.clear();
        for (CityForList city : citiesList){
            spinnerValues.add(city.getName());
        }

        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, String dialogType) {
    }
}
