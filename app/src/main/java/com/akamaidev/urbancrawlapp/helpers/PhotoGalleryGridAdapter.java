package com.akamaidev.urbancrawlapp.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.akamaidev.urbancrawlapp.R;
import com.akamaidev.urbancrawlapp.jsonobjs.MediaObj;

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


public class PhotoGalleryGridAdapter extends RecyclerView.Adapter<PhotoGalleryGridAdapter.ViewHolder> {

    ArrayList<MediaObj> dataSource;

    Context ctx;

    OnRowClickedListener onRowClickedListener;

    public interface OnRowClickedListener{
        public void onRowClicked(int adapterPosition);
    }

    public PhotoGalleryGridAdapter(Context ctx, ArrayList<MediaObj> dataSource, OnRowClickedListener onRowClickedListener) {
        this.ctx = ctx;
        this.dataSource = dataSource;
        this.onRowClickedListener = onRowClickedListener;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.griditem_media, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageLoader.loadImage(ctx, dataSource.get(position).getUrl(), holder.mediaThumb);
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView mediaThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            mediaThumb = (ImageView)itemView.findViewById(R.id.griditem_media_thumb);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRowClickedListener.onRowClicked(getAdapterPosition());
                }
            });
        }
    }
}
