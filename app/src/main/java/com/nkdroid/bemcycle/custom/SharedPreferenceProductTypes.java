package com.nkdroid.bemcycle.custom;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.nkdroid.bemcycle.model.ProductType;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SharedPreferenceProductTypes {

	public static final String PREF_NAME = "SHARED_DATA_PRODUCT";
	public static final String PREF_VALUE = "shared_values_for_PRODUCT";
    List<ProductType> driverNotifications =new ArrayList<ProductType>();
	public SharedPreferenceProductTypes() {
		super();
	}


    public void clearProductTypes(Context context) {
        SharedPreferences sharedPref;
        Editor editor;
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        driverNotifications.clear();
        editor.clear();
        editor.commit();
    }
    public void saveProductTypes(Context context, ProductType driverNotification) {
        SharedPreferences sharedPref;
        Editor editor;
        if (driverNotifications == null) {
            driverNotifications = new ArrayList<ProductType>();
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

    public ArrayList<ProductType> loadProductTypes(Context context) {
        SharedPreferences sharePref;
        List<ProductType> notificationList;
        sharePref = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        String jsonFavorites = sharePref.getString(PREF_VALUE, null);
        Gson gson = new Gson();
        ProductType[] favoriteItems = gson.fromJson(jsonFavorites,ProductType[].class);
        notificationList = new ArrayList<ProductType>(Arrays.asList(favoriteItems));
//        Log.e(" array", notificationList + "");
        return (ArrayList<ProductType>) notificationList;
    }

}
