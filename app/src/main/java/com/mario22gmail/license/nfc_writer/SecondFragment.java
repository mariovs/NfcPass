package com.mario22gmail.license.nfc_writer;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {

    FloatingActionButton fab;
    public SecondFragment() {
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
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.show();
        return inflater.inflate(R.layout.fragment_second, container, false);
    }
    

    @Override
    public void onStart() {
        super.onStart();
        fab.show();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        fab.show();
    }

    
    
    @Override
    public void onStop() {
        super.onStop();
        fab.hide();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fab.hide();
    }


}
