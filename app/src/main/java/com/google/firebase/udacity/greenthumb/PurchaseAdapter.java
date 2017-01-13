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

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;
import com.google.firebase.udacity.greenthumb.data.PlantCartHelper;

/**
 * {@link PurchaseAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of purchased plant data as its data source,
 * displaying a plant's name and purchased quantity.
 */
public class PurchaseAdapter extends RecyclerView.Adapter {

    private Cursor mCursor;

    static class ViewHolder extends RecyclerView.ViewHolder {

        public int itemId;
        public String itemName;
        public String itemQuantity;
        public TextView mTextViewItemName;
        public TextView mTextViewQuantity;
        public ImageButton mButtonRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewItemName = (TextView) itemView.findViewById(R.id.text_view_item_name);
            mTextViewQuantity = (TextView) itemView.findViewById(R.id.text_view_item_quantity);
            mButtonRemove = (ImageButton) itemView.findViewById(R.id.imagebutton_remove_purchase);

            mButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlantCartHelper.removePurchase(v.getContext(), itemId);
                }
            });
        }
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_purchase, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mCursor == null) {
            return;
        }
        mCursor.moveToPosition(position);

        ViewHolder view = (ViewHolder) holder;
        view.itemId = mCursor.getInt(mCursor.getColumnIndexOrThrow(PlantEntry._ID));
        view.itemName = mCursor.getString(mCursor.getColumnIndexOrThrow(PlantEntry.COLUMN_NAME));
        view.itemQuantity = mCursor.getString(mCursor.getColumnIndexOrThrow(PlantEntry.COLUMN_PURCHASED_QUANTITY));

        view.mTextViewItemName.setText(view.itemName);
        view.mTextViewQuantity.setText(view.itemQuantity);
    }

    @Override
    public int getItemCount() {
        if (mCursor != null)
            return mCursor.getCount();
        return 0;
    }
}
