package com.akamaidev.urbancrawlapp.presenters;

import android.content.Context;

import com.akamaidev.urbancrawlapp.helpers.Logs;
import com.akamaidev.urbancrawlapp.jsonobjs.City;
import com.akamaidev.urbancrawlapp.jsonobjs.CityForHome;
import com.akamaidev.urbancrawlapp.models.CityDetailsModel;

import retrofit.RetrofitError;

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

public class CityDetailsPresenter {

    public interface CityDetailsCallback{
        public void onCityDetailsSuccess(City cityForHome);
        public void onCityDetailsError();
    }

    public void getCityDetails(final int cityId, final Context ctx, final CityDetailsCallback callback){

        CityDetailsModel cityDetailsModel = new CityDetailsModel();
        cityDetailsModel.getCityById(ctx, cityId, new CityDetailsModel.CityDetailsModelCallback() {
            @Override
            public void onSuccess(CityForHome cityForHome) {
                callback.onCityDetailsSuccess(cityForHome.getCityDetails());
            }

            @Override
            public void onError(RetrofitError t) {
                if(t != null) {
                    Logs.logException(t);
                }
                callback.onCityDetailsError();
            }
        });
    }


}
