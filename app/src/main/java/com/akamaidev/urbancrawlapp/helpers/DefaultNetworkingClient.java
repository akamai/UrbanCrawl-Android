package com.akamaidev.urbancrawlapp.helpers;

import android.content.Context;

import com.akamai.android.sdk.net.AkaURLConnection;
import com.akamai.android.sdk.net.AkaURLStreamHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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

public class DefaultNetworkingClient {

    public String makeBlockingGetCall(Context ctx, String urlString, String method){
        Analytics.logStartEvent(ctx, "DefaultNetworkingClient, URL: "+urlString);
        long startTime = System.currentTimeMillis();

        // MAP SDK Usage : Using AkaURLConnection
        AkaURLConnection httpURLConnection = null;
        try {
            URL url = new URL(null, urlString, new AkaURLStreamHandler());
            httpURLConnection = (AkaURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(method);
            InputStream is = httpURLConnection.getInputStream();

            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            String line, jsonStr = "";
            while ((line = r.readLine()) != null) {
                jsonStr+=line;
            }

            Helper.writeAndPublishLogs(ctx, new LogEvent("Loaded",urlString, (System.currentTimeMillis() - startTime)));
            return jsonStr;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Helper.writeAndPublishLogs(ctx, new LogEvent("Failed (MalformedUrlException)",urlString, 0));
        } catch (IOException e) {
            e.printStackTrace();
            Helper.writeAndPublishLogs(ctx, new LogEvent("Failed (IOException)",urlString, 0));
        }finally {
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
        Analytics.logStopEvent(ctx, "DefaultNetworkingClient, URL: "+urlString);
        return null;
    }
}
