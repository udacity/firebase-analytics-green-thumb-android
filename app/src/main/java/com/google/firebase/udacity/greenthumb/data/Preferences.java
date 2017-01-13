package com.google.firebase.udacity.greenthumb.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * {@link Preferences} stores and retrieves the gardening experience rating from shared
 * preferences.
 */
public class Preferences {

    /**
     * first_load is true if the current app session is the first time the app has been opened.
     * False otherwise.
     */
    public static final String PREFS_FIRST_LOAD = "first_load";
    /**
     * gardening_experience is the rating index from 0 to 4.
     * The index value indexes the string array gardening_experience_rating in strings.xml
     */
    public static final String PREFS_GARDENING_EXPERIENCE = "gardening_experience";

    /**
     * Gets the first load boolean flag.
     * @param context The context to use for default shared preferences
     * @return true if the current session is the first time the app has loaded. False otherwise.
     */
    public static boolean getFirstLoad(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREFS_FIRST_LOAD, true);
    }

    /**
     * Sets the first load boolean flag.
     * @param context The context to use for default shared preferences.
     * @param firstLoadFlag The first load flag value to save.
     */
    public static void setFirstLoad(Context context, boolean firstLoadFlag) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREFS_FIRST_LOAD, firstLoadFlag);
        editor.apply();
    }

    /**
     * Gets the gardening experience rating index (0 to 4).
     * @param context The context to use for default shared preferences
     * @return The rating index from 0 to 4, or -1 if there is no rating in shared preferences.
     */
    public static int getGardeningExperience(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(PREFS_GARDENING_EXPERIENCE, -1);
    }

    /**
     * Sets the gardening experience rating index
     * @param context The context to use for default shared preferences
     * @param ratingExperience The rating experience value to save. Should range from 0 to 4.
     */
    public static void setGardeningExperience(Context context, int ratingExperience) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREFS_GARDENING_EXPERIENCE, ratingExperience);
        editor.apply();
    }
}
