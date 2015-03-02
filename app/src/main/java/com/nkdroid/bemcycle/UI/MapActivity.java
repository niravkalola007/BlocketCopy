package com.nkdroid.bemcycle.UI;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nkdroid.bemcycle.R;

import java.io.IOException;
import java.util.List;

public class MapActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private String postCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent=getIntent();
        postCode=intent.getStringExtra("post_code");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
            toolbar.setTitle("MAP");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        locationFromPostCode(postCode);
    }

    public void locationFromPostCode(String postCode){

        Geocoder geocoder1 = new Geocoder(this);
        try {
            List<Address> addresses1 = geocoder1.getFromLocationName(postCode, 1);
            if (addresses1 != null && !addresses1.isEmpty()) {
                Address address1 = addresses1.get(0);
                address1.setCountryName("sweden");
                // Use the address as needed

                GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                LatLng latlong = new LatLng(address1.getLatitude(), address1.getLongitude());
                map.setMyLocationEnabled(true);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, 13));
                map.addMarker(new MarkerOptions()
                        .title(postCode)

                        .position(latlong));
            } else {
                // Display appropriate message when Geocoder services are not available
                Toast.makeText(this, "Unable to geocode zipcode", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // handle exception
        }
    }
}
