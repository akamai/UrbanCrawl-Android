package com.akamaidev.urbancrawlapp.presenters;

import android.content.Context;
import android.os.AsyncTask;

import com.akamaidev.urbancrawlapp.jsonobjs.MediaObj;
import com.akamaidev.urbancrawlapp.models.PhotoGalleryModel;

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

public class PhotoGalleryPresenter {

    public interface PhotoGalleryCallback{
        public void onPhotoGallerySuccess(ArrayList<MediaObj> mediaList);
        public void onPhotoGalleryError();
    }

    public void getMediaByPlaceId(final int placeId, final Context ctx, final PhotoGalleryCallback callback){
        new AsyncTask<Void, Void, Integer>(){

            ArrayList<MediaObj> mediaList;

            @Override
            public Integer doInBackground(Void... v){

                PhotoGalleryModel photoGalleryModel = new PhotoGalleryModel();
                mediaList = photoGalleryModel.getMediaByPlaceId(ctx, placeId);

                return (null != mediaList ? 1 : -1);
            }

            @Override
            protected void onPostExecute(Integer result){
                super.onPostExecute(result);
                if(result == -1){
                    callback.onPhotoGalleryError();
                }else{
                    callback.onPhotoGallerySuccess(mediaList);
                }
            }

        }.execute();
    }


}
