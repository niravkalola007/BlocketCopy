package com.nkdroid.bemcycle.model;

import java.util.ArrayList;

/**
 * Created by nirav kalola on 2/15/2015.
 */
public class CityType {

    public String CityCode;
    public String cityName;
    public ArrayList<String> subList;

    public CityType(String cityCode, String cityName) {
        CityCode = cityCode;
        this.cityName = cityName;
    }

    public CityType(ArrayList<String> subList,String cityCode, String cityName) {
        this.subList = subList;
        CityCode = cityCode;
        this.cityName = cityName;
    }
}
