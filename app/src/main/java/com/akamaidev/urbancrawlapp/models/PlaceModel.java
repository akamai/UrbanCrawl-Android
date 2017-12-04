package com.akamaidev.urbancrawlapp.models;

import android.content.Context;

import com.akamaidev.urbancrawlapp.MAPSDKWrappers.AkaOkHttpInterceptor;
import com.akamaidev.urbancrawlapp.MAPSDKWrappers.OkHttpStack;
import com.akamaidev.urbancrawlapp.helpers.Constants;
import com.akamaidev.urbancrawlapp.helpers.Helper;
import com.akamaidev.urbancrawlapp.helpers.LogEvent;
import com.akamaidev.urbancrawlapp.helpers.Logs;
import com.akamaidev.urbancrawlapp.jsonobjs.Place;
import com.akamaidev.urbancrawlapp.jsonobjs.PlaceDetails;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;

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

public class PlaceModel {

    public interface PlaceModelCallback{
        public void onSuccess(Place place);
        public void onError(VolleyError t);
    }

    public void getPlaceById(final Context ctx, final int placeId, final PlaceModelCallback callback){

        final long startTime = System.currentTimeMillis();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new AkaOkHttpInterceptor()).build();

        RequestQueue requestQueue = Volley.newRequestQueue(ctx, new OkHttpStack(client));
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.GET, Constants.GET_PLACE_DETAILS + "?id=" + placeId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                PlaceDetails place = gson.fromJson(response, PlaceDetails.class);
                if (null != place) {
                    Helper.writeAndPublishLogs(ctx, new LogEvent("Loaded ",(Constants.GET_PLACE_DETAILS + "?id=" + placeId), (System.currentTimeMillis() - startTime)));
                    callback.onSuccess(place.getPlaceDetails());
                } else {
                    Helper.writeAndPublishLogs(ctx, new LogEvent("Failed to load ",(Constants.GET_PLACE_DETAILS + "?id=" + placeId), 0));
                    callback.onError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Helper.writeAndPublishLogs(ctx, new LogEvent("Failed to load ",(Constants.GET_PLACE_DETAILS + "?id=" + placeId), 0));
                Logs.logException(error);
                callback.onError(error);
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}
