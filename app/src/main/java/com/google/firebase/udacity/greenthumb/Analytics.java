package com.google.firebase.udacity.greenthumb;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.udacity.greenthumb.data.Plant;

/**
 * {@link Analytics} helps log analytics events
 */
public class Analytics {

    public static void logEventAddToCart(Context context, Plant plant, long quantity) {
        Bundle params = new Bundle();
        params.putInt(FirebaseAnalytics.Param.ITEM_ID, plant.id);
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, plant.name);
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "plants");
        params.putDouble(FirebaseAnalytics.Param.QUANTITY, quantity);
        params.putDouble(FirebaseAnalytics.Param.PRICE, plant.price);
        FirebaseAnalytics.getInstance(context).logEvent(
                FirebaseAnalytics.Event.ADD_TO_CART, params);
    }

    public static void logEventViewItem(Context context, Plant plant) {
        Bundle params = new Bundle();
        params.putInt(FirebaseAnalytics.Param.ITEM_ID, plant.id);
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, plant.name);
        FirebaseAnalytics.getInstance(context)
                .logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);
    }

    public static void logEventBeginCheckout(Context context) {
        FirebaseAnalytics.getInstance(context).logEvent(
                FirebaseAnalytics.Event.BEGIN_CHECKOUT, null);
    }

    public static void logEventEcommercePurchase(Context context) {
        FirebaseAnalytics.getInstance(context).logEvent(
                FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, null);
    }

}
