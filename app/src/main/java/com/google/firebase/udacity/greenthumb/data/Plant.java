package com.google.firebase.udacity.greenthumb.data;

import android.database.Cursor;

import com.google.firebase.udacity.greenthumb.data.DbContract.PlantEntry;

/**
 * {@link Plant} POJO data model for each database row
 */
public class Plant {
    public int id;
    public String name;
    public String description;
    public int price;
    public int cartQuantity;
    public int purchasedQuantity;

    public Plant(int id, String name, String description, int price, int cartQuantity, int purchasedQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.cartQuantity = cartQuantity;
        this.purchasedQuantity = purchasedQuantity;
    }

    public Plant(Cursor c) {
        int columnId = c.getColumnIndex(PlantEntry._ID);
        int columnName = c.getColumnIndex(PlantEntry.COLUMN_NAME);
        int columnDescription = c.getColumnIndex(PlantEntry.COLUMN_DESCRIPTION);
        int columnPrice = c.getColumnIndex(PlantEntry.COLUMN_PRICE);
        int columnCartQuantity = c.getColumnIndex(PlantEntry.COLUMN_CART_QUANTITY);
        int columnPurchasedQuantity  = c.getColumnIndex(PlantEntry.COLUMN_PURCHASED_QUANTITY);

        if (c.getCount() == 0) {
            return;
        }

        if (columnId != -1) {
            this.id = c.getInt(columnId);
        }
        if (columnName != -1) {
            this.name = c.getString(columnName);
        }
        if (columnDescription != -1) {
            this.description = c.getString(columnDescription);
        }
        if (columnPrice != -1) {
            this.price = c.getInt(columnPrice);
        }
        if (columnCartQuantity != -1) {
            this.cartQuantity = c.getInt(columnCartQuantity);
        }
        if (columnPurchasedQuantity != -1) {
            this.purchasedQuantity = c.getInt(columnPurchasedQuantity);
        }
    }
}
