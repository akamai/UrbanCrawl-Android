package com.akamaidev.urbancrawlapp.models;

import android.content.Context;

import com.akamaidev.urbancrawlapp.MAPSDKWrappers.AkaRetrofitClient;
import com.akamaidev.urbancrawlapp.helpers.Analytics;
import com.akamaidev.urbancrawlapp.helpers.CityDetailsService;
import com.akamaidev.urbancrawlapp.helpers.Constants;
import com.akamaidev.urbancrawlapp.helpers.Helper;
import com.akamaidev.urbancrawlapp.helpers.LogEvent;
import com.akamaidev.urbancrawlapp.jsonobjs.CityForHome;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

public class CityDetailsModel {

    public interface CityDetailsModelCallback{
        public void onSuccess(CityForHome cityForHome);
        public void onError(RetrofitError t);
    }

    public void getCityById(final Context ctx, final int cityId, final CityDetailsModelCallback callback){
        final long startTime = System.currentTimeMillis();
        Analytics.logStartEvent(ctx, "Retrofit, CityDetails, ID: "+cityId);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL+Constants.API_SUFFIX)
                .setClient(new AkaRetrofitClient(ctx))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        CityDetailsService cityDetailsService = restAdapter.create(CityDetailsService.class);

        cityDetailsService.getCityDetailsById(cityId, new Callback<CityForHome>() {
            @Override
            public void success(CityForHome cityForHome, Response response) {
                if(response.getStatus() == 200){
                    Analytics.logStopEvent(ctx, "Retrofit, CityDetails, ID: "+cityId);
                    Helper.writeAndPublishLogs(
                            ctx,
                            new LogEvent("Loaded ",(Constants.BASE_URL+Constants.API_SUFFIX+Constants.GET_CITY_DETAILS_RETROFIT+"?id="+cityId), (System.currentTimeMillis() - startTime)));
                    callback.onSuccess(cityForHome);
                }else{
                    Helper.writeAndPublishLogs(
                            ctx,
                            new LogEvent("Failed to load ",(Constants.BASE_URL+Constants.API_SUFFIX+Constants.GET_CITY_DETAILS_RETROFIT+"?id="+cityId), 0));
                    Analytics.logStopEvent(ctx, "Retrofit, CityDetails, ID: "+cityId);
                    callback.onError(null);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Helper.writeAndPublishLogs(
                        ctx,
                        new LogEvent("Failed to load ",(Constants.BASE_URL+Constants.API_SUFFIX+Constants.GET_CITY_DETAILS_RETROFIT+"?id="+cityId), 0));
                Analytics.logStopEvent(ctx, "Retrofit, CityDetails, ID: "+cityId);
                callback.onError(error);
            }
        });
    }
}
