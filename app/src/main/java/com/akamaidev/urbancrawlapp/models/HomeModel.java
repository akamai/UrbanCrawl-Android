package com.akamaidev.urbancrawlapp.models;

import android.content.Context;

import com.akamaidev.urbancrawlapp.helpers.Constants;
import com.akamaidev.urbancrawlapp.helpers.DefaultNetworkingClient;
import com.akamaidev.urbancrawlapp.jsonobjs.Cities;
import com.akamaidev.urbancrawlapp.jsonobjs.CityForList;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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

public class HomeModel {

    public ArrayList<CityForList> getAllCities(Context ctx){
        DefaultNetworkingClient defaultNetworkingClient = new DefaultNetworkingClient();

        String jsonStr = defaultNetworkingClient.makeBlockingGetCall(ctx, Constants.GET_ALL_CITIES, Constants.METHOD_GET);
        if(jsonStr != null) {
            try{
                Gson gson = new Gson();
                Cities cities = gson.fromJson(jsonStr, Cities.class);
                return cities.getCities();
            }catch (JsonSyntaxException gsonSyntaxException){
                gsonSyntaxException.printStackTrace();
                return null;
            }
        }else{
            return null;
        }
    }

}
