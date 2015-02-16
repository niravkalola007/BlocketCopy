package com.nkdroid.bemcycle.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.nkdroid.bemcycle.model.AdvertType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SharedPreferenceAdvertType {

	public static final String PREF_NAME = "SHARED_DATA_ADVERT";
	public static final String PREF_VALUE = "shared_values_for_ADVERT";
    List<AdvertType> driverNotifications =new ArrayList<AdvertType>();
	public SharedPreferenceAdvertType() {
		super();
	}


    public void clearAdvertType(Context context) {
        SharedPreferences sharedPref;
        Editor editor;
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        driverNotifications.clear();
        editor.clear();
        editor.commit();
    }
    public void saveAdvertType(Context context, AdvertType driverNotification) {
        SharedPreferences sharedPref;
        Editor editor;
        if (driverNotifications == null) {
            driverNotifications = new ArrayList<AdvertType>();
        }
        driverNotifications.add(driverNotification);
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(driverNotifications);
        editor.putString(PREF_VALUE, jsonFavorites);
//        Log.e(" list:", jsonFavorites + "");
        editor.commit();
    }

    public ArrayList<AdvertType> loadAdvertType(Context context) {
        SharedPreferences sharePref;
        List<AdvertType> notificationList;
        sharePref = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        String jsonFavorites = sharePref.getString(PREF_VALUE, null);
        Gson gson = new Gson();
        AdvertType[] favoriteItems = gson.fromJson(jsonFavorites,AdvertType[].class);
        notificationList = new ArrayList<AdvertType>(Arrays.asList(favoriteItems));
//        Log.e(" array", notificationList + "");
        return (ArrayList<AdvertType>) notificationList;
    }

}
