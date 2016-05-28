package com.mario22gmail.license.nfc_project;


import android.content.Intent;
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
    private ArrayList<SecureNote> notes;

    public void InitializeCredentials(ArrayList<WebsitesCredentials> credentials )
    {
        this.credentials = credentials;
    }

    public void InitializeSecureNotes(ArrayList<SecureNote> notes)
    {
        SecureNote note1= new SecureNote("subiect1","mesaj1");
        SecureNote note2= new SecureNote("subiect2","mesaj2");
        SecureNote note3= new SecureNote("subiect3","mesaj3");


        ArrayList<SecureNote> secureNotes = new ArrayList<SecureNote>(4);
        secureNotes.add(note1);
        secureNotes.add(note2);
        secureNotes.add(note3);
        this.notes = secureNotes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_card_content, container, false);

        Intent setTitleIntent = new Intent("fragment.setTitle");
        setTitleIntent.putExtra("Title","Conținut card");
        NavigationDrawerActivity.getAppContext().sendBroadcast(setTitleIntent);




        final TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Site-uri web"));
        tabLayout.addTab(tabLayout.newTab().setText("Mesaje"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)rootView.findViewById(R.id.pager);
        final TabsViewPager adapter = new TabsViewPager
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        adapter.InitializeCredentials(credentials);
        InitializeSecureNotes(null);
        adapter.InitializeSecureNotes(notes);
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

    @Override
    public void onStart() {
        Intent intentShowFab = new Intent("fragment.showFabButton");
        intentShowFab.putExtra("showFab",true);
        NavigationDrawerActivity.getAppContext().sendBroadcast(intentShowFab);
        super.onStart();
    }

    @Override
    public void onStop() {
        Intent intentShowFab = new Intent("fragment.showFabButton");
        intentShowFab.putExtra("showFab",false);
        NavigationDrawerActivity.getAppContext().sendBroadcast(intentShowFab);
        super.onStop();
    }
}
