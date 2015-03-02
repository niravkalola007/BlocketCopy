package com.nkdroid.bemcycle.model;

import java.util.ArrayList;

/**
 * Created by nirav kalola on 2/15/2015.
 */
public class ProductType {

    public String ProductCode;
    public String ProductName;
    public ArrayList<String> subList;

    public ProductType(String productCode, String productName) {

        ProductCode = productCode;
        ProductName = productName;
    }

    public ProductType(ArrayList<String> subList, String productCode, String productName) {
        this.subList = subList;
        ProductCode = productCode;
        ProductName = productName;
    }
}
