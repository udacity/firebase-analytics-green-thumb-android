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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;
import com.google.firebase.udacity.greenthumb.data.PlantCartHelper;

import java.util.concurrent.TimeUnit;

/**
 * {@link ShoppingCartActivity} } displays a list of plants
 * in the shopping cart that can be checked out.
 */
public class ShoppingCartActivity extends AppCompatActivity
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int SHOPPING_CART_LOADER = 3;

    private RecyclerView mRecyclerView;
    private TextView mTextViewEmptyCart;
    private ShoppingCartAdapter mCartAdapter;
    private TextView mTextViewTotalPrice;
    private Button mButtonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.shopping_cart_title);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTextViewEmptyCart = (TextView) findViewById(R.id.text_view_empty_cart);
        mTextViewTotalPrice = (TextView) findViewById(R.id.text_view_total_price);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(), layoutManager.getOrientation());
        mCartAdapter = new ShoppingCartAdapter();

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mRecyclerView.setAdapter(mCartAdapter);

        mButtonCheckout = (Button) findViewById(R.id.button_cart_checkout);
        mButtonCheckout.setOnClickListener(this);

        getSupportLoaderManager().initLoader(SHOPPING_CART_LOADER, null, this);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ShoppingCartActivity.class);
        context.startActivity(intent);
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.button_cart_checkout:
                Snackbar.make(v, R.string.checkout_progress, Snackbar.LENGTH_LONG).show();
                final long DELAY = TimeUnit.SECONDS.toMillis(3);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PlantCartHelper.checkoutCart(ShoppingCartActivity.this);
                        Snackbar.make(v, R.string.checkout_complete, Snackbar.LENGTH_SHORT).show();
                    }
                }, DELAY);
                break;
        }
    }

    /**
     * Returns the total price of the shopping cart
     * @param cursor The cursor containing the items in the cart
     * @return The total price of the cart
     */
    private int calculateTotal(Cursor cursor) {
        if (cursor == null) {
            return 0;
        }
        int total = 0;
        while (cursor.moveToNext()) {
            int quantity = cursor.getInt(
                    cursor.getColumnIndexOrThrow(PlantEntry.COLUMN_CART_QUANTITY));
            int price = cursor.getInt(
                    cursor.getColumnIndexOrThrow(PlantEntry.COLUMN_PRICE));
            total = total + (quantity * price);
        }
        return total;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PlantEntry._ID,
                PlantEntry.COLUMN_NAME,
                PlantEntry.COLUMN_PRICE,
                PlantEntry.COLUMN_CART_QUANTITY
        };
        String selection = PlantEntry.COLUMN_CART_QUANTITY + " > 0";
        return new CursorLoader(this,
                PlantEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCartAdapter.swapCursor(data);

        int mTotalPrice = calculateTotal(data);
        mTextViewTotalPrice.setText(getString(R.string.shopping_cart_total, mTotalPrice));

        boolean emptyCart = data.getCount() == 0;
        mTextViewEmptyCart.setVisibility(emptyCart ? View.VISIBLE : View.GONE);
        mButtonCheckout.setEnabled(!emptyCart);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCartAdapter.swapCursor(null);
    }
}
