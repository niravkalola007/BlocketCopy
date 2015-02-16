package com.nkdroid.bemcycle.UI;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nkdroid.bemcycle.R;

public class ProductDetailActivity extends ActionBarActivity {
    private Toolbar toolbar;
    private String ptName,productDetail,date,name,mobile,email,imagePath;
    private TextView productName,productDetails,detailDate,detailName,detailEmail,detailMobile;
    private ImageView deailImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
            toolbar.setTitle("PRODUCT DETAILS");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        Intent intent=getIntent();
        ptName=intent.getStringExtra("product_name");
        productDetail=intent.getStringExtra("product_detail");
        date=intent.getStringExtra("posted_date");
        name=intent.getStringExtra("seller_name");
        mobile=intent.getStringExtra("mobile");
        email=intent.getStringExtra("email");
        imagePath=intent.getStringExtra("image_path");

        productName= (TextView) findViewById(R.id.productName);
        productDetails= (TextView) findViewById(R.id.productDetails);
        detailDate= (TextView) findViewById(R.id.detailDate);
        detailName= (TextView) findViewById(R.id.detailName);
        detailEmail= (TextView) findViewById(R.id.detailEmail);
        detailMobile= (TextView) findViewById(R.id.detailMobile);
        deailImage= (ImageView) findViewById(R.id.deailImage);
        deailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ProductDetailActivity.this,FullScreenImageActivity.class);
                intent.putExtra("image_path",imagePath);
                startActivity(intent);
            }
        });
        Glide.with(ProductDetailActivity.this)

                .load(imagePath)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .into(deailImage);
        productName.setText("Product: "+ptName);
        productDetails.setText("Description: "+productDetail);
        detailDate.setText("Published Date: "+date);
        detailName.setText("Name: "+name);
        detailEmail.setText("Email: "+email);
        detailMobile.setText("Mobile: "+mobile);


    }
}
