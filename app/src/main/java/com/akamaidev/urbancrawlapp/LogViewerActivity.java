package com.akamaidev.urbancrawlapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.akamaidev.urbancrawlapp.helpers.Analytics;
import com.akamaidev.urbancrawlapp.helpers.Helper;

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

public class LogViewerActivity extends AppCompatActivity {

    String logStr = "Cant access the log file";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ((TextView)findViewById(R.id.log_tv)).setText(logStr);

        ((TextView)findViewById(R.id.log_tv)).setText("Loading logs..");

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params){
                Helper helper = new Helper();
                logStr = helper.readAllLogsFromFile(LogViewerActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void v){
                if(logStr.length() > 0) {
                    ((TextView) findViewById(R.id.log_tv)).setText(logStr);
                }else{
                    ((TextView) findViewById(R.id.log_tv)).setText("Couldn't load logs, maybe the file couldn't be created");
                }
            }
        }.execute();

        Analytics.logEvent(this, "Log Viewer Activity Opened");
    }

}
