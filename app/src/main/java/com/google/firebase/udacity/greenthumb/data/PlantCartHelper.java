package com.google.firebase.udacity.greenthumb.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;

/**
 * Helper class for some PlantProvider operations
 */
public class PlantCartHelper {

    /**
     * Check out all items in the shopping cart and save them to the purchase history
     * @param context the context to use for the content resolver
     */
    public static void checkoutCart(Context context) {
        /*
           Get plants in the shopping cart
         */
        String[] projection = {
                PlantEntry._ID,
                PlantEntry.COLUMN_NAME,
                PlantEntry.COLUMN_PRICE,
                PlantEntry.COLUMN_CART_QUANTITY
        };
        String selection = PlantEntry.COLUMN_CART_QUANTITY + " > 0";
        Cursor query = context.getContentResolver().query(
                PlantEntry.CONTENT_URI,
                projection,
                selection,
                null,
                null);
        if (query == null) {
            return;
        }

        /*
           Remove plants from the shopping cart and add each of them to the purchased list
         */
        while (query.moveToNext()) {
            int cartQuantity = query.getInt(query.getColumnIndexOrThrow(PlantEntry.COLUMN_CART_QUANTITY));
            ContentValues purchaseValues = new ContentValues();
            purchaseValues.put(PlantEntry.COLUMN_CART_QUANTITY, 0);
            purchaseValues.put(PlantEntry.COLUMN_PURCHASED_QUANTITY, cartQuantity);
            String where = PlantEntry._ID + "= ?";
            String[] selectionArgs = {query.getString(query.getColumnIndexOrThrow(PlantEntry._ID))};
            context.getContentResolver().update(PlantEntry.CONTENT_URI, purchaseValues, where, selectionArgs);
        }

        query.close();
    }


    /**
     * Remove all quantities from the cart for a specific item
     * @param context the context to use for the content resolver
     * @param itemId the specific item id to remove from the cart
     */
    public static void removeFromCart(Context context, int itemId) {
        ContentValues values = new ContentValues();
        values.put(PlantEntry.COLUMN_CART_QUANTITY, 0);
        context.getContentResolver().update(
                PlantEntry.CONTENT_URI,
                values,
                PlantEntry._ID + " = " + itemId,
                null);
    }

    /**
     * Subtract a specified quantity of items from the cart
     * @param context the context to use for the content resolver
     * @param itemId the specific item id to subtract from the cart
     * @param subtractQuantity the number of items to subtract
     */
    public static void subtractCartQuantity(Context context, int itemId, int subtractQuantity) {
        int quantity = getCartQuantity(context, itemId);
        ContentValues values = new ContentValues();
        values.put(PlantEntry.COLUMN_CART_QUANTITY, Math.max(1, quantity - subtractQuantity));
        context.getContentResolver().update(
                PlantEntry.CONTENT_URI,
                values,
                PlantEntry._ID + " = " + itemId,
                null);
    }

    /**
     * Adds a specified quantity of items to the cart
     * @param context the context to use for the content resolver
     * @param itemId the specific item id to add to the cart
     * @param addQuantity the number of items to add
     */
    public static void addCartQuantity(Context context, int itemId, int addQuantity) {
        int quantity = getCartQuantity(context, itemId);
        ContentValues values = new ContentValues();
        values.put(PlantEntry.COLUMN_CART_QUANTITY, quantity + addQuantity);
        context.getContentResolver().update(
                PlantEntry.CONTENT_URI,
                values,
                PlantEntry._ID + " = " + itemId,
                null);
    }

    /**
     * Removes an item from the list of purchases
     * @param context the context to use for the content resolver
     * @param itemId the specific item to remove from the purchase list
     */
    public static void removePurchase(Context context, int itemId) {
        ContentValues values = new ContentValues();
        values.put(PlantEntry.COLUMN_PURCHASED_QUANTITY, 0);
        context.getContentResolver().update(
                PlantEntry.CONTENT_URI,
                values,
                PlantEntry._ID + " = " + itemId,
                null);
    }

    /**
     * Returns the number of items in the cart for a specific item
     * @param context the context to use for the content resolver
     * @param itemId the specific item id
     * @return the cart quantity for the item with id itemId
     */
    private static int getCartQuantity(Context context, int itemId) {
        String[] projection = {
                PlantEntry.COLUMN_CART_QUANTITY
        };
        Cursor cursor = context.getContentResolver().query(
                PlantEntry.createPlantUriWithId(itemId),
                projection,
                null,
                null,
                null);

        if (cursor == null) {
            throw new IllegalArgumentException(itemId + " not found");
        }

        cursor.moveToFirst();
        int cartQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(PlantEntry.COLUMN_CART_QUANTITY));
        cursor.close();
        return cartQuantity;
    }

}
