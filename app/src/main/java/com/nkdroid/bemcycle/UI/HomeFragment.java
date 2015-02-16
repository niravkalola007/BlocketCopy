package com.nkdroid.bemcycle.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.nkdroid.bemcycle.R;
import com.nkdroid.bemcycle.custom.MyCustomAdapter;
import com.nkdroid.bemcycle.custom.SharedPreferenceAdvertType;
import com.nkdroid.bemcycle.custom.SharedPreferenceCityType;
import com.nkdroid.bemcycle.custom.SharedPreferenceProductTypes;
import com.nkdroid.bemcycle.model.AdvertType;
import com.nkdroid.bemcycle.model.CityType;
import com.nkdroid.bemcycle.model.ProductType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;


    private SharedPreferenceProductTypes sharedPreferenceProductTypes;
    private SharedPreferenceAdvertType sharedPreferenceAdvertType;
    private SharedPreferenceCityType sharedPreferenceCityType;
    SwipeRefreshLayout swipeView;
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
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        sharedPreferenceProductTypes=new SharedPreferenceProductTypes();
        sharedPreferenceAdvertType=new SharedPreferenceAdvertType();
        sharedPreferenceCityType=new SharedPreferenceCityType();
        sharedPreferenceProductTypes.clearProductTypes(getActivity());
        sharedPreferenceAdvertType.clearAdvertType(getActivity());
        sharedPreferenceCityType.clearCitytype(getActivity());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("product_category");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {

                    for(int i=0;i<scoreList.size();i++){

                        sharedPreferenceProductTypes.saveProductTypes(getActivity(),new ProductType(scoreList.get(i).getString("ProductCode"), scoreList.get(i).getString("ProductName")));
                    }

                    ParseQuery<ParseObject> query1 = ParseQuery.getQuery("advert_type_category");
                    query1.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> scoreList, ParseException e) {
                            if (e == null) {

                                for(int i=0;i<scoreList.size();i++){

                                    sharedPreferenceAdvertType.saveAdvertType(getActivity(),new AdvertType(scoreList.get(i).getString("AdvertCode"), scoreList.get(i).getString("AdvertName")));
                                }

                                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("city_category");
                                query2.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> scoreList, ParseException e) {
                                        if (e == null) {

                                            for(int i=0;i<scoreList.size();i++){

                                                sharedPreferenceCityType.saveCityType(getActivity(),new CityType(scoreList.get(i).getString("CityCode"), scoreList.get(i).getString("CityName")));
                                            }

                                            progressDialog.dismiss();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView= (RecyclerView)convertView.findViewById(R.id.my_recycler_view);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        swipeView= (SwipeRefreshLayout) convertView.findViewById(R.id.swipe);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);


                // Create the adapter
                ParseQuery<ParseObject> query = ParseQuery.getQuery("general");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> scoreList, ParseException e) {
                        if (e == null) {
                            swipeView.setRefreshing(false);

                            RecyclerView.Adapter adapter = new MyCustomAdapter(getActivity(), scoreList);
                            recyclerView.setAdapter(adapter);

                        } else {

                        }
                    }
                });
            }
        });

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dx==0 ){
                    swipeView.setEnabled(true);
                } else {
                    swipeView.setEnabled(false);
                }
            }

        });
        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        recyclerView.setHasFixedSize(true);



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
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    progressDialog.dismiss();
                    scoreList.addAll(scoreList);
                    scoreList.addAll(scoreList);
                    RecyclerView.Adapter adapter = new MyCustomAdapter(getActivity(), scoreList);
                    recyclerView.setAdapter(adapter);

                } else {

                }
            }
        });
    }
}
