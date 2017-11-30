package com.akamaidev.urbancrawlapp.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akamaidev.urbancrawlapp.R;
import com.akamaidev.urbancrawlapp.jsonobjs.Place;

import java.util.List;

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

public class CityPlacesListAdapter extends RecyclerView.Adapter<CityPlacesListAdapter.ViewHolder> {

    List<Place> dataSource;
    String cityName;

    Context ctx;

    OnRowClickedListener onRowClickedListener;

    public interface OnRowClickedListener{
        public void onRowClicked(int adapterPosition);
    }

    public CityPlacesListAdapter(Context ctx, List<Place> dataSource, String cityName, OnRowClickedListener onRowClickedListener) {
        this.ctx = ctx;
        this.dataSource = dataSource;
        this.cityName = cityName;
        this.onRowClickedListener = onRowClickedListener;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_city_places, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.placeName.setText(dataSource.get(position).getName());
        holder.cityName.setText(cityName);
        ImageLoader.loadImage(ctx, dataSource.get(position).getHeroimage(), holder.cityThumb);
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
        TextView placeName;
        TextView cityName;
        ImageView cityThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            placeName = (TextView)itemView.findViewById(R.id.row_city_place_name);
            cityName = (TextView)itemView.findViewById(R.id.row_city_name);
            cityThumb = (ImageView)itemView.findViewById(R.id.row_city_place_thumb);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRowClickedListener.onRowClicked(getAdapterPosition());
                }
            });
        }
    }
}
