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
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;
import com.google.firebase.udacity.greenthumb.data.PlantCartHelper;

/**
 * {@link ShoppingCartAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of plant data as its data source,
 * displaying a plant's name and shopping cart quantity.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter {

    private Cursor mCursor;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public int itemId;
        public TextView mTextViewItemName;
        public TextView mTextViewItemPrice;
        public TextView mTextViewItemQuantity;
        public TextView mTextViewItemTotalPrice;
        public Button mButtonSubtract;
        public Button mButtonAdd;
        public ImageButton mButtonRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextViewItemName = (TextView) itemView.findViewById(R.id.text_view_item_name);
            mTextViewItemPrice = (TextView) itemView.findViewById(R.id.text_view_item_price);
            mTextViewItemQuantity = (TextView) itemView.findViewById(R.id.text_view_item_quantity);
            mTextViewItemTotalPrice = (TextView) itemView.findViewById(R.id.text_view_total_price);
            mButtonSubtract = (Button) itemView.findViewById(R.id.button_quantity_subtract);
            mButtonAdd = (Button) itemView.findViewById(R.id.button_quantity_add);
            mButtonRemove = (ImageButton) itemView.findViewById(R.id.imagebutton_remove_cart);

            mButtonSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlantCartHelper.subtractCartQuantity(v.getContext(), itemId, 1);
                    updateUi();
                }
            });

            mButtonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlantCartHelper.addCartQuantity(v.getContext(), itemId, 1);
                    updateUi();
                }
            });

            mButtonRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Save quantity for undo
                    final int previousQuantity = Integer.parseInt(mTextViewItemQuantity.getText().toString());

                    PlantCartHelper.removeFromCart(v.getContext(), itemId);
                    notifyItemRemoved(getAdapterPosition());

                    Snackbar.make(v, String.format("'%s' removed from cart", mTextViewItemName.getText()), Snackbar.LENGTH_SHORT)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PlantCartHelper.addCartQuantity(v.getContext(), itemId, previousQuantity);
                                }
                            })
                            .show();
                }
            });
        }

        /**
         * Updates the UI after first view binding after each user interaction.
         */
        private void updateUi() {
            // Disable the subtraction button if there's only one quantity for the item.
            // Enable otherwise.
            if (1 == Integer.parseInt(mTextViewItemQuantity.getText().toString())) {
                mButtonSubtract.setEnabled(false);
            } else {
                mButtonSubtract.setEnabled(true);
            }
        }
    }

    public void swapCursor(Cursor cursor) {
        this.mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_shopping_cart, parent, false);
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
        int  price = mCursor.getInt(mCursor.getColumnIndexOrThrow(PlantEntry.COLUMN_PRICE));
        int quantity = mCursor.getInt(mCursor.getColumnIndexOrThrow(PlantEntry.COLUMN_CART_QUANTITY));
        int totalPrice = price * quantity;

        ViewHolder view = (ViewHolder) holder;
        Resources resources = view.itemView.getContext().getResources();
        String itemPrice = resources.getQuantityString(R.plurals.number_of_credits, price, price);
        String totalPriceString = resources.getQuantityString(R.plurals.number_of_credits, totalPrice, totalPrice);
        view.itemId = mCursor.getInt(id);
        view.mTextViewItemName.setText(name);
        view.mTextViewItemPrice.setText(itemPrice);
        view.mTextViewItemQuantity.setText(String.valueOf(quantity));
        view.mTextViewItemTotalPrice.setText(totalPriceString);
        view.updateUi();
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }
}
