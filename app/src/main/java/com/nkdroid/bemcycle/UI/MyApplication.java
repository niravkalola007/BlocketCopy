package com.nkdroid.bemcycle.UI;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

/**
 * Created by nirav kalola on 2/13/2015.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "Tgd63gpxgISWQHca11IgELXtF3glaiMDQbFgJC0E", "zpbBGlTbFMqVJByCCEzKzCEw3BYOQ9TKWlyAwKbI");


        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
//        ParseInstallation.getCurrentInstallation().saveInBackground();
//        PushService.setDefaultPushCallback(this, MainActivity.class);

    }
}
