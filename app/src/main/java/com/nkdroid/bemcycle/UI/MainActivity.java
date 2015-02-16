package com.nkdroid.bemcycle.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nkdroid.bemcycle.custom.SharedPreferenceAdvertType;
import com.nkdroid.bemcycle.custom.SharedPreferenceCityType;
import com.nkdroid.bemcycle.custom.SharedPreferenceProductTypes;
import com.nkdroid.bemcycle.model.AdvertType;
import com.nkdroid.bemcycle.model.CityType;
import com.nkdroid.bemcycle.model.ProductType;
import com.nkdroid.bemcycle.util.AppUtils;
import com.nkdroid.bemcycle.R;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    //  private ButtonRectangle btnLogout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private DrawerLayout drawerLayoutRight;
    private ActionBarDrawerToggle drawerToggleRight;
    private LinearLayout rightDrawerList;
    private Spinner spProductType,spcityType,spAdvertType;
    private TextView clearFilter;
    private SharedPreferenceProductTypes sharedPreferenceProductTypes;
    private SharedPreferenceAdvertType sharedPreferenceAdvertType;
    private SharedPreferenceCityType sharedPreferenceCityType;
    private ArrayList<AdvertType> advertTypeList;
    private ArrayList<CityType> cityTypeList;
    private ArrayList<ProductType> productList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private String[] leftSliderData = {"Adverts", "Submit Advert", "Customer service" };
    private  List<ParseObject> mDataset;
    private  List<ParseObject> mDatasetFiltered;
    private String searchingValue="";
    private int selectedCategory,selectedCity,selectedAdvert;

    private int[] imagelist = {R.drawable.ic_action_search,
            R.drawable.ic_submit_advert,
            R.drawable.ic_communication_email,

  };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nitView();


        if (toolbar != null) {
            toolbar.setTitle("BEM CYCLE");
            setSupportActionBar(toolbar);
        }

        initDrawer();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        HomeFragment categoryFragment = HomeFragment.newInstance("", "");
        if (manager.findFragmentByTag("HomeFragment") == null) {
            ft.replace(R.id.main_container, categoryFragment, "HomeFragment").commit();
        }



        advertTypeList=new ArrayList<>();
        cityTypeList=new ArrayList<>();
        productList=new ArrayList<>();

        final ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        sharedPreferenceProductTypes=new SharedPreferenceProductTypes();
        sharedPreferenceAdvertType=new SharedPreferenceAdvertType();
        sharedPreferenceCityType=new SharedPreferenceCityType();
        sharedPreferenceProductTypes.clearProductTypes(MainActivity.this);
        sharedPreferenceAdvertType.clearAdvertType(MainActivity.this);
        sharedPreferenceCityType.clearCitytype(MainActivity.this);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("product_category");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {

                    for(int i=0;i<scoreList.size();i++){

                        sharedPreferenceProductTypes.saveProductTypes(MainActivity.this,new ProductType(scoreList.get(i).getString("ProductCode"), scoreList.get(i).getString("ProductName")));
                    }

                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("advert_type_category");
                    query1.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {

                                for(int i=0;i<scoreList.size();i++){

                                    sharedPreferenceAdvertType.saveAdvertType(MainActivity.this,new AdvertType(scoreList.get(i).getString("AdvertCode"), scoreList.get(i).getString("AdvertName")));
                                }

                                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("city_category");
                                query2.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if (e == null) {

                                            for(int i=0;i<scoreList.size();i++){

                                                sharedPreferenceCityType.saveCityType(MainActivity.this,new CityType(scoreList.get(i).getString("CityCode"), scoreList.get(i).getString("CityName")));
                                            }

                                            progressDialog.dismiss();
                                            cityTypeList.addAll(sharedPreferenceCityType.loadCityType(MainActivity.this));
                                            cityTypeList.add(0,new CityType("","Select City"));
                                            advertTypeList.addAll(sharedPreferenceAdvertType.loadAdvertType(MainActivity.this));
                                            advertTypeList.add(0,new AdvertType("","Select Advert Type"));
                                            productList.addAll(sharedPreferenceProductTypes.loadProductTypes(MainActivity.this));
                                            productList.add(0,new ProductType("","Select Product Type"));


                                            CityAdapter cityAdapter=new CityAdapter(MainActivity.this,R.layout.item_row,cityTypeList);
                                            spcityType.setAdapter(cityAdapter);



                                            spcityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                                                    Toast.makeText(MainActivity.this, searchingValue+"", Toast.LENGTH_SHORT).show();
                                                    try {
                                                        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HomeFragment");
                                                        homeFragment.parseAdapter.filter(searchingValue, productList.get(spProductType.getSelectedItemPosition()).ProductName, cityTypeList.get(spcityType.getSelectedItemPosition()).cityName, advertTypeList.get(spAdvertType.getSelectedItemPosition()).AdvertName);
                                                        homeFragment.listView.invalidate();
                                                    }catch (NullPointerException e){
                                                        e.printStackTrace();
                                                    }
                                                    SharedPreferences preferences = getSharedPreferences("filter", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putInt("city", spcityType.getSelectedItemPosition());
                                                    editor.putInt("advert", spAdvertType.getSelectedItemPosition());
                                                    editor.putInt("category",spProductType.getSelectedItemPosition());
                                                    editor.commit();
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                            ProductAdapter productAdapter=new ProductAdapter(MainActivity.this,R.layout.item_row,productList);
                                            spProductType.setAdapter(productAdapter);
                                            spProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    try {
                                                        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HomeFragment");
                                                        homeFragment.parseAdapter.filter(searchingValue, productList.get(spProductType.getSelectedItemPosition()).ProductName, cityTypeList.get(spcityType.getSelectedItemPosition()).cityName, advertTypeList.get(spAdvertType.getSelectedItemPosition()).AdvertName);
                                                        homeFragment.listView.invalidate();
                                                    }catch (NullPointerException e){
                                                        e.printStackTrace();
                                                    }
                                                    SharedPreferences preferences = getSharedPreferences("filter", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putInt("city", spcityType.getSelectedItemPosition());
                                                    editor.putInt("advert", spAdvertType.getSelectedItemPosition());
                                                    editor.putInt("category",spProductType.getSelectedItemPosition());
                                                    editor.commit();
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                            AdvertAdapter advertAdapter=new AdvertAdapter(MainActivity.this,R.layout.item_row,advertTypeList);
                                            spAdvertType.setAdapter(advertAdapter);

                                            SharedPreferences preferences = getSharedPreferences("filter", MODE_PRIVATE);
                                            selectedCategory=preferences.getInt("category",0);
                                            selectedCity=preferences.getInt("city",0);
                                            selectedAdvert=preferences.getInt("advert",0);
                                            spAdvertType.setSelection(selectedAdvert);
                                            spcityType.setSelection(selectedCity);
                                            spProductType.setSelection(selectedCategory);

                                            spAdvertType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                    try {
                                                        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HomeFragment");
                                                        homeFragment.parseAdapter.filter(searchingValue, productList.get(spProductType.getSelectedItemPosition()).ProductName, cityTypeList.get(spcityType.getSelectedItemPosition()).cityName, advertTypeList.get(spAdvertType.getSelectedItemPosition()).AdvertName);
                                                        homeFragment.listView.invalidate();
                                                    }catch (NullPointerException e){
                                                        e.printStackTrace();
                                                    }
                                                    SharedPreferences preferences = getSharedPreferences("filter", MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putInt("city", spcityType.getSelectedItemPosition());
                                                    editor.putInt("advert", spAdvertType.getSelectedItemPosition());
                                                    editor.putInt("category",spProductType.getSelectedItemPosition());
                                                    editor.commit();


                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        }
                    });
                }
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();

        // Display the current values for this user, such as their age and gender.

    }

    private void nitView() {
        spProductType= (Spinner) findViewById(R.id.spProductType);
        spcityType= (Spinner) findViewById(R.id.spcityType);
        spAdvertType= (Spinner) findViewById(R.id.spAdvertType);
        clearFilter= (TextView) findViewById(R.id.clearFilter);
        clearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "clear", Toast.LENGTH_SHORT).show();
                spProductType.setSelection(0);
                spcityType.setSelection(0);
                spAdvertType.setSelection(0);
            }
        });


        //  btnLogout = (ButtonRectangle)findViewById(R.id.btnLogout);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        rightDrawerList = (LinearLayout) findViewById(R.id.right_drawer);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setBackgroundColor(Color.parseColor("#494949"));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        drawerLayoutRight = (DrawerLayout) findViewById(R.id.drawerLayoutRight);
        navigationDrawerAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, leftSliderData);
        leftDrawerList.setAdapter(new DrawerAdapter(MainActivity.this,leftSliderData));

        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.TOP | Gravity.RIGHT);

        layoutParams.width = (int) AppUtils.convertDpToPixel(32, MainActivity.this);
        layoutParams.height = (int)AppUtils.convertDpToPixel(32,MainActivity.this);
        layoutParams.rightMargin = 16;


        leftDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();

                switch (position) {

                    case 0:
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        HomeFragment categoryFragment = HomeFragment.newInstance("", "");
                        if (manager.findFragmentByTag("HomeFragment") == null) {
                            ft.replace(R.id.main_container, categoryFragment, "HomeFragment").commit();
                        }



                        break;
                    case 1:
//                        Toast.makeText(MainActivity.this,"click",Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(MainActivity.this,SubmitAdvertActivity.class);
                        startActivity(intent);
                        break;
                    case 2:

//                        Intent intentService=new Intent(MainActivity.this,CustomerServiceActivity.class);
//                        startActivity(intentService);
                        break;


                }

            }
        });
    }

    public class DrawerAdapter extends BaseAdapter {

        Context context;
        String[] drawerTitleList;

        public DrawerAdapter(Context context, String[] drawerTitleList) {
            this.context = context;
            this.drawerTitleList = drawerTitleList;

        }

        public int getCount() {

            return drawerTitleList.length;
        }

        public Object getItem(int position) {
            return drawerTitleList[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            ImageView drawerIcon;
            TextView drawerText;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.mydrawer_listview_layout, parent,false);
                holder = new ViewHolder();
                holder.drawerIcon=(ImageView)convertView.findViewById(R.id.drawerIcon);
                holder.drawerText=(TextView)convertView.findViewById(R.id.drawerText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.drawerIcon.setImageResource(imagelist[position]);
            holder.drawerText.setText(leftSliderData[position]);
            return convertView;
        }
    }




    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {


            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                drawerLayoutRight.closeDrawer(rightDrawerList);
//                Toast.makeText(MainActivity.this, "abc", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

        };


        drawerLayout.setDrawerListener(drawerToggle);

        drawerToggleRight = new ActionBarDrawerToggle(this, drawerLayoutRight,R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);


            }


        };
        drawerLayoutRight.setDrawerListener(drawerToggleRight);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
//        drawerToggleRight.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
//        drawerToggleRight.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //*** setOnQueryTextFocusChangeListener ***
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {



            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(MainActivity.this, "called", Toast.LENGTH_SHORT).show();



                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {

                searchingValue=searchQuery;
                HomeFragment homeFragment= (HomeFragment) getSupportFragmentManager().findFragmentByTag("HomeFragment");
                homeFragment.parseAdapter.filter(searchQuery,productList.get(spProductType.getSelectedItemPosition()).ProductName,cityTypeList.get(spcityType.getSelectedItemPosition()).cityName,advertTypeList.get(spAdvertType.getSelectedItemPosition()).AdvertName);
                homeFragment.listView.invalidate();

                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
//            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_filter) {
//            Toast.makeText(this, "Filter", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(leftDrawerList);
            if(drawerLayoutRight.isDrawerOpen(rightDrawerList)){
                drawerLayoutRight.closeDrawer(rightDrawerList);

            }else {
                drawerLayoutRight.openDrawer(rightDrawerList);
            }

            return true;
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        if (drawerToggleRight.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


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

            TextView txt = new TextView(MainActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).cityName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MainActivity.this);
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

            TextView txt = new TextView(MainActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).AdvertName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MainActivity.this);
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

            TextView txt = new TextView(MainActivity.this);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(values.get(position).ProductName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(MainActivity.this);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(16,16,16,16);
            txt.setTextSize(18);
            txt.setText(values.get(position).ProductName);
            txt.setTextColor(Color.parseColor("#494949"));
            return  txt;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("filter", MODE_PRIVATE);
        selectedCategory=preferences.getInt("category",0);
        selectedCity=preferences.getInt("city",0);
        selectedAdvert=preferences.getInt("advert",0);
        spAdvertType.setSelection(selectedAdvert);
        spcityType.setSelection(selectedCity);
        spProductType.setSelection(selectedCategory);

    }
}