package com.mario22gmail.license.nfc_project;

/**
 * Created by Mario Vasile on 3/29/2016.
 */



import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Credentials;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        private final TextView urlTextMainView;
        private final TextView userNameTextVie;
        private final ImageView imageViewWebItem;


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
            imageViewWebItem = (ImageView) v.findViewById(R.id.imageViewWebItem);
            urlTextMainView = (TextView) v.findViewById(R.id.textViewUrl);
            userNameTextVie = (TextView) v.findViewById(R.id.secondTextCredentialItem);

        }

        public TextView getUrlMainTextView() {
            return urlTextMainView;
        }
        public TextView getUserNameTextView()
        {
            return userNameTextVie;
        }
        public ImageView getImageViewWebItem()
        {
            return imageViewWebItem;
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

        ImageView navigateIcon = (ImageView) viewHolder.itemView.findViewById(R.id.navigateWebFromItem);
        navigateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebsitesCredentials credential =mDataSet.get(position);
                Intent myIntent = new Intent("start.fragment.action");
                myIntent.putExtra("myCredentials",credential);
                NavigationDrawerActivity.getAppContext().sendBroadcast(myIntent);

            }
        });
        viewHolder.getUserNameTextView().setText(mDataSet.get(position).getUserName());
        setImageForCredential(viewHolder, mDataSet.get(position));
    }

    public void setImageForCredential(ViewHolder viewHolder, WebsitesCredentials credential)
    {
        String url = credential.getUrl();
        switch (url){
            case  WebSitesConstants.Facebook:
                    viewHolder.getUrlMainTextView().setText("Facebook");
                    viewHolder.getImageViewWebItem().setImageResource(R.drawable.facebook);
                break;
            case WebSitesConstants.Dropbox:
                viewHolder.getUrlMainTextView().setText("Dropbox");
                viewHolder.getImageViewWebItem().setImageResource(R.drawable.dropbox);
                break;
            case WebSitesConstants.Twitter:
                viewHolder.getUrlMainTextView().setText("Twitter");
                viewHolder.getImageViewWebItem().setImageResource(R.drawable.twitter);
                break;
            case WebSitesConstants.MySpace :
                viewHolder.getUrlMainTextView().setText("Myspace");
                viewHolder.getImageViewWebItem().setImageResource(R.drawable.myspace);
                break;
            case WebSitesConstants.LinkedIn:
                viewHolder.getUrlMainTextView().setText("LinkedIn");
                viewHolder.getImageViewWebItem().setImageResource(R.drawable.linkedin);
                break;
            case WebSitesConstants.Gmail:
                viewHolder.getUrlMainTextView().setText("Gmail");
                viewHolder.getImageViewWebItem().setImageResource(R.drawable.gmail);
                break;
            case WebSitesConstants.Instagram:
                viewHolder.getUrlMainTextView().setText("Instagram");
                viewHolder.getImageViewWebItem().setImageResource(R.drawable.instagram);
                break;
            default:
                viewHolder.getUrlMainTextView().setText(url);
                viewHolder.getImageViewWebItem().setImageResource(R.drawable.browser_default);
                break;
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}