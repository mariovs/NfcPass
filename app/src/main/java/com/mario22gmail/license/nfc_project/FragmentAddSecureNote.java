package com.mario22gmail.license.nfc_project;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddSecureNote extends Fragment {


    public FragmentAddSecureNote() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent myIntent = new Intent("fragment.setTitle");
        myIntent.putExtra("Title","Adauga mesaj");
        NavigationDrawerActivity.getAppContext().sendBroadcast(myIntent);
        return inflater.inflate(R.layout.fragment_add_secure_note, container, false);
    }

}
