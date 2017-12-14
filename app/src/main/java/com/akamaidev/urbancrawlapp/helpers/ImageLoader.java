package com.akamaidev.urbancrawlapp.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.akamaidev.urbancrawlapp.MAPSDKWrappers.AkaPicassoDownloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

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

// MAP SDK : This class uses "Picasso" and the MAP SDK Picasso Downloader

public class ImageLoader {

    static long timeTaken;

    public static void loadImage(final Context with, final String url, final ImageView into){
        final long startTime = System.currentTimeMillis();

        Picasso picasso = new Picasso.Builder(with)
                .downloader(new AkaPicassoDownloader(with))
                .build();

        picasso.setLoggingEnabled(false);
        picasso.load(url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(into, new Callback() {
            @Override
            public void onSuccess() {
                timeTaken = (System.currentTimeMillis() - startTime);
                Helper.writeAndPublishLogs(with, new LogEvent("Loaded",url, timeTaken));
            }

            @Override
            public void onError() {
                Log.e("Picasso","Failed to load : "+url);
            }
        });
    }
}
