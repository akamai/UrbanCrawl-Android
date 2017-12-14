package com.akamaidev.urbancrawlapp;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.akamai.android.sdk.net.webkit.AkaWebViewL15Client;
import com.akamai.android.sdk.net.webkit.AkaWebViewL21Client;
import com.akamaidev.urbancrawlapp.helpers.Analytics;

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

public class WebViewerActivity extends AppCompatActivity {

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_viewer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        String url = "https://developer.akamai.com";

        WebView webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        // MAP SDK Usage : Using WebView Client
        if(Build.VERSION.SDK_INT>=21){
            webView.setWebViewClient(new AkaWebViewL21Client());
        }else{
            webView.setWebViewClient(new AkaWebViewL15Client());
        }

        webView.loadUrl(url);

        Analytics.logEvent(this, "WebViewer Activity Opened");

    }

}
