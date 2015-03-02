package com.nkdroid.bemcycle.model;

import java.util.ArrayList;

/**
 * Created by nirav kalola on 2/15/2015.
 */
public class AdvertType {

    public String AdvertCode;
    public String AdvertName;
    public ArrayList<String> subList;

    public AdvertType(String advertCode, String advertName) {
        AdvertCode = advertCode;
        AdvertName = advertName;
    }

    public AdvertType(ArrayList<String> subList,String advertCode, String advertName) {
        this.subList = subList;
        AdvertCode = advertCode;
        AdvertName = advertName;
    }
}
