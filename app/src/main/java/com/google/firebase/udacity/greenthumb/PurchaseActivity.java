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
import android.widget.TextView;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;

/**
 * {@link PurchaseActivity} displays a list of purchased plants.
 */
public class PurchaseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PURCHASE_LOADER = 4;

    private RecyclerView mRecyclerView;
    private PurchaseAdapter mPurchaseAdapter;
    private TextView mTextViewPurchaseEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        Toolbar toolbar =  (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.purchases_title);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mTextViewPurchaseEmpty = (TextView) findViewById(R.id.text_view_empty_purchase);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        mPurchaseAdapter = new PurchaseAdapter();
        mRecyclerView.setAdapter(mPurchaseAdapter);

        getSupportLoaderManager().initLoader(PURCHASE_LOADER, null, this);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, PurchaseActivity.class);
        context.startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PlantEntry._ID,
                PlantEntry.COLUMN_NAME,
                PlantEntry.COLUMN_PURCHASED_QUANTITY
        };
        String selection = PlantEntry.COLUMN_PURCHASED_QUANTITY + " > 0";
        return new CursorLoader(this,
                PlantEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPurchaseAdapter.swapCursor(data);

        boolean emptyPurchases = data.getCount() == 0;
        mTextViewPurchaseEmpty.setVisibility(emptyPurchases ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPurchaseAdapter.swapCursor(null);
    }
}