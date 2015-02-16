package com.nkdroid.bemcycle.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nkdroid.bemcycle.util.AppUtils;
import com.nkdroid.bemcycle.R;
import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    //  private ButtonRectangle btnLogout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private DrawerLayout drawerLayoutRight;
    private ActionBarDrawerToggle drawerToggleRight;
    private ListView rightDrawerList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private String[] leftSliderData = {"Adverts", "Submit Advert", "Customer service" };

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

    }

    @Override
    public void onStart() {
        super.onStart();

        // Display the current values for this user, such as their age and gender.

    }

    private void nitView() {

        //  btnLogout = (ButtonRectangle)findViewById(R.id.btnLogout);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        rightDrawerList = (ListView) findViewById(R.id.right_drawer);
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

                        Intent intentService=new Intent(MainActivity.this,CustomerServiceActivity.class);
                        startActivity(intentService);
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
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
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




}