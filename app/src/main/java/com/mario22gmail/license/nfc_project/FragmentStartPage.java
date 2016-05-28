package com.mario22gmail.license.nfc_project;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentStartPage extends Fragment {


    public FragmentStartPage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        NavigationDrawerActivity activity = (NavigationDrawerActivity)getActivity();
//        activity.setTitleActionBar(" ");
        Intent myIntent = new Intent("fragment.setTitle");
        myIntent.putExtra("Title","Nfc Project");
        NavigationDrawerActivity.getAppContext().sendBroadcast(myIntent);
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start_page, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.nonemenu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }
}
