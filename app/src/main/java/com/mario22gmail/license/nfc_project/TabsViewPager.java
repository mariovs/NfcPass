package com.mario22gmail.license.nfc_project;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Mario Vasile on 4/17/2016.
 */
public class TabsViewPager extends FragmentStatePagerAdapter{
    int mNumOfTabs;

    public TabsViewPager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    private ArrayList<WebsitesCredentials> credentials ;

    public void InitializeCredentials(ArrayList<WebsitesCredentials> credentials )
    {
        this.credentials = credentials;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                 if(credentials != null && credentials.size()>0)
                 {
                     ChooseOptions chooseOptions = new ChooseOptions();
                     chooseOptions.initDataset(credentials);
                     return chooseOptions;
                 }
                else
                 {
                     EmptyFragment fragment = new EmptyFragment();
                     return fragment;
                 }
            case 1:
                    EmptyFragment tab2 = new EmptyFragment();
                    return tab2;
            default:
                EmptyFragment tab3 = new EmptyFragment();
                return tab3;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
