package com.mario22gmail.license.nfc_project;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Mario Vasile on 4/17/2016.
 */
public class TabsViewPager extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabsViewPager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    private ArrayList<WebsitesCredentials> credentials;
    private ArrayList<SecureNote> secureNotes;

    public void InitializeCredentials(ArrayList<WebsitesCredentials> credentials) {
        this.credentials = credentials;
    }

    public void InitializeSecureNotes(ArrayList<SecureNote> notes) {
        this.secureNotes = notes;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if (credentials != null && credentials.size() > 0) {
                    FragmentWebCredentialsOnCard fragmentWebCredentialsOnCard = new FragmentWebCredentialsOnCard();
                    fragmentWebCredentialsOnCard.initDataset(credentials);
                    return fragmentWebCredentialsOnCard;
                } else {
                    FragmentEmptyState fragment = new FragmentEmptyState();
                    return fragment;
                }
            case 1:
                if (secureNotes != null && secureNotes.size() > 0) {
                    FragmentSecureNotes fragmentSecureNotes = new FragmentSecureNotes();
                    fragmentSecureNotes.initDataSetSecureNote(secureNotes);
                    return fragmentSecureNotes;

                } else {
                    FragmentEmptyState emptyState = new FragmentEmptyState();
                    return emptyState;
                }

            default:
                FragmentEmptyState tabDefault = new FragmentEmptyState();
                return tabDefault;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
