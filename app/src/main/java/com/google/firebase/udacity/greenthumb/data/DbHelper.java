package com.google.firebase.udacity.greenthumb.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.firebase.udacity.greenthumb.R;
import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;

/**
 * {@link DbHelper} is a helper to create the SQLite database and insert initial plant data.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "greenthumb.db";
    private static final int DATABASE_VERSION = 1;

    private Context context;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a String that contains the SQL statement to create the database tables
        String SQL_CREATE_PLANT_TABLE = "CREATE TABLE " + PlantEntry.TABLE_NAME + " ("
                + PlantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PlantEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + PlantEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + PlantEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + PlantEntry.COLUMN_CART_QUANTITY + " INTEGER NOT NULL, "
                + PlantEntry.COLUMN_PURCHASED_QUANTITY + " INTEGER NOT NULL);";
        sqLiteDatabase.execSQL(SQL_CREATE_PLANT_TABLE);
    }

    /**
     * Add initial plant data when the app launches for the first time.
     * @param db The database to insert data
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Only update the database if it's writable
        if (db.isReadOnly()) {
            return;
        }
        // Only insert initial plant data if the plant database is empty
        Cursor cursor = db.query(PlantEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            return;
        }
        cursor.close();

        // Plant database is empty. Add all the plants.
        String[] names = context.getResources().getStringArray(R.array.plant_names);
        String[] descriptions = context.getResources().getStringArray(R.array.plant_descriptions);
        int[] prices = context.getResources().getIntArray(R.array.plant_prices);

        if (names.length != descriptions.length
                && descriptions.length != prices.length) {
            throw new IllegalStateException("There should be an equal number of plant names/descriptions/prices");
        }

        for (int i = 0; i < names.length; i++) {
            ContentValues values = new ContentValues();
            values.put(PlantEntry.COLUMN_NAME, names[i]);
            values.put(PlantEntry.COLUMN_DESCRIPTION, descriptions[i]);
            values.put(PlantEntry.COLUMN_PRICE, prices[i]);
            values.put(PlantEntry.COLUMN_CART_QUANTITY, 0);
            values.put(PlantEntry.COLUMN_PURCHASED_QUANTITY, 0);
            db.insert(PlantEntry.TABLE_NAME, null, values);
        }
        context.getContentResolver().notifyChange(PlantEntry.CONTENT_URI, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // No need to implement for first version of database
    }
}
