package com.akamaidev.urbancrawlapp.presenters;

import android.content.Context;
import android.os.AsyncTask;

import com.akamai.android.sdk.VocService;
import com.akamaidev.urbancrawlapp.jsonobjs.CityForList;
import com.akamaidev.urbancrawlapp.models.HomeModel;

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

public class PlanTravelPresenter {

    public interface PlanTravelCallback{
        public void onCitiesListError();
        public void onCitiesListSuccess(ArrayList<CityForList> citiesList);
    }

    public void getCitiesNames(final Context ctx, final PlanTravelCallback callback){


        new AsyncTask<Void, Void, Integer>(){

            ArrayList<CityForList> citiesList = null;

            @Override
            public Integer doInBackground(Void... v){

                HomeModel homeModel = new HomeModel();

                citiesList = homeModel.getAllCities(ctx);

                return (null != citiesList ? 1 : -1);
            }

            @Override
            protected void onPostExecute(Integer result){
                super.onPostExecute(result);
                if(result == -1){
                    callback.onCitiesListError();
                }else{
                    callback.onCitiesListSuccess(citiesList);
                }
            }

        }.execute();
    }

    public void planTravel(String cityName, Context ctx){

        // MAP SDK Usage : Updating Segment Subscription
        VocService vocService = VocService.createVocService(ctx);
        vocService.updateSegmentSubscription(new String[]{cityName});
    }

}
