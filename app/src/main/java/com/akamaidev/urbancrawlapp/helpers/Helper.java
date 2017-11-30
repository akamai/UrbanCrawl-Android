package com.akamaidev.urbancrawlapp.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.akamaidev.urbancrawlapp.LogViewerActivity;
import com.akamaidev.urbancrawlapp.PlanTravelActivity;
import com.akamaidev.urbancrawlapp.R;
import com.akamaidev.urbancrawlapp.AboutActivity;
import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.List;

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

public class Helper {

    static String logFilename = "UC_Logs.txt";

    public Reader getReaderForRawFile(Context context, int rawFileId){
        InputStream is = context.getResources().openRawResource(rawFileId);
        Reader rd = new BufferedReader(new InputStreamReader(is));
        return rd;
    }

    public boolean handleMenuClick(Context ctx, View view, MenuItem item){
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);

        switch (item.getItemId()){
            case R.id.menu_plantravel:
                snackbar.show();
                Intent planTravelActivity = new Intent(ctx, PlanTravelActivity.class);
                ctx.startActivity(planTravelActivity);
                return true;
            case R.id.menu_settings:
                snackbar.show();
                Intent settingsActivity = new Intent(ctx, AboutActivity.class);
                ctx.startActivity(settingsActivity);
                return true;
            default:
                return false;
        }
    }

    /**
     * Acquires a coarse location from any proivder, and if the location is found, calculates
     * the distance between the current coarse location and the city's location.
     * If the distance can't be found, returns -1, denoting that the distance couldn't be calculated.
     *
     * Suppressing "MissingPermission" because the control will reach here only if the permission are
     * granted, and checks for this have already been passed.
     *
     * The distance returned is in kilometers
     *
     * @param ctx Context to request Location service from the system
     * @param lat Latitude of the city
     * @param lng Longitude of the city
     * @return Distance of the city from the current coarse location, in kilometers
     */
    @SuppressLint("MissingPermission")
    public float calculateDistance(Context ctx, double lat, double lng){
        Location cityLocation = new Location("");
        cityLocation.setLatitude(lat);
        cityLocation.setLongitude(lng);

        LocationManager locationManager = (LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);

        Location coarseLocation = null;

        for (int i = providers.size()-1 ; i >= 0; i--){
            coarseLocation = locationManager.getLastKnownLocation(providers.get(i));
            if(coarseLocation != null) break;
        }

        float distanceResult = -1;

        if(coarseLocation != null) {
            distanceResult = cityLocation.distanceTo(coarseLocation);
        }

        return (distanceResult/1000);
    }

    public void setDevMode(Context ctx, boolean turnOn){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(Constants.PREF_KEY_DEV_MODE, turnOn).commit();
    }

    public boolean getDevMode(Context ctx){
        return ctx.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE).getBoolean(Constants.PREF_KEY_DEV_MODE, false);
    }

    public void setupNavDrawer(final Context ctx, final DrawerLayout drawerLayout, ListView listView, final View logOverlay, final ScrollView logScroller){

        String[] drawerRos = new String[]{"About Urban Crawl","Toggle Developer Mode", "Open Logs"};
        listView.setAdapter(new ArrayAdapter<String>(ctx, R.layout.row_drawer_list, drawerRos));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        Intent aboutActivity = new Intent(ctx, AboutActivity.class);
                        ctx.startActivity(aboutActivity);
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                        break;
                    case 1:
                        if(getDevMode(ctx)){
                            //the dev mode was on, turn it off
                            setDevMode(ctx, false);
                            logOverlay.setVisibility(View.GONE);
                        }else{
                            setDevMode(ctx, true);
                            logOverlay.setVisibility(View.VISIBLE);
                            logScroller.post(new Runnable() {
                                @Override
                                public void run() {
                                    logScroller.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                        }
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                        break;
                    case 2:
                        Intent logViewer = new Intent(ctx, LogViewerActivity.class);
                        ctx.startActivity(logViewer);
                        drawerLayout.closeDrawer(Gravity.RIGHT);
                        break;
                }
            }
        });
    }

    public static void writeAndPublishLogs(Context ctx, LogEvent logEvent){
        String logStr = logEvent.getStatus()+" "+logEvent.getUrl()+" in "+logEvent.getTimeTakenInMillis()+" milliseconds";
        writeToFile(ctx, logStr);
        EventBus.getDefault().post(logEvent);
    }

    static void writeToFile(Context ctx, String strToWrite){

        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        if(!path.exists()){
            path.mkdirs();
        }
        File file = new File(path, "/" + logFilename);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(strToWrite+"\n");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    public String readAllLogsFromFile(Context context) {

        String ret = "";

        try {
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path, "/" + logFilename);

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ( (receiveString = bufferedReader.readLine()) != null ) {
                stringBuilder.append(receiveString);
            }

            bufferedReader.close();
            ret = stringBuilder.toString();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        return ret;
    }
}
