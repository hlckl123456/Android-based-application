package com.ks.placesearch;

import android.app.Application;

import org.json.JSONObject;

import java.util.HashMap;

public final class GlobalVaries extends Application {
    private HashMap<String, JSONObject> favList;

    @Override
    public void onCreate() {
        favList = new HashMap<String, JSONObject>();
        super.onCreate();
    }

    public HashMap<String, JSONObject> getFavList() {
        return favList;
    }

    public void addFav(String key, JSONObject json) {
        favList.put(key, json);
    }

    public void removeFav(String key) {
        favList.remove(key);
    }
}

