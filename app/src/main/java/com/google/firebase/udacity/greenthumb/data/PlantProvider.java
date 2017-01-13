package com.google.firebase.udacity.greenthumb.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;

/**
 * {@link PlantProvider} is the content provider for the plant database.
 */
public class PlantProvider extends ContentProvider {

    private static final String TAG = "PlantProvider";

    private DbHelper mDbHelper;

    public static final int MATCH_CODE_PLANT = 100;
    public static final int MATCH_CODE_PLANT_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, DbContract.PATH_PLANT, MATCH_CODE_PLANT);
        matcher.addURI(authority, DbContract.PATH_PLANT + "/#", MATCH_CODE_PLANT_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case MATCH_CODE_PLANT:
                cursor = mDbHelper.getReadableDatabase().query(
                        PlantEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null /* groupBy */,
                        null /* having */,
                        sortOrder);
                break;
            case MATCH_CODE_PLANT_ID:
                String plantId = PlantEntry.getIdFromUri(uri);
                cursor = mDbHelper.getReadableDatabase().query(
                        PlantEntry.TABLE_NAME,
                        projection,
                        PlantEntry._ID + " = " + plantId,
                        selectionArgs,
                        null /* groupBy */,
                        null /* having */,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH_CODE_PLANT:
                return PlantEntry.CONTENT_LIST_TYPE;
            case MATCH_CODE_PLANT_ID:
                return PlantEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        long id;
        switch (match) {
            case MATCH_CODE_PLANT:
                id = mDbHelper.getWritableDatabase()
                        .insert(PlantEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Insert is not supported for " + uri);
        }
        if (id == -1) {
            Log.e(TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH_CODE_PLANT:
                rowsDeleted = database.delete(
                        PlantEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] whereArgs) {
        final SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MATCH_CODE_PLANT:
                rowsUpdated = database.update(
                        PlantEntry.TABLE_NAME,
                        values,
                        where,
                        whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Updating is not supported for " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
