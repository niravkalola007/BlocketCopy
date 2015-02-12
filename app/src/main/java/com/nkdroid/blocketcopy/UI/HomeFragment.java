package com.nkdroid.blocketcopy.UI;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nkdroid.blocketcopy.R;
import com.nkdroid.blocketcopy.custom.MyCustomAdapter;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    RecyclerView recyclerView;

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
        recyclerView= (RecyclerView)convertView.findViewById(R.id.my_recycler_view);

        // improve performance if you know that changes in content
        // do not change the size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        // Data set used by the adapter. This data will be displayed.
        ArrayList<String> myDataset = new ArrayList<String>();
        for (int i= 0; i < 70; i++){
            myDataset.add("Sample " + i);
        }

        // Create the adapter
        RecyclerView.Adapter adapter = new MyCustomAdapter(getActivity(), myDataset);
        recyclerView.setAdapter(adapter);
        return convertView;
    }



}
