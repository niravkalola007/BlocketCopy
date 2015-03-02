package com.nkdroid.bemcycle.UI;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nkdroid.bemcycle.R;
import com.nkdroid.bemcycle.custom.MyCustomAdapter;
import com.nkdroid.bemcycle.custom.SharedPreferenceAdvertType;
import com.nkdroid.bemcycle.custom.SharedPreferenceCityType;
import com.nkdroid.bemcycle.custom.SharedPreferenceProductTypes;
import com.nkdroid.bemcycle.model.AdvertType;
import com.nkdroid.bemcycle.model.AppConstants;
import com.nkdroid.bemcycle.model.CityType;
import com.nkdroid.bemcycle.model.ProductType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.xml.sax.helpers.ParserAdapter;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {
    //    RecyclerView recyclerView;
    ListView  listView;
    public ProductAdapter parseAdapter;
    public List<ParseObject> sampleData;
    ArrayList<ParseObject> arraylist;
    //    RecyclerView.Adapter adapter;
    private SharedPreferenceProductTypes sharedPreferenceProductTypes;
    private SharedPreferenceAdvertType sharedPreferenceAdvertType;
    private SharedPreferenceCityType sharedPreferenceCityType;
    SwipeRefreshLayout swipeView;
    public static String categoryValue="Select Product Type";
    public static String cityValue="Select City";
    public static String advertValue="Select Advert Type";
    private String selectedCategory,selectedCity,selectedAdvert;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView= inflater.inflate(R.layout.fragment_home, container, false);
//        recyclerView= (RecyclerView)convertView.findViewById(R.id.my_recycler_view);
        listView= (ListView)convertView.findViewById(R.id.my_recycler_view);
        // use a linear layout manager
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(mLayoutManager);

        swipeView= (SwipeRefreshLayout) convertView.findViewById(R.id.swipe);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);


                // Create the adapter
                ParseQuery<ParseObject> query = ParseQuery.getQuery("general");
                query.orderByDescending("DateString");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> scoreList, ParseException e) {
                        if (e == null) {
                            swipeView.setRefreshing(false);

                            ArrayList<ParseObject> newList=new ArrayList<ParseObject>();

                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
                           final  Calendar cal = Calendar.getInstance();

                            cal.add(Calendar.DATE, -7);
                            for(int i=0;i<scoreList.size();i++){
                                try {
                                    if (simpleDateFormat.parse(scoreList.get(i).get("DateString").toString()).after(simpleDateFormat.parse(simpleDateFormat.format(cal.getTime()))) && scoreList.get(i).get("status").toString().equalsIgnoreCase(AppConstants.APPROVED)) {
                                        newList.add(scoreList.get(i));
                                    }

                                }catch (Exception ee){
                                    ee.printStackTrace();
                                }
                            }

                            parseAdapter=new ProductAdapter(getActivity(),newList);
                            listView.setAdapter(parseAdapter);

                            parseAdapter.filter(MainActivity.searchingValue,MainActivity.categoryValue,MainActivity.cityValue,MainActivity.advertValue);
                            parseAdapter.notifyDataSetChanged();


                        } else {

                        }
                    }
                });
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem==0){
                    swipeView.setEnabled(true);
                } else {
                    swipeView.setEnabled(false);
                }
            }

        });



        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
//        recyclerView.setHasFixedSize(true);



        // Data set used by the adapter. This data will be displayed.
        return convertView;
    }

    @Override
    public void onResume() {
        super.onResume();

        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create the adapter
        ParseQuery<ParseObject> query = ParseQuery.getQuery("general");
        query.orderByDescending("DateString");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    progressDialog.dismiss();
//                    scoreList.addAll(scoreList);
//                    scoreList.addAll(scoreList);
                ArrayList<ParseObject> newList=new ArrayList<ParseObject>();

                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
                    final  Calendar cal = Calendar.getInstance();

                    cal.add(Calendar.DATE, -7);
                    for(int i=0;i<scoreList.size();i++){
                        try {
                            if (simpleDateFormat.parse(scoreList.get(i).get("DateString").toString()).after(simpleDateFormat.parse(simpleDateFormat.format(cal.getTime()))) && scoreList.get(i).get("status").toString().equalsIgnoreCase(AppConstants.APPROVED)) {
                                newList.add(scoreList.get(i));
                            }

                        }catch (Exception ee){
                            ee.printStackTrace();
                        }
                    }

                    parseAdapter=new ProductAdapter(getActivity(),newList);
                    listView.setAdapter(parseAdapter);

                    SharedPreferences preferences = getActivity().getSharedPreferences("filter", getActivity().MODE_PRIVATE);
                    selectedCategory=preferences.getString("category",categoryValue);
                    selectedCity=preferences.getString("city",cityValue);
                    selectedAdvert=preferences.getString("advert",advertValue);


                        parseAdapter.filter("", selectedCategory, selectedCity,selectedAdvert);
                       listView.invalidate();



                } else {

                }
            }
        });
    }


    public class ProductAdapter extends BaseAdapter {

        Context context;

        LayoutInflater inflater;


        public ProductAdapter(Context context, List<ParseObject> sampleDataValue) {
            this.context = context;
            sampleData = sampleDataValue;
            arraylist = new ArrayList<ParseObject>();
            arraylist.addAll(sampleData);

        }



        public int getCount() {

            return sampleData.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {

            public TextView productTitle,txtCity,txtDate;
            public ImageView productThumbnail;

        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);



            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_row,parent,false);
                holder = new ViewHolder();

                holder.productTitle = (TextView) convertView.findViewById(R.id.productTitle);
                holder.txtCity = (TextView) convertView.findViewById(R.id.txtCity);
                holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
                holder.productThumbnail= (ImageView) convertView.findViewById(R.id.productThumbnail);




                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.productTitle.setText(sampleData.get(position).getString("AdsTitel"));
            holder.txtCity.setText(sampleData.get(position).getString("City"));
            holder.txtDate.setText(sampleData.get(position).getString("DateString"));
            ParseFile file=sampleData.get(position).getParseFile("ThumbnailImage");


            Glide.with(getActivity())
                    .load(file.getUrl())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.productThumbnail);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(), ProductDetailActivity.class);
                    intent.putExtra("product_name",sampleData.get(position).getString("AdsTitel")+"");
                    intent.putExtra("product_detail",sampleData.get(position).getString("AdsDescription")+"");
                    intent.putExtra("posted_date",sampleData.get(position).getString("DateString")+"");
                    intent.putExtra("seller_name",sampleData.get(position).getString("Name")+"");
                    intent.putExtra("mobile",sampleData.get(position).getNumber("Telefon")+"");
                    intent.putExtra("email",sampleData.get(position).getString("Epost")+"");
                    intent.putExtra("image_path",sampleData.get(position).getParseFile("ThumbnailImage").getUrl()+"");
                    intent.putExtra("post_data",sampleData.get(position).getNumber("post_code")+"" );
                    intent.putExtra("object_id",sampleData.get(position).getObjectId()+"" );
                    startActivity(intent);
                }
            });

            return convertView;
        }

        // Filter Class
        public void filter(String charText,String categoryText,String cityText,String advertText) {

            charText = charText.toLowerCase(Locale.getDefault());
            categoryText=categoryText.toLowerCase(Locale.getDefault());
            advertText=advertText.toLowerCase(Locale.getDefault());
            cityText=cityText.toLowerCase(Locale.getDefault());
            sampleData.clear();
            if (charText.length() == 0 && categoryText.equalsIgnoreCase("Select Product Type") && advertText.equalsIgnoreCase("Select Advert Type") && cityText.equalsIgnoreCase("Select City")) {
                sampleData.addAll(arraylist);

            } else {
                for (ParseObject st : arraylist) {
                    if (charText.length() != 0 && st.getString("AdsTitel").toLowerCase(Locale.getDefault()).contains(charText)) {
                        sampleData.add(st);
                    }
                    else if(st.getString("City").toLowerCase(Locale.getDefault()).contains(cityText)){
                        sampleData.add(st);
                    }
                    else if(st.getString("Advert").toLowerCase(Locale.getDefault()).contains(advertText)){
                        sampleData.add(st);
                    }
                    else  if(st.getString("Category").toLowerCase(Locale.getDefault()).contains(categoryText)){
                        sampleData.add(st);
                    } else {

                    }
                }
            }
            notifyDataSetChanged();
        }

    }




}
