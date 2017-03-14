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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;
import com.google.firebase.udacity.greenthumb.data.Plant;
import com.google.firebase.udacity.greenthumb.data.PlantCartHelper;

/**
 * {@link PlantDetailActivity} displays a plant's name and description
 * and allows the user to check out the plant to the shopping cart.
 */
public class PlantDetailActivity extends AppCompatActivity
        implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "PlantDetailActivity";

    private static final String INTENT_EXTRA_ITEM = "item_id";
    private static final int PLANT_DETAIL_LOADER = 2;

    private int mItemId;
    private Plant mPlant;

    private Cursor mCursor;

    private Toolbar mToolbar;
    private TextView mItemDescription;
    private TextView mItemPrice;

    private GoogleApiClient mGoogleApiClient;

    public static void startActivity(Context context, int itemPosition) {
        Intent i = new Intent(context, PlantDetailActivity.class);
        i.putExtra(INTENT_EXTRA_ITEM, itemPosition);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mItemDescription = (TextView) findViewById(R.id.text_view_item_description);
        mItemPrice = (TextView) findViewById(R.id.text_view_item_price);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        fab.setOnClickListener(this);

        mItemId = getIntent().getIntExtra(INTENT_EXTRA_ITEM, 0);

        getSupportLoaderManager().initLoader(PLANT_DETAIL_LOADER, null, this);

        // Build GoogleApiClient with AppInvite API for receiving deep links
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();

        handleDynamicLink();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (mPlant == null)
                    return;

                int quantity = 1;
                PlantCartHelper.addCartQuantity(this, mPlant.id, quantity);
                Snackbar.make(v, R.string.shopping_cart_item_added, Snackbar.LENGTH_SHORT).show();

                Analytics.logEventAddToCart(this, mPlant, quantity);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PlantEntry._ID,
                PlantEntry.COLUMN_NAME,
                PlantEntry.COLUMN_DESCRIPTION,
                PlantEntry.COLUMN_PRICE
        };
        String selection = PlantEntry._ID + " = " + mItemId;
        return new CursorLoader(this,
                PlantEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        mCursor.moveToFirst();

        mPlant = new Plant(data);
        mToolbar.setTitle(mPlant.name);
        mItemDescription.setText(mPlant.description);
        mItemPrice.setText(getString(R.string.plant_credits, mPlant.price));

        Analytics.logEventViewItem(this, mPlant);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "GoogleApiClient connection failed: " + connectionResult.getErrorMessage());
    }

    private void handleDynamicLink() {
        // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
        // would automatically launch the deep link if one is found.
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);

                                    // Handle the deep link. For example, open the linked
                                    // content, or apply promotional credit to the user's
                                    // account.
                                    Uri uri = Uri.parse(deepLink);
                                    String plantId = uri.getLastPathSegment();
                                    mItemId = Integer.parseInt(plantId);
                                    getSupportLoaderManager().restartLoader(
                                            PLANT_DETAIL_LOADER, null, PlantDetailActivity.this);

                                } else {
                                    Log.d(TAG, "getInvitation: no deep link found.");
                                }
                            }
                        });
    }
}
