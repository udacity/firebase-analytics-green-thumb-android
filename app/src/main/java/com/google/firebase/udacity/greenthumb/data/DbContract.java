package com.google.firebase.udacity.greenthumb.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * {@link DbContract} is the database schema contract.
 */
public final class DbContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DbContract() {}

    public static final String CONTENT_AUTHORITY = "com.google.firebase.udacity.greenthumb";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PLANT = "plant";

    public static final class PlantEntry implements BaseColumns {

        /** The content URI to access the plant data in the provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PLANT)
                .build();
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of plants.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLANT;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single plant.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLANT;

        /** Name of plant database table. */
        public final static String TABLE_NAME = "plant";

        /**
         * Columns of plant database table.
         * _id is implied as a subclass of BaseColumns.
         */
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_CART_QUANTITY = "cart_quantity";
        public static final String COLUMN_PURCHASED_QUANTITY = "purchased_quantity";

        public static String getIdFromUri(Uri uri) {
            return uri.getLastPathSegment();
        }

        public static Uri createPlantUriWithId(int itemId) {
            return ContentUris.withAppendedId(CONTENT_URI, itemId);
        }
    }
}