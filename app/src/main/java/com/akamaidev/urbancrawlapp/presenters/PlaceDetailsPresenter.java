package com.akamaidev.urbancrawlapp.presenters;

import android.content.Context;

import com.akamaidev.urbancrawlapp.jsonobjs.Place;
import com.akamaidev.urbancrawlapp.models.PlaceModel;
import com.android.volley.VolleyError;

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

public class PlaceDetailsPresenter {

    public interface PlaceDetailsCallback{
        public void onPlaceDetailsSuccess(Place place);
        public void onPlaceDetailsError();
    }

    public void getPlaceDetails(final int placeId, final Context ctx, final PlaceDetailsCallback callback){
        PlaceModel placeModel = new PlaceModel();
        placeModel.getPlaceById(ctx, placeId, new PlaceModel.PlaceModelCallback() {
            @Override
            public void onSuccess(Place place) {
                callback.onPlaceDetailsSuccess(place);
            }

            @Override
            public void onError(VolleyError t) {
                callback.onPlaceDetailsError();
            }
        });
    }


}
