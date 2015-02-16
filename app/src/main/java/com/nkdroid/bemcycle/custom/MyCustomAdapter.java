package com.nkdroid.bemcycle.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nkdroid.bemcycle.R;
import com.nkdroid.bemcycle.UI.ProductDetailActivity;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.ViewHolder> {

    private static List<ParseObject> mDataset;
    private static List<ParseObject> mDatasetFiltered;
    private static Context sContext;

    public MyCustomAdapter(Context context, List<ParseObject> myDataset) {
        mDataset = myDataset;
        sContext = context;
        this.mDatasetFiltered = new ArrayList<ParseObject>();
        this.mDatasetFiltered.addAll(mDataset);

    }

    // Create new views. This is invoked by the layout manager.
    @Override
    public MyCustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);

        // Set the view to the ViewHolder
        ViewHolder holder = new ViewHolder(v);

//        holder.mNameTextView.setOnLongClickListener(MyCustomAdapter.this);

//        holder.productTitle.setTag(holder);

        return holder;
    }

    // Replace the contents of a view. This is invoked by the layout manager.
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.productTitle.setText(mDataset.get(position).getString("AdsTitel"));
        holder.txtCity.setText(mDataset.get(position).getString("City"));
        holder.txtDate.setText(mDataset.get(position).getString("DateString"));
        ParseFile file=mDataset.get(position).getParseFile("ThumbnailImage");


            Glide.with(sContext)
                    .load(file.getUrl())
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .into(holder.productThumbnail);





    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    // Implement OnClick listener. The clicked item text is displayed in a Toast message.
//    @Override
//    public void onClick(View view) {
//        ViewHolder holder = (ViewHolder) view.getTag();
//        if (view.getId() == holder.mNameTextView.getId()) {
//            Toast.makeText(sContext, holder.mNameTextView.getText(), Toast.LENGTH_SHORT).show();
//        }
//    }

//    // Implement OnLongClick listener. Long Clicked items is removed from list.
//    @Override
//    public boolean onLongClick(View view) {
//        ViewHolder holder = (ViewHolder) view.getTag();
//        if (view.getId() == holder.mNameTextView.getId()) {
//            mDataset.remove(holder.getPosition());
//
//            // Call this method to refresh the list and display the "updated" list
//            notifyDataSetChanged();
//
//            Toast.makeText(sContext, "Item " + holder.mNameTextView.getText() + " has been removed from list",
//                    Toast.LENGTH_SHORT).show();
//        }
//        return false;
//    }

    // Create the ViewHolder class to keep references to your views
    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        public TextView productTitle,txtCity,txtDate;
        public ImageView productThumbnail;

        /**
         * Constructor
         * @param v The container view which holds the elements from the row item xml
         */
        public ViewHolder(View v) {
            super(v);

            productTitle = (TextView) v.findViewById(R.id.productTitle);
            txtCity = (TextView) v.findViewById(R.id.txtCity);
            txtDate = (TextView) v.findViewById(R.id.txtDate);
            productThumbnail= (ImageView) v.findViewById(R.id.productThumbnail);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent=new Intent(sContext, ProductDetailActivity.class);
            intent.putExtra("product_name",mDataset.get(getPosition()).getString("AdsTitel")+"");
            intent.putExtra("product_detail",mDataset.get(getPosition()).getString("AdsDescription")+"");
            intent.putExtra("posted_date",mDataset.get(getPosition()).getString("DateString")+"");
            intent.putExtra("seller_name",mDataset.get(getPosition()).getString("Name")+"");
            intent.putExtra("mobile",mDataset.get(getPosition()).getNumber("Telefon")+"");
            intent.putExtra("email",mDataset.get(getPosition()).getString("Epost")+"");
            intent.putExtra("image_path",mDataset.get(getPosition()).getParseFile("ThumbnailImage").getUrl()+"");
            sContext.startActivity(intent);
        }


    }


    public  void myFilterData(String charText) {


//        receivedString = charText;

        charText = charText.toLowerCase(Locale.getDefault());
        mDataset.clear();
        if (charText.length() == 0) {
            mDataset.addAll(mDatasetFiltered);

        } else {
            for (ParseObject st : mDatasetFiltered) {
                if (st.getString("Name").toLowerCase(Locale.getDefault()).contains(charText)) {
                    mDataset.add(st);
                }
            }
        }
        notifyDataSetChanged();
    }
}