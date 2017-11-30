package com.akamaidev.urbancrawlapp.models;

import android.content.Context;

import com.akamaidev.urbancrawlapp.helpers.Constants;
import com.akamaidev.urbancrawlapp.helpers.DefaultNetworkingClient;
import com.akamaidev.urbancrawlapp.jsonobjs.MediaList;
import com.akamaidev.urbancrawlapp.jsonobjs.MediaObj;
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

public class PhotoGalleryModel {

    public ArrayList<MediaObj> getMediaByPlaceId(Context ctx, int placeId){
        DefaultNetworkingClient defaultNetworkingClient = new DefaultNetworkingClient();
        String jsonStr = defaultNetworkingClient.makeBlockingGetCall(ctx, Constants.GET_PLACE_MEDIA+"?placeid="+placeId+"&type=image", Constants.METHOD_GET);

        if(jsonStr != null) {
            try {
                Gson gson = new Gson();

                MediaList media = gson.fromJson(jsonStr, MediaList.class);
                if (null != media && media.getError() == null) {
                    return media.getMedia();
                } else {
                    return null;
                }
            }catch (JsonSyntaxException jsonSynExc){
                jsonSynExc.printStackTrace();
                return null;
            }

        }else{
            return null;
        }
    }

}
