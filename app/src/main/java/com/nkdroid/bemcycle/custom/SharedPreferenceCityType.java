package com.nkdroid.bemcycle.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.nkdroid.bemcycle.model.CityType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SharedPreferenceCityType {

	public static final String PREF_NAME = "SHARED_DATA_CITY";
	public static final String PREF_VALUE = "shared_values_for_CITY";
    List<CityType> driverNotifications =new ArrayList<CityType>();
	public SharedPreferenceCityType() {
		super();
	}


    public void clearCitytype(Context context) {
        SharedPreferences sharedPref;
        Editor editor;
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        driverNotifications.clear();
        editor.clear();
        editor.commit();
    }
    public void saveCityType(Context context, CityType driverNotification) {
        SharedPreferences sharedPref;
        Editor editor;
        if (driverNotifications == null) {
            driverNotifications = new ArrayList<CityType>();
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

    public ArrayList<CityType> loadCityType(Context context) {
        SharedPreferences sharePref;
        List<CityType> notificationList;
        sharePref = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        String jsonFavorites = sharePref.getString(PREF_VALUE, null);
        Gson gson = new Gson();
        CityType[] favoriteItems = gson.fromJson(jsonFavorites,CityType[].class);
        notificationList = new ArrayList<CityType>(Arrays.asList(favoriteItems));
//        Log.e(" array", notificationList + "");
        return (ArrayList<CityType>) notificationList;
    }

}
