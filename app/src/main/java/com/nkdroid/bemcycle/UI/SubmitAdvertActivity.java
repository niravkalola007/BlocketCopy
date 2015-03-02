package com.nkdroid.bemcycle.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nkdroid.bemcycle.R;
import com.nkdroid.bemcycle.custom.SharedPreferenceAdvertType;
import com.nkdroid.bemcycle.custom.SharedPreferenceCityType;
import com.nkdroid.bemcycle.custom.SharedPreferenceProductTypes;
import com.nkdroid.bemcycle.model.AdvertType;
import com.nkdroid.bemcycle.model.AppConstants;
import com.nkdroid.bemcycle.model.CityType;
import com.nkdroid.bemcycle.model.ProductType;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class SubmitAdvertActivity extends ActionBarActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    static final int REQUEST_VIDEO_FROM_GALLERY = 2;
    private Uri selectedImageUri;

    String selectedVideoPath = "no";
    private Toolbar toolbar;

    private SharedPreferenceProductTypes sharedPreferenceProductTypes;
    private SharedPreferenceAdvertType sharedPreferenceAdvertType;
    private SharedPreferenceCityType sharedPreferenceCityType;
    private ArrayList<AdvertType> advertTypeList;
    private ArrayList<CityType> cityTypeList;
    private ArrayList<ProductType> productList;
    private TextView  spProductType,spAdvertType,spcityType;
    private EditText etName, etEmail, etMobile, etAdvertTitle, etDate, etAdvertDescription, etPostCode;
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
        imageUpload = (ImageView) findViewById(R.id.imageUpload);
        registerForContextMenu(imageUpload);
        imageUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        etName = (EditText) findViewById(R.id.etName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etPostCode = (EditText) findViewById(R.id.etPostCode);
        etAdvertTitle = (EditText) findViewById(R.id.etAdvertTitle);
        etAdvertDescription = (EditText) findViewById(R.id.etAdvertDescription);
        txtSubmit = (TextView) findViewById(R.id.txtSubmit);
        txtSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmptyField(etName)) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                } else if (isEmailMatch(etEmail) == false) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Enter Valid Email", Toast.LENGTH_SHORT).show();

                } else if (isEmptyField(etMobile)) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Enter Mobile", Toast.LENGTH_SHORT).show();
                } else if (isEmptyField(etAdvertTitle)) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Enter Advert Title", Toast.LENGTH_SHORT).show();
                } else if (isEmptyField(etAdvertDescription)) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Enter Advet Description", Toast.LENGTH_SHORT).show();
                } else if (isEmptyField(etPostCode)) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Enter Post Code", Toast.LENGTH_SHORT).show();
                } else if (isEmptyField(etDate)) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Select Date", Toast.LENGTH_SHORT).show();
                }
                else if (spProductType.getText().toString().toString().equalsIgnoreCase("Select Product Type")) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Select Product Category", Toast.LENGTH_SHORT).show();
                }
                else if (spAdvertType.getText().toString().toString().equalsIgnoreCase("Select Advert Type")) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Select Advert Type", Toast.LENGTH_SHORT).show();
                } else if (spcityType.getText().toString().toString().equalsIgnoreCase("Select City")) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Select City", Toast.LENGTH_SHORT).show();
                }
                else if (selectedVideoPath.equalsIgnoreCase("no")) {
                    Toast.makeText(SubmitAdvertActivity.this, "Please Select Image", Toast.LENGTH_SHORT).show();
                } else {

                    final ProgressDialog progressDialogValue = new ProgressDialog(SubmitAdvertActivity.this);
                    progressDialogValue.setMessage("Uploading Data...");
                    progressDialogValue.setCancelable(false);
                    progressDialogValue.show();
                    ParseObject pObject = new ParseObject("general");
                    pObject.put("Name", etName.getText().toString().trim());
                    pObject.put("Epost", etEmail.getText().toString().trim());
                    pObject.put("Telefon", Integer.parseInt(etMobile.getText().toString().trim()));
                    pObject.put("Category", spProductType.getText().toString().trim());
                    pObject.put("City", spcityType.getText().toString().trim());
                    pObject.put("Advert", spAdvertType.getText().toString());
                    pObject.put("AdsTitel", etAdvertTitle.getText().toString().trim());
                    pObject.put("AdsDescription", etAdvertDescription.getText().toString().trim());
                    pObject.put("post_code", Integer.parseInt(etPostCode.getText().toString().trim()));
                    pObject.put("status", AppConstants.NOT_APPROVED);
                    pObject.put("DateString", etDate.getText().toString().trim());
                    BitmapDrawable drawable = (BitmapDrawable) imageUpload.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ParseFile file = new ParseFile(System.currentTimeMillis() + ".jpg", getBytesFromBitmap(bitmap));
                    file.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            progressDialogValue.dismiss();
                            Toast.makeText(SubmitAdvertActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                    pObject.put("ThumbnailImage", file);

                    pObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ex == null) {


                            } else {

                                Toast.makeText(SubmitAdvertActivity.this, "Error while uploading, try again!" + ex, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        etDate = (EditText) findViewById(R.id.etDate);
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
                        try {
                            etDate.setText(new SimpleDateFormat("dd-MM-yyyy").format(new SimpleDateFormat("dd-MM-yyyy").parse(etDate.getText().toString())) + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                datePicker.show();
            }
        });
        spAdvertType = (TextView) findViewById(R.id.spAdvertType);
        spProductType = (TextView) findViewById(R.id.spProductType);
        spcityType = (TextView) findViewById(R.id.spcityType);

        sharedPreferenceProductTypes = new SharedPreferenceProductTypes();
        sharedPreferenceAdvertType = new SharedPreferenceAdvertType();
        sharedPreferenceCityType = new SharedPreferenceCityType();


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
        advertTypeList = new ArrayList<>();
        cityTypeList = new ArrayList<>();
        productList = new ArrayList<>();

        cityTypeList.addAll(sharedPreferenceCityType.loadCityType(SubmitAdvertActivity.this));
        advertTypeList.addAll(sharedPreferenceAdvertType.loadAdvertType(SubmitAdvertActivity.this));
        productList.addAll(sharedPreferenceProductTypes.loadProductTypes(SubmitAdvertActivity.this));


        spcityType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog alertDialog = new Dialog(SubmitAdvertActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.list, null);
                alertDialog.setContentView(convertView);
                alertDialog.setTitle("Select City");
                ListView lv = (ListView) convertView.findViewById(R.id.lv);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


//                    String names[] ={"A","B","C","D"};
                            final Dialog innerAlertDialog = new Dialog(SubmitAdvertActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View convertView = (View) inflater.inflate(R.layout.list, null);
                            innerAlertDialog.setContentView(convertView);
                            innerAlertDialog.setTitle(cityTypeList.get(position).cityName);
                            ListView lv = (ListView) convertView.findViewById(R.id.lv);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                    spcityType.setText(cityTypeList.get(position).subList.get(pos));
                                    innerAlertDialog.dismiss();
                                    alertDialog.dismiss();
                                }
                            });

                            CitySubAdapter productSubAdapter = new CitySubAdapter(SubmitAdvertActivity.this, cityTypeList.get(position).subList);
                            lv.setAdapter(productSubAdapter);
                            innerAlertDialog.show();


                    }
                });
                CityAdapter productSubAdapter = new CityAdapter(SubmitAdvertActivity.this, cityTypeList);
                lv.setAdapter(productSubAdapter);
                alertDialog.show();
            }
        });

        spAdvertType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog alertDialog = new Dialog(SubmitAdvertActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.list, null);
                alertDialog.setContentView(convertView);
                alertDialog.setTitle("Select Advert Type");
                ListView lv = (ListView) convertView.findViewById(R.id.lv);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        spAdvertType.setText(advertTypeList.get(position).AdvertName);

                        alertDialog.dismiss();

                    }
                });
                AdvertAdapter productSubAdapter = new AdvertAdapter(SubmitAdvertActivity.this, advertTypeList);
                lv.setAdapter(productSubAdapter);
                alertDialog.show();
            }
        });
        spProductType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog alertDialog = new Dialog(SubmitAdvertActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View convertView = (View) inflater.inflate(R.layout.list, null);
                alertDialog.setContentView(convertView);
                alertDialog.setTitle("Select Product Type");
                ListView lv = (ListView) convertView.findViewById(R.id.lv);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


//                    String names[] ={"A","B","C","D"};
                            final Dialog innerAlertDialog = new Dialog(SubmitAdvertActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View convertView = (View) inflater.inflate(R.layout.list, null);
                            innerAlertDialog.setContentView(convertView);
                            innerAlertDialog.setTitle(productList.get(position).ProductName);
                            ListView lv = (ListView) convertView.findViewById(R.id.lv);
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                                    spProductType.setText(productList.get(position).subList.get(pos));
                                    innerAlertDialog.dismiss();
                                    alertDialog.dismiss();
                                }
                            });

                            ProductSubAdapter productSubAdapter = new ProductSubAdapter(SubmitAdvertActivity.this, productList.get(position).subList);
                            lv.setAdapter(productSubAdapter);
                            innerAlertDialog.show();

                    }
                });
                ProductAdapter productSubAdapter = new ProductAdapter(SubmitAdvertActivity.this, productList);
                lv.setAdapter(productSubAdapter);
                alertDialog.show();
            }
        });

    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 1, Menu.NONE, "Take Picture");
        menu.add(0, 2, Menu.NONE, "Gallery");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                }
                break;
            case 2:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
                break;
            case 3:

                break;
        }
        return super.onContextItemSelected(item);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode != RESULT_OK)
//            return;
//
//        switch (requestCode) {
//            case REQUEST_VIDEO_CAPTURE:
//                selectedImageUri = data.getData();
//
//                selectedVideoPath = getPath(selectedImageUri);
//
//                File imgFile = new File(selectedVideoPath);
//                if(imgFile.exists()){
//                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                    imageUpload.setImageBitmap(myBitmap);
//                }
//
//
//                break;
//
//            case REQUEST_VIDEO_FROM_GALLERY:
//                selectedImageUri = data.getData();
//
//                selectedVideoPath = getPath(selectedImageUri);
//                File imgFileNew = new File(selectedVideoPath);
//                if(imgFileNew.exists()){
//                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFileNew.getAbsolutePath());
//                    imageUpload.setImageBitmap(myBitmap);
//                }
//                break;
//        }
//
//    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(SubmitAdvertActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 0);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);

                    // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);

                    imageUpload.setImageBitmap(bm);
                    selectedVideoPath = "yes";
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Uri selectedImageUri = data.getData();

                String tempPath = getPath(selectedImageUri, SubmitAdvertActivity.this);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                imageUpload.setImageBitmap(bm);
                selectedVideoPath = "yes";
            }
        }
    }

    public String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    @Override
    protected void onResume() {
        super.onResume();


    }

    public class ProductSubAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> drawerTitleList;

        public ProductSubAdapter(Context context, ArrayList<String> drawerTitleList) {
            this.context = context;
            this.drawerTitleList = drawerTitleList;

        }

        public int getCount() {

            return drawerTitleList.size();
        }

        public Object getItem(int position) {
            return drawerTitleList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView drawerText;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_list_dialog, parent,false);
                holder = new ViewHolder();
                holder.drawerText=(TextView)convertView.findViewById(R.id.txtItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.drawerText.setText(drawerTitleList.get(position));
            return convertView;
        }
    }

    public class ProductAdapter extends BaseAdapter {

        Context context;
        ArrayList<ProductType> drawerTitleList;

        public ProductAdapter(Context context, ArrayList<ProductType> drawerTitleList) {
            this.context = context;
            this.drawerTitleList = drawerTitleList;

        }

        public int getCount() {

            return drawerTitleList.size();
        }

        public Object getItem(int position) {
            return drawerTitleList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView drawerText;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_list_dialog, parent,false);
                holder = new ViewHolder();
                holder.drawerText=(TextView)convertView.findViewById(R.id.txtItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.drawerText.setText(drawerTitleList.get(position).ProductName);
            return convertView;
        }
    }

    public class CitySubAdapter extends BaseAdapter {

        Context context;
        ArrayList<String> drawerTitleList;

        public CitySubAdapter(Context context, ArrayList<String> drawerTitleList) {
            this.context = context;
            this.drawerTitleList = drawerTitleList;

        }

        public int getCount() {

            return drawerTitleList.size();
        }

        public Object getItem(int position) {
            return drawerTitleList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView drawerText;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_list_dialog, parent,false);
                holder = new ViewHolder();
                holder.drawerText=(TextView)convertView.findViewById(R.id.txtItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.drawerText.setText(drawerTitleList.get(position));
            return convertView;
        }
    }

    public class CityAdapter extends BaseAdapter {

        Context context;
        ArrayList<CityType> drawerTitleList;

        public CityAdapter(Context context, ArrayList<CityType> drawerTitleList) {
            this.context = context;
            this.drawerTitleList = drawerTitleList;

        }

        public int getCount() {

            return drawerTitleList.size();
        }

        public Object getItem(int position) {
            return drawerTitleList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView drawerText;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_list_dialog, parent,false);
                holder = new ViewHolder();
                holder.drawerText=(TextView)convertView.findViewById(R.id.txtItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.drawerText.setText(drawerTitleList.get(position).cityName);
            return convertView;
        }
    }



    public class AdvertAdapter extends BaseAdapter {

        Context context;
        ArrayList<AdvertType> drawerTitleList;

        public AdvertAdapter(Context context, ArrayList<AdvertType> drawerTitleList) {
            this.context = context;
            this.drawerTitleList = drawerTitleList;

        }

        public int getCount() {

            return drawerTitleList.size();
        }

        public Object getItem(int position) {
            return drawerTitleList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            TextView drawerText;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_list_dialog, parent,false);
                holder = new ViewHolder();
                holder.drawerText=(TextView)convertView.findViewById(R.id.txtItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.drawerText.setText(drawerTitleList.get(position).AdvertName);
            return convertView;
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
