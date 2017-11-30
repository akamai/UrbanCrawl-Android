package com.akamaidev.urbancrawlapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akamaidev.urbancrawlapp.helpers.Analytics;
import com.akamaidev.urbancrawlapp.helpers.Helper;
import com.akamaidev.urbancrawlapp.helpers.ImageLoader;
import com.akamaidev.urbancrawlapp.helpers.PhotoGalleryGridAdapter;
import com.akamaidev.urbancrawlapp.jsonobjs.MediaObj;
import com.akamaidev.urbancrawlapp.presenters.PhotoGalleryPresenter;

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

public class PhotoGalleryActivity extends AppCompatActivity implements PhotoGalleryPresenter.PhotoGalleryCallback, PhotoGalleryGridAdapter.OnRowClickedListener {

    Toolbar toolbar;
    RecyclerView photoGrid;

    int placeId;
    String placeName, placeCity, placeThumb;

    ArrayList<MediaObj> mediaList;

    public static final String KEY_PLACE_ID = "KEY_PLACE_ID";
    public static final String KEY_PLACE_NAME = "KEY_PLACE_NAME";
    public static final String KEY_PLACE_CITY = "KEY_PLACE_CITY";
    public static final String KEY_PLACE_THUMB = "KEY_PLACE_THUMB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(getIntent().getExtras() != null){
            placeId = getIntent().getExtras().getInt(KEY_PLACE_ID);
            placeName = getIntent().getExtras().getString(KEY_PLACE_NAME);
            placeCity = getIntent().getExtras().getString(KEY_PLACE_CITY);
            placeThumb = getIntent().getExtras().getString(KEY_PLACE_THUMB);

        }else{
            finish();
            return;
        }

        ((TextView)findViewById(R.id.media_place_name)).setText(placeName);
        ((TextView)findViewById(R.id.media_place_cityname)).setText(placeCity);
        ImageLoader.loadImage(this, placeThumb, ((ImageView)findViewById(R.id.media_thumbnail)));

        photoGrid = (RecyclerView)findViewById(R.id.photogrid_recyclerview);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        photoGrid.setLayoutManager(layoutManager);

        PhotoGalleryPresenter photoGalleryPresenter = new PhotoGalleryPresenter();
        photoGalleryPresenter.getMediaByPlaceId(placeId, this, this);

        findViewById(R.id.photogrid_recyclerview).setVisibility(View.INVISIBLE);
        findViewById(R.id.photogrid_progressbar).setVisibility(View.VISIBLE);

        Analytics.logEvent(this, "Photo Gallery Activity Opened");

    }

    @Override
    public void onPhotoGallerySuccess(ArrayList<MediaObj> mediaList) {
        this.mediaList = mediaList;
        PhotoGalleryGridAdapter photoGalleryGridAdapter = new PhotoGalleryGridAdapter(this, mediaList, this);
        photoGrid.setAdapter(photoGalleryGridAdapter);
        findViewById(R.id.photogrid_recyclerview).setVisibility(View.VISIBLE);
        findViewById(R.id.photogrid_progressbar).setVisibility(View.GONE);
    }

    @Override
    public void onPhotoGalleryError() {
        findViewById(R.id.photogrid_progressbar).setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Some Error Occurred and the images couldn't be loaded. Pictures Gallery will now close.");
        builder.setTitle("Couldn't load images");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PhotoGalleryActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.uc_purple));

        Snackbar.make(toolbar, "Some Error occurred", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_plan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Helper helper = new Helper();
        boolean wasMenuHandled = helper.handleMenuClick(this, toolbar, item);

        if(wasMenuHandled){
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRowClicked(int adapterPosition) {
        Intent i = new Intent(PhotoGalleryActivity.this, PhotoViewerActivity.class);
        i.putExtra(PhotoViewerActivity.KEY_IMG_URL, mediaList.get(adapterPosition).getUrl());
        startActivity(i);
    }
}
