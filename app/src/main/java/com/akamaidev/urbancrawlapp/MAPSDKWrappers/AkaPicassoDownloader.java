package com.akamaidev.urbancrawlapp.MAPSDKWrappers;

import android.content.Context;
import android.net.Uri;

import com.akamai.android.sdk.VocService;
import com.akamai.android.sdk.net.AkaURLConnection;
import com.akamai.android.sdk.net.AkaURLStreamHandler;
import com.squareup.picasso.Downloader;

import java.io.IOException;
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

/**
 * Created by vaanand on 1/6/16.
 * Usage:
     Context ctx;
     Picasso.Builder picassoBuilder = new Picasso.Builder(ctx);
     picassoBuilder.downloader(new AkaPicassoDownloader(ctx));
     Picasso picasso = picassoBuilder.build();
     Picasso.setSingletonInstance(picasso);
 */
public class AkaPicassoDownloader implements Downloader{

    public AkaPicassoDownloader(Context context) {
        // initialize VocService
        VocService.createVocService(context);
    }
    AkaURLConnection connection;
    @Override
    public Response load(Uri uri, int networkPolicy) throws IOException {
        // Ignoring networkPolicy for now.
        connection =
                (AkaURLConnection) new URL(null, uri.toString(), new AkaURLStreamHandler()).openConnection();
        return new Response(connection.getInputStream(), true, connection.getContentLength());
    }

    @Override
    public void shutdown() {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
