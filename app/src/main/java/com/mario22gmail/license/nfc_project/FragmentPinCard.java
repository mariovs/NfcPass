package com.mario22gmail.license.nfc_project;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPinCard extends Fragment {

    com.github.clans.fab.FloatingActionButton fab;
    public FragmentPinCard() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        Intent myIntent = new Intent("fragment.setTitle");
        myIntent.putExtra("Title","Second Fragment");
//        NavigationDrawerActivity.getAppContext().sendBroadcast(myIntent);
        LocalBroadcastManager.getInstance(NavigationDrawerActivity.getAppContext()).sendBroadcast(myIntent);

//        fab = (com.github.clans.fab.FloatingActionButton) getActivity().findViewById(R.id.fab);
//        fab.show(true);
        return inflater.inflate(R.layout.fragment_pin_card, container, false);
    }
    

    @Override
    public void onStart() {
        super.onStart();
//        fab.show(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        fab.show(true);
    }

    
    
    @Override
    public void onStop() {
        super.onStop();
//        fab.hide(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        fab.hide(true);
    }

}
