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

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;
import com.google.firebase.udacity.greenthumb.data.Preferences;

/**
 * {@link MainActivity} displays a list of plants to buy.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PLANT_LOADER = 1;

    PlantAdapter mAdapter;

    private int mRatingChoice = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Pass in null cursor; Cursor with plant data filled in loader's onLoadFinished
        mAdapter = new PlantAdapter(null);
        recyclerView.setAdapter(mAdapter);

        // Kick off the loader
        getSupportLoaderManager().initLoader(PLANT_LOADER, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Show the gardening experience rating when the app is first opened
        if (Preferences.getFirstLoad(this)) {
            showExperienceDialog();
            Preferences.setFirstLoad(this, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_shopping_cart:
                ShoppingCartActivity.startActivity(this);
                break;
            case R.id.menu_purchases:
                PurchaseActivity.startActivity(this);
                break;
            case R.id.menu_about:
                AboutActivity.startActivity(this);
                break;
            case R.id.menu_experience:
                showExperienceDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows a dialog for the user to rate their gardening experience.
     */
    private void showExperienceDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.gardening_experience_title)
                .setSingleChoiceItems(
                        R.array.gardening_experience_rating_labels,
                        Preferences.getGardeningExperience(this),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mRatingChoice = which;
                            }
                        })
                .setPositiveButton(R.string.button_gardening_experience_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mRatingChoice == -1) {
                            return;
                        }
                        Preferences.setGardeningExperience(MainActivity.this, mRatingChoice);
                    }
                })
                .setNegativeButton(R.string.button_gardening_experience_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                PlantEntry._ID,
                PlantEntry.COLUMN_NAME,
                PlantEntry.COLUMN_DESCRIPTION,
                PlantEntry.COLUMN_PRICE
        };
        return new CursorLoader(this,
                PlantEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
