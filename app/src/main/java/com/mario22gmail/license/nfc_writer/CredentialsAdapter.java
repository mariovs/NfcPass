package com.mario22gmail.license.nfc_writer;

/**
 * Created by Mario Vasile on 3/29/2016.
 */



import android.content.Intent;
import android.net.Credentials;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CredentialsAdapter extends RecyclerView.Adapter<CredentialsAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private ArrayList<WebsitesCredentials> mDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.

//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d("nfc_debug", "Element " + getPosition() + " clicked.");
//
//                }
//            });
            Button button = (Button) v.findViewById(R.id.buttonNavigareWebsite);
            button.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Log.d("nfc_debug", "Element " + getPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }



    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CredentialsAdapter(ArrayList<WebsitesCredentials> dataSet) {
        mDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        Button button = (Button) viewHolder.itemView.findViewById(R.id.buttonNavigareWebsite);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebsitesCredentials credential =mDataSet.get(position);
                Intent myIntent = new Intent("start.fragment.action");
                myIntent.putExtra("myCredentials",credential);
                NavigationDrawerActivity.getAppContext().sendBroadcast(myIntent);

            }
        });
        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        viewHolder.getTextView().setText(mDataSet.get(position).getUserName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}