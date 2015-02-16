package com.nkdroid.bemcycle.UI;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.nkdroid.bemcycle.R;
//import com.ortiz.touch.TouchImageView;

public class FullScreenImageActivity extends ActionBarActivity {
    private Toolbar toolbar;
//    TouchImageView fullScreenImage;
    ImageView fullScreenImage;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
            toolbar.setTitle("FULL SCREEN IMAGE");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
//        fullScreenImage= (TouchImageView) findViewById(R.id.fullScreenImage);
        fullScreenImage= (ImageView) findViewById(R.id.fullScreenImage);
        Intent intent=getIntent();
        imagePath=intent.getStringExtra("image_path");

        Glide.with(FullScreenImageActivity.this)

                .load(imagePath)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(fullScreenImage);
    }



}
