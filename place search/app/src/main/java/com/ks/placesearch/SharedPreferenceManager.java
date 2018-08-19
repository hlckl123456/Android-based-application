package com.ks.placesearch;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.Map;


public class SharedPreferenceManager {


    private static final String PREF_NAME = "favouriteList";
    private static String TAG = "SharedPreferenceManager";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    int PRIVATE_MODE = 0;

    @SuppressLint("CommitPrefEdits")
    public SharedPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void setFavourite(String symbol, String json_string) {
        editor.putString(symbol, json_string);
        editor.commit();
        Log.d(TAG, "User set favourites");
    }

    public boolean isFavourite(String placdId) {
        return pref.contains(placdId);
    }

    public void removeFavourite(String placdId) {
        editor.remove(placdId);
        editor.commit();
        Log.d(TAG, "User remove favourites");
    }

    public Map<String, ?> getAll() {
        return pref.getAll();
    }

    public String getFavourite(String symbol) {
        return pref.getString(symbol, "-1");
    }
}
