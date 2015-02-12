package com.nkdroid.blocketcopy.UI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nkdroid.blocketcopy.R;

import java.io.IOException;

public class SplashActivity extends ActionBarActivity {
    private GoogleCloudMessaging gcm;
    private String regid;
    private String PROJECT_NUMBER = "92884720384";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {

                getRegId();
            }
        }.start();

    }

    public void getRegId(){

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(SplashActivity.this);
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    Log.e("GCM ID :", regid);
                    if(regid==null || regid==""){
                        AlertDialog.Builder alert = new AlertDialog.Builder(SplashActivity.this);
                        alert.setTitle("Error");
                        alert.setMessage("Internal Server Error");
                        alert.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getRegId();
                                dialog.dismiss();
                            }
                        });
                        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        alert.show();
                    } else {
                        // Store GCM ID in sharedpreference
                        SharedPreferences sharedPreferences=getSharedPreferences("GCM",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("GCM_ID",regid);
                        editor.commit();

                        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                        startActivity(intent);

                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        }.execute();

    } // end of getRegId

}
