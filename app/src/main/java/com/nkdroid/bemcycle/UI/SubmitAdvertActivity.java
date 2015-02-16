package com.nkdroid.bemcycle.UI;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nkdroid.bemcycle.R;
import com.nkdroid.bemcycle.custom.SharedPreferenceAdvertType;
import com.nkdroid.bemcycle.custom.SharedPreferenceCityType;
import com.nkdroid.bemcycle.custom.SharedPreferenceProductTypes;
import com.nkdroid.bemcycle.model.AdvertType;
import com.nkdroid.bemcycle.model.CityType;
import com.nkdroid.bemcycle.model.ProductType;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class SubmitAdvertActivity extends ActionBarActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_VIDEO_FROM_GALLERY = 2;
    private Uri selectedImageUri;

    String selectedVideoPath="no";
    private Toolbar toolbar;

    private SharedPreferenceProductTypes sharedPreferenceProductTypes;
    private SharedPreferenceAdvertType sharedPreferenceAdvertType;
    private SharedPreferenceCityType sharedPreferenceCityType;
    private ArrayList<AdvertType> advertTypeList;
    private ArrayList<CityType> cityTypeList;
    private ArrayList<ProductType> productList;
    private Spinner spAdvertType,spProductType,spcityType;
    private EditText etName,etEmail,etMobile,etAdvertTitle,etDate,etAdvertDescription;
    private TextView txtSubmit;
    private ImageView imageUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_advert);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.mipmap.ic_launcher);
            toolbar.setTitle("SUBMIT ADVERT");
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        imageUpload= (ImageView) findViewById(R.id.imageUpload);
        registerForContextMenu(imageUpload);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContextMenu(imageUpload);
            }
        });
        etName= (EditText) findViewById(R.id.etName);
        etEmail= (EditText) findViewById(R.id.etEmail);
        etMobile= (EditText) findViewById(R.id.etMobile);
        etAdvertTitle= (EditText) findViewById(R.id.etAdvertTitle);
        etAdvertDescription= (EditText) findViewById(R.id.etAdvertDescription);
        txtSubmit= (TextView) findViewById(R.id.txtSubmit);
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(isEmptyField(etName)){
                Toast.makeText(SubmitAdvertActivity.this,"Please Enter Name",Toast.LENGTH_SHORT).show();
            } else if(isEmailMatch(etEmail)==false){
                Toast.makeText(SubmitAdvertActivity.this,"Please Enter Valid Email",Toast.LENGTH_SHORT).show();

            } else if(isEmptyField(etMobile)){
                Toast.makeText(SubmitAdvertActivity.this,"Please Enter Mobile",Toast.LENGTH_SHORT).show();
            } else if(isEmptyField(etAdvertTitle)){
                Toast.makeText(SubmitAdvertActivity.this,"Please Enter Advert Title",Toast.LENGTH_SHORT).show();
            } else if(isEmptyField(etAdvertDescription)){
                Toast.makeText(SubmitAdvertActivity.this,"Please Enter Advet Description",Toast.LENGTH_SHORT).show();
            } else if(isEmptyField(etDate)){
                Toast.makeText(SubmitAdvertActivity.this,"Please Select Date",Toast.LENGTH_SHORT).show();
            }
            else if(spProductType.getSelectedItemPosition()==0){
                Toast.makeText(SubmitAdvertActivity.this,"Please Select Product Category",Toast.LENGTH_SHORT).show();
            }else if(spAdvertType.getSelectedItemPosition()==0){
                Toast.makeText(SubmitAdvertActivity.this,"Please Select Advert Type",Toast.LENGTH_SHORT).show();
            }
            else if(spcityType.getSelectedItemPosition()==0){
                Toast.makeText(SubmitAdvertActivity.this,"Please Select City",Toast.LENGTH_SHORT).show();
            }
            else if(selectedVideoPath.equalsIgnoreCase("no")){
                Toast.makeText(SubmitAdvertActivity.this,"Please Select Image",Toast.LENGTH_SHORT).show();
            } else {

                final ProgressDialog progressDialogValue=new ProgressDialog(SubmitAdvertActivity.this);
                progressDialogValue.setMessage("Uploading Data...");
                progressDialogValue.setCancelable(false);
                progressDialogValue.show();
                ParseObject pObject = new ParseObject("general");
                pObject.put("Name", etName.getText().toString().trim());
                pObject.put("Epost",etEmail.getText().toString().trim() );
                pObject.put("Telefon",Integer.parseInt(etMobile.getText().toString().trim()) );
                pObject.put("Category", productList.get(spProductType.getSelectedItemPosition()).ProductName );
                pObject.put("City",cityTypeList.get(spcityType.getSelectedItemPosition()).cityName );
                pObject.put("Advert", advertTypeList.get(spAdvertType.getSelectedItemPosition()).AdvertName);
                pObject.put("AdsTitel", etAdvertTitle.getText().toString().trim());
                pObject.put("AdsDescription",etAdvertDescription.getText().toString().trim() );
                pObject.put("DateString",etDate.getText().toString().trim());
                BitmapDrawable  drawable =   (BitmapDrawable)  imageUpload . getDrawable ();
                Bitmap  bitmap =  drawable . getBitmap ();
                ParseFile file = new ParseFile(System.currentTimeMillis()+".jpg", getBytesFromBitmap(bitmap));
                file.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialogValue.dismiss();
                        Toast.makeText(SubmitAdvertActivity.this,"Data updated successfully",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                pObject.put("ThumbnailImage", file );

                pObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException ex) {
                        if (ex == null) {


                        } else {

                            Toast.makeText(SubmitAdvertActivity.this,"Error while uploading, try again!"+ex,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            }
        });
        etDate= (EditText) findViewById(R.id.etDate);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(SubmitAdvertActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }
                }, mYear, mMonth, mDay);
                datePicker.show();
            }
        });
        spAdvertType= (Spinner) findViewById(R.id.spAdvertType);
        spProductType= (Spinner) findViewById(R.id.spProductType);
        spcityType= (Spinner) findViewById(R.id.spcityType);

        sharedPreferenceProductTypes=new SharedPreferenceProductTypes();
        sharedPreferenceAdvertType=new SharedPreferenceAdvertType();
        sharedPreferenceCityType=new SharedPreferenceCityType();


//        cityTypeList=new ArrayList<String>();
//        advertTypeList=new ArrayList<String>();
//        productList=new ArrayList<String>();
//        for(int i=0;i<sharedPreferenceAdvertType.loadAdvertType(SubmitAdvertActivity.this).size();i++)
//        {
//            advertTypeList.add(sharedPreferenceAdvertType.loadAdvertType(SubmitAdvertActivity.this).get(i).AdvertName);
//        }
//        for(int i=0;i<sharedPreferenceCityType.loadCityType(SubmitAdvertActivity.this).size();i++)
//        {
//            cityTypeList.add(sharedPreferenceCityType.loadCityType(SubmitAdvertActivity.this).get(i).cityName);
//        }
//        for(int i=0;i<sharedPreferenceProductTypes.loadProductTypes(SubmitAdvertActivity.this).size();i++)
//        {
//            productList.add(sharedPreferenceProductTypes.loadProductTypes(SubmitAdvertActivity.this).get(i).ProductName);
//        }
//        try {
//            Toast.makeText(SubmitAdvertActivity.this, cityTypeList.size() + "" , Toast.LENGTH_LONG).show();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        advertTypeList=new ArrayList<>();
        cityTypeList=new ArrayList<>();
        productList=new ArrayList<>();

        cityTypeList.addAll(sharedPreferenceCityType.loadCityType(SubmitAdvertActivity.this));
        cityTypeList.add(0,new CityType("","Select City"));
        advertTypeList.addAll(sharedPreferenceAdvertType.loadAdvertType(SubmitAdvertActivity.this));
        advertTypeList.add(0,new AdvertType("","Select Advert Type"));
        productList.addAll(sharedPreferenceProductTypes.loadProductTypes(SubmitAdvertActivity.this));
        productList.add(0,new ProductType("","Select Product Category"));
        CityAdapter cityAdapter=new CityAdapter(SubmitAdvertActivity.this,R.layout.item_row,cityTypeList);
        spcityType.setAdapter(cityAdapter);
        ProductAdapter productAdapter=new ProductAdapter(SubmitAdvertActivity.this,R.layout.item_row,productList);
        spProductType.setAdapter(productAdapter);

        AdvertAdapter advertAdapter=new AdvertAdapter(SubmitAdvertActivity.this,R.layout.item_row,advertTypeList);
        spAdvertType.setAdapter(advertAdapter);
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0,1,Menu.NONE,"Take Picture");
        menu.add(0,2,Menu.NONE,"Gallery");

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case 1:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                break;
            case 2:
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent,"Complete action using"), 2);
                break;
            case 3:

                break;
        }
        return super.onContextItemSelected(item);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case REQUEST_VIDEO_CAPTURE:
                selectedImageUri = data.getData();

                selectedVideoPath = getPath(selectedImageUri);

                File imgFile = new File(selectedVideoPath);
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    imageUpload.setImageBitmap(myBitmap);
                }


                break;

            case REQUEST_VIDEO_FROM_GALLERY:
                selectedImageUri = data.getData();

                selectedVideoPath = getPath(selectedImageUri);
                File imgFileNew = new File(selectedVideoPath);
                if(imgFileNew.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFileNew.getAbsolutePath());
                    imageUpload.setImageBitmap(myBitmap);
                }
                break;
        }

    }

    public String getPath(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_submit_advert, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public class CityAdapter extends ArrayAdapter<CityType>{

        Context context;
        int layoutResourceId;
        ArrayList<CityType> values;
        // int android.R.Layout.

        public CityAdapter(Context context, int resource, ArrayList<CityType> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values=objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(SubmitAdvertActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).cityName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(SubmitAdvertActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setText(values.get(position).cityName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }
    }

    public class AdvertAdapter extends ArrayAdapter<AdvertType>{

        Context context;
        int layoutResourceId;
        ArrayList<AdvertType> values;
        // int android.R.Layout.

        public AdvertAdapter(Context context, int resource, ArrayList<AdvertType> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values=objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(SubmitAdvertActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).AdvertName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(SubmitAdvertActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setText(values.get(position).AdvertName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }
    }

    public class ProductAdapter extends ArrayAdapter<ProductType>{

        Context context;
        int layoutResourceId;
        ArrayList<ProductType> values;
        // int android.R.Layout.

        public ProductAdapter(Context context, int resource, ArrayList<ProductType> objects) {
            super(context, resource, objects);
            this.context = context;
            this.values=objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(SubmitAdvertActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).ProductName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(SubmitAdvertActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setText(values.get(position).ProductName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }
    }

    public boolean isEmptyField(EditText param1) {

        boolean isEmpty = false;
        if (param1.getText() == null || param1.getText().toString().equalsIgnoreCase("")) {
            isEmpty = true;
        }
        return isEmpty;
    }



    public boolean isEmailMatch(EditText param1) {
        // boolean isMatch = false;
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(param1.getText().toString()).matches();
    }
}
