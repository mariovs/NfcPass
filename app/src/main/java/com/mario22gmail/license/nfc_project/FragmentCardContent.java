package com.mario22gmail.license.nfc_project;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCardContent extends Fragment {


    public FragmentCardContent() {
        // Required empty public constructor
    }

    private ArrayList<WebsitesCredentials> credentials ;

    public void InitializeCredentials(ArrayList<WebsitesCredentials> credentials )
    {
        this.credentials = credentials;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_card_content, container, false);

        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbarNavigationDrawer);
//


        TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Site-uri web"));
        tabLayout.addTab(tabLayout.newTab().setText("Fisiere"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.pager);
        final TabsViewPager adapter = new TabsViewPager
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        adapter.InitializeCredentials(credentials);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return rootView;
    }

}
