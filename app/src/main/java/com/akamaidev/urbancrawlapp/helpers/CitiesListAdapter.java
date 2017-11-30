package com.akamaidev.urbancrawlapp.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akamaidev.urbancrawlapp.jsonobjs.CityForList;
import com.akamaidev.urbancrawlapp.R;

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

public class CitiesListAdapter extends RecyclerView.Adapter<CitiesListAdapter.ViewHolder> {
    ArrayList<CityForList> dataSource;
    OnRowClickedListener onRowClickedListener;
    Context ctx;

    public interface OnRowClickedListener{
        public void onRowClicked(int adapterPosition);
    }

    public CitiesListAdapter(ArrayList<CityForList> dataSource, OnRowClickedListener onRowClickedListener, Context ctx) {
        this.dataSource = dataSource;
        this.onRowClickedListener = onRowClickedListener;
        this.ctx = ctx;
        setHasStableIds(true);
    }

    public void updateDataSource(ArrayList<CityForList> updatedData){
        this.dataSource = updatedData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_city_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cityName.setText(dataSource.get(position).getName());
        holder.countryName.setText(dataSource.get(position).getCountryname());
        ImageLoader.loadImage(ctx, dataSource.get(position).getThumburl(), holder.cityThumb);
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
        TextView cityName;
        TextView countryName;
        ImageView cityThumb;

        public ViewHolder(View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.row_cityName_tv);
            countryName = itemView.findViewById(R.id.row_countryName_tv);
            cityThumb = itemView.findViewById(R.id.row_cityThumb);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRowClickedListener.onRowClicked(getAdapterPosition());
                }
            });
        }
    }
}
