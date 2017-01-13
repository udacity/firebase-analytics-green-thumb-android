/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.udacity.greenthumb;

import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;

/**
 * {@link PlantAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of plant data as its data source,
 * displaying a plant's name, description, and price for each row.
 */
public class PlantAdapter extends RecyclerView.Adapter {

    private Cursor mCursor;

    private class ViewHolder extends RecyclerView.ViewHolder {

        public int itemId;
        public TextView mTextViewItemName;
        public TextView mTextViewItemPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewItemName = (TextView) itemView.findViewById(R.id.text_view_item_name);
            mTextViewItemPrice = (TextView) itemView.findViewById(R.id.text_view_item_price);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlantDetailActivity.startActivity(v.getContext(), itemId);
                }
            });
        }
    }

    public PlantAdapter(Cursor cursor) {
        this.mCursor = cursor;
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_plant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);

        int id = mCursor.getColumnIndexOrThrow(PlantEntry._ID);
        String name = mCursor.getString(mCursor.getColumnIndexOrThrow(PlantEntry.COLUMN_NAME));
        int price = mCursor.getInt(mCursor.getColumnIndexOrThrow(PlantEntry.COLUMN_PRICE));

        ViewHolder view = (ViewHolder) holder;
        Resources resources = view.itemView.getContext().getResources();
        String priceString = resources.getQuantityString(R.plurals.number_of_credits, price, price);
        view.itemId = mCursor.getInt(id);
        view.mTextViewItemName.setText(name);
        view.mTextViewItemPrice.setText(priceString);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }
}
