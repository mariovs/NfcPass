package com.mario22gmail.license.nfc_project;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmptyFragment extends Fragment {


    public EmptyFragment() {
        // Required empty public constructor
    }

    private FloatingActionMenu floatingMenu ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        floatingMenu = (FloatingActionMenu)getActivity().findViewById(R.id.menuFab);
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }

    @Override
    public void onStart() {

        floatingMenu.showMenu(true);
        super.onStart();
    }

    @Override
    public void onPause() {
        floatingMenu.hideMenu(true);
        super.onPause();
    }

    @Override
    public void onResume() {
        floatingMenu.showMenu(true);
        super.onResume();
    }

    @Override
    public void onStop() {
        floatingMenu.hideMenu(true);
        super.onStop();
    }
}
