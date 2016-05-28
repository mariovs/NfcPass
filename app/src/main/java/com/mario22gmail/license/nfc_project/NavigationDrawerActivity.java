package com.mario22gmail.license.nfc_project;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.github.clans.fab.FloatingActionMenu;
import com.nxp.nfclib.NxpNfcLib;
import com.nxp.nfclib.Nxpnfclibcallback;
import com.nxp.nfclib.desfire.DESFireEV1;
import com.nxp.nfclib.desfire.DESFireFile;
import com.nxp.nfclib.desfire.IDESFireEV1;
import com.nxp.nfclib.exceptions.CloneDetectedException;
import com.nxp.nfclib.exceptions.DESFireException;
import com.nxp.nfclib.exceptions.ReaderException;
import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.keystore.common.IKeyConstants;
import com.nxp.nfclib.keystore.common.IKeyStore;
import com.nxp.nfclib.keystore.common.KeyStoreFactory;
import com.nxp.nfclib.utils.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Locale;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Context context;
    private NfcAdapter nfcAdapter;
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private boolean desfireCardNeedApp = false;
    private NxpNfcLib libInstance = null;
    private IDESFireEV1 card;
    private IKeyStore ks = null;
    private boolean mIsPerformingCardOperations = false;
    private final String nfcDebugTag = "nfc_debug";
    private final Handler handler = new Handler();
    private String js = "";

    final static private String APP_KEY = "";
    final static private String APP_SECRET = "";
    private DropboxAPI<AndroidAuthSession> mDBApi;

    public static final byte[] KEY_2KTDES = {(byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static final byte[] MY_KEY_2KTDES = {(byte) 0x01, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    private FragmentPinCard fragmentPinCard = new FragmentPinCard();
    private boolean isCardEmpty = true;
    private boolean isDesfire = false;
    private boolean isNFCDisabled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NavigationDrawerActivity.context = getApplicationContext();

        setContentView(R.layout.navigation_drawer);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);



        //inregistrez contextul si filtru

        //set the main fragment
//        FragmentStartPage mainFragment = new FragmentStartPage();
//        ChangeFragment(mainFragment);

//        FragmentEmptyState emptyFragment = new FragmentEmptyState();
//        ChangeFragment(emptyFragment);
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        libInstance = NxpNfcLib.getInstance();
        libInstance.registerActivity(this, "");

        try {
            ks = KeyStoreFactory.getInstance().getSoftwareKeyStore();
            ks.formatKeyEntry(2, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_2K3DES);
            ks.setKey(2, (byte) 0,
                    IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_2K3DES, KEY_2KTDES);

            ks.formatKeyEntry(3, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_DES);
            ks.setKey(3, (byte) 0, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_DES, MY_KEY_2KTDES);

            toolbar = (Toolbar) findViewById(R.id.toolbarNavigationDrawer);
            setSupportActionBar(toolbar);
        } catch (SmartCardException e) {
            e.printStackTrace();
        }


        libInstance.loadKeyStore(ks);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.menuFab);

        fabMenu.hideMenuButton(false);

        fabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                String text;
                if (opened) {

                    text = "Menu opened";
                } else {
                    text = "Menu closed";
                }
                Log.i(nfcDebugTag, text);

            }
        });

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fab.setLabelColors(ContextCompat.getColor(activity, Color.GRAY),
//                        fab.getColor()
//                        fab.getColor(activity, R.color.cardview_dark_background));
//                fab.setLabelTextColor(ContextCompat.getColor(activity, Color.BLACK));
//            }
//
//        });

//        fabMenu.hideMenuButton(false);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentStartPage startPage = new FragmentStartPage();
        ChangeFragment(startPage);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    //DEFAULT ANDROID METHODS
    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
        context.registerReceiver(showFabButton,new IntentFilter("fragment.showFabButton"));
        context.registerReceiver(goToCredentialsPageBroadCast,new IntentFilter("goToWebCredentialPage"));
        context.registerReceiver(logInWebsite, new IntentFilter("start.fragment.action"));
        context.registerReceiver(customActionBarTitle, new IntentFilter("fragment.setTitle"));
        context.registerReceiver(editWebCredential, new IntentFilter("editWebCredential"));
        context.registerReceiver(editWebCredentialConfirmation, new IntentFilter("editWebCredentialConfirmation"));
        context.registerReceiver(deleteWebCredential, new IntentFilter("deleteWebCredential"));
        if(!nfcAdapter.isEnabled())
        {
            FragmentEnableNFC fragment = new FragmentEnableNFC();
            fragment.show(getSupportFragmentManager(), "Enable NFC dialog");
        }
//        if (mDBApi.getSession().authenticationSuccessful()) {
//            try {
//                // Required to complete auth, sets the access token on the session
//                mDBApi.getSession().finishAuthentication();
//                Log.i(nfcDebugTag, "Dropbox autentificat");
//                String accessToken = mDBApi.getSession().getOAuth2AccessToken();
//            } catch (IllegalStateException e) {
//                Log.i("DbAuthLog", "Error authenticating", e);
//            }
//        }

        Log.i(nfcDebugTag, "Is On Resume");


    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(nfcDebugTag, "activity is on pause");
        context.unregisterReceiver(logInWebsite);
        context.unregisterReceiver(customActionBarTitle);
        context.unregisterReceiver(editWebCredential);
        context.unregisterReceiver(editWebCredentialConfirmation);
        context.unregisterReceiver(deleteWebCredential);
        context.unregisterReceiver(goToCredentialsPageBroadCast);
        context.unregisterReceiver(showFabButton);

        disableForegroundDispatchSystem();
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {

            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
            if (canback) {
                getFragmentManager().popBackStack();
            }
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    ///Meniu navigare
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main_page) {
            FragmentStartPage startPage = new FragmentStartPage();
            ChangeFragment(startPage);

//            FragmentEmptyState fragmentEmptyState = new FragmentEmptyState();
//            ChangeFragment(fragmentEmptyState);

        } else if (id == R.id.nav_add_note) {
//            fragmentPinCard = new FragmentPinCard();
            FragmentAddSecureNote fragmentAddSecureNote = new FragmentAddSecureNote();
            ChangeFragment(fragmentAddSecureNote);

        } else if (id == R.id.nav_browser) {
            FragmentBrowser browserFragment = new FragmentBrowser();
            ChangeFragment(browserFragment);

        }else if(id == R.id.nav_add_web_page) {
            FragmentOptionsAddSites addSitesFragment = new FragmentOptionsAddSites();
            ChangeFragment(addSitesFragment);

        } else if (id == R.id.nav_info) {
//            FragmentWebCredentialsOnCard chooseOptions = new FragmentWebCredentialsOnCard();
//            ChangeFragment(chooseOptions);
//            FragmentPinDialog fragment = new FragmentPinDialog();
//            fragment.show(getSupportFragmentManager(), "Mario popup");

            FragmentAbout fragmentAbout = new FragmentAbout();
            fragmentAbout.show(getSupportFragmentManager(),"Info popup");
//            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.setCustomAnimations(R.anim.popupanim, R.anim.popoutanim, R.anim.popupanim, R.anim.popoutanim);
//            fragmentTransaction.replace(R.id.FragmentContainer, fragment);
//            fragmentTransaction.addToBackStack(null);
//
//            fragmentTransaction.commit();
//            ChangeFragment(fragment);

        } else if (id == R.id.nav_exit) {
//            FragmentCardContent cardContent = new FragmentCardContent();
//            ChangeFragment(cardContent);
            this.finishAffinity();
        }
//        } else if (id == R.id.nav_send) {
//            FragmentOptionsAddSites fragmentOptiuni = new FragmentOptionsAddSites();
//            ChangeFragment(fragmentOptiuni);
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //BROADCAST RECIVER
    BroadcastReceiver customActionBarTitle = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(nfcDebugTag, "A ajuns si aici");
            String title = (String) intent.getSerializableExtra("Title");
            getSupportActionBar().setTitle(title);
//
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
////            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
//            getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
        }
    };

    BroadcastReceiver editWebCredentialConfirmation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(nfcDebugTag, "edit confirmation broadcast");
            try {
                if (card != null) {
                    card.selectApplication(11);

                    Log.i(nfcDebugTag, "Applicatia 11 selectata");
                    WebsitesCredentials credentialToWrite = (WebsitesCredentials) intent.getSerializableExtra("credential");

                    byte[] filesIdsByteArray = card.getFileIDs();
                    card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                            (byte) 0, null);
                    for (int i = 0; i < filesIdsByteArray.length; i++) {
                        byte[] readBytes = card.readData(filesIdsByteArray[i], 0, 0);
                        String readText = new String(readBytes);
                        Log.i(nfcDebugTag, readText + " " + i);
                        String[] credentialsFromCard = readText.split("@@@");
                        if (credentialsFromCard.length == 3) {
                            if (credentialsFromCard[0].equals(credentialToWrite.getUrl())
                                    && credentialsFromCard[1].equals(credentialToWrite.getUserName())) {
                                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                                        (byte) 0, null);
                                card.deleteFile(filesIdsByteArray[i]);
                                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                                        (byte) 0, null);
                                WriteCredentials(credentialToWrite);
                                Log.i(nfcDebugTag, "A fost inlocuit");
                                return;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DESFireException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (SmartCardException e) {
                e.printStackTrace();
            }
        }
    };

    BroadcastReceiver deleteWebCredential = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WebsitesCredentials credentialToDelete = (WebsitesCredentials) intent.getSerializableExtra("credential");
            Log.i(nfcDebugTag, "delete credential broadcast");
            try {
                if (card != null) {
                    card.selectApplication(11);

                    Log.i(nfcDebugTag, "Applicatia 11 selectata");

                    byte[] filesIdsByteArray = card.getFileIDs();
                    card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                            (byte) 0, null);
                    for (int i = 0; i < filesIdsByteArray.length; i++) {
                        byte[] readBytes = card.readData(filesIdsByteArray[i], 0, 0);
                        String readText = new String(readBytes);
                        Log.i(nfcDebugTag, readText + " " + i);
                        String[] credentialsFromCard = readText.split("@@@");
                        if (credentialsFromCard.length == 3) {
                            if (credentialsFromCard[0].equals(credentialToDelete.getUrl())
                                    && credentialsFromCard[1].equals(credentialToDelete.getUserName())) {
                                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                                        (byte) 0, null);
                                card.deleteFile(filesIdsByteArray[i]);
                                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                                        (byte) 0, null);
                                Log.i(nfcDebugTag, "A fost sters");
                                UpdatePageWithCredentials();
                                return;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DESFireException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (SmartCardException e) {
                e.printStackTrace();
            }

        }
    };


    BroadcastReceiver editWebCredential = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            WebsitesCredentials credential = (WebsitesCredentials) intent.getSerializableExtra("credential");
            Log.i(nfcDebugTag, "Edit broadcast signal");
            EditCredential(credential.getUrl(), credential.getUserName());
        }
    };

    BroadcastReceiver showFabButton = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean showFab = intent.getBooleanExtra("showFab",false);
            if(showFab)
            {
                FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.menuFab);
                fabMenu.showMenuButton(true);
            }
            else
            {
                FloatingActionMenu fabMenu = (FloatingActionMenu) findViewById(R.id.menuFab);
                fabMenu.hideMenuButton(true);
            }
        }
    };


    BroadcastReceiver logInWebsite = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //This piece of code will be executed when you click on your item
            // Call your fragment...
            WebsitesCredentials credential = (WebsitesCredentials) intent.getSerializableExtra("myCredentials");
            Log.i(nfcDebugTag, "A ajuns aici");
            String javaScriptforLogIn = GenerateJavascript(credential);
            if (!javaScriptforLogIn.equals("")) {
                FragmentBrowser fragment = new FragmentBrowser();
                fragment.InitString(javaScriptforLogIn);
                fragment.InitUrl(credential.getUrl());
                ChangeFragment(fragment);
            } else {
                Log.i(nfcDebugTag, "Javascript gol");
                Snackbar.make(findViewById(R.id.nav_view), "Credentiale incorecte", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    };

    BroadcastReceiver goToCredentialsPageBroadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            Log.i(nfcDebugTag,"A ajuns in broadcast");
            ArrayList<WebsitesCredentials> credentials = null;
            try {
                credentials = GetWebsitesFromDesfire();
            } catch (IOException e) {
                Log.i(nfcDebugTag, "io exception" + e.getMessage());
                e.printStackTrace();
            } catch (SmartCardException e) {
                Log.i(nfcDebugTag, "smart card exception" + e.getMessage());
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                Log.i(nfcDebugTag, "general security exception" + e.getMessage());
                e.printStackTrace();
            }
            FragmentCardContent fragmentCardContent = new FragmentCardContent();
            fragmentCardContent.InitializeCredentials(credentials);


            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_left_animation, R.anim.slide_right_animation, R.anim.slide_left_back_animation, R.anim.slide_right_back_animation);
            fragmentTransaction.replace(R.id.FragmentContainer, fragmentCardContent, fragmentCardContent.getClass().getName());
//            fragmentTransaction.addToBackStack(fragmentCardContent.getClass().getName());

            fragmentTransaction.commit();
//            ChangeFragment(fragmentCardContent);
        }
    };



    //UI INTERACTION
    public void ChangeFragment(Fragment fragment) {
        Log.i(nfcDebugTag, "Change fragment method");
//        ||
            FragmentManager fm = getSupportFragmentManager();

        if(fm.getBackStackEntryCount() == 0 ) {
            android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_left_animation, R.anim.slide_right_animation, R.anim.slide_left_back_animation, R.anim.slide_right_back_animation);
            fragmentTransaction.replace(R.id.FragmentContainer, fragment, fragment.getClass().getName());
            fragmentTransaction.addToBackStack(fragment.getClass().getName());

            fragmentTransaction.commit();
        }else
        {
            int lengthStack = getSupportFragmentManager().getFragments().size();
            Fragment lastFragment =  getSupportFragmentManager().getFragments().get(lengthStack - 1);
            boolean isFragmentLast = fragment.getClass().isInstance(lastFragment);
            if(!isFragmentLast)
            {
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_left_animation, R.anim.slide_right_animation, R.anim.slide_left_back_animation, R.anim.slide_right_back_animation);
                fragmentTransaction.replace(R.id.FragmentContainer, fragment, fragment.getClass().getName());
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
                fragmentTransaction.commit();

            }
        }
    }

    public static Context getAppContext() {
        return NavigationDrawerActivity.context;
    }


//NFC LOGIC

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(nfcDebugTag, "Tag detected ");
        Toast.makeText(this, "Card detectat", Toast.LENGTH_SHORT).show();

        isCardEmpty = true;
        try {
            libInstance.filterIntent(intent, mCallback);
//                FragmentPinDialog fragment = new FragmentPinDialog();
//                fragment.show(getSupportFragmentManager(), "Mario popup");
            if (isDesfire == false) {

                if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
                    Toast.makeText(this, "NfcIntent", Toast.LENGTH_SHORT).show();

                    Log.i(nfcDebugTag, "Tag detectat");

                    Parcelable[] parcelables = intent.getParcelableArrayExtra(nfcAdapter.EXTRA_NDEF_MESSAGES);
                    if (parcelables != null && parcelables.length > 0) {
                        readTextFromTag((NdefMessage) parcelables[0]);
                    } else {
                        Toast.makeText(this, "No NDEF Message Found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    String mesaj = "ce mai faci";
                    NdefMessage ndefMessage = createNdefMessage(mesaj);
                    writeNdefMessage(tag, ndefMessage);
                }
            }

            if (isCardEmpty) {
                FragmentEmptyState Fragment = new FragmentEmptyState();
                ChangeFragment(Fragment);
//            ChangeFragment(chooseOptions);
//                FragmentPinDialog fragment = new FragmentPinDialog();
//                fragment.show(getSupportFragmentManager(), "Mario popup");

            } else {
                Log.i(nfcDebugTag, "A ajuns pe else pana la fragment");

                int lengthStack = getSupportFragmentManager().getFragments().size();
                Fragment lastFragment =  getSupportFragmentManager().getFragments().get(lengthStack - 1);
                boolean isDialogFragment = FragmentPinDialog.class.isInstance(lastFragment);
                if(!isDialogFragment)
                {
                    FragmentPinDialog fragment = new FragmentPinDialog();
                    fragment.show(getSupportFragmentManager(), "Mario popup");
                }
            }
        } catch (CloneDetectedException e) {
            Toast.makeText(this, "Error_with_warning", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.i(nfcDebugTag, "Exceptia este" + e.getMessage());
        } finally {
            isDesfire = false;
        }
        Log.i(nfcDebugTag, "Nfc intent a ajuns la sfarsit");
    }


//DEFAULT NFC OPERATION

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if (tag == null) {
                Toast.makeText(this, "Tag cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                formatTag(tag, ndefMessage);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }
                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "Tag writen", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(nfcDebugTag, e.getMessage());
        }
    }

    //Nfc default read
    private void readTextFromTag(NdefMessage ndefMessage) {
        NdefRecord[] records = ndefMessage.getRecords();
        if (records != null && records.length > 0) {
            android.nfc.NdefRecord ndefRecord = records[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            Log.i(nfcDebugTag, "tag conent from read Text " + tagContent);
            Toast.makeText(this, tagContent, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No Ndef Records", Toast.LENGTH_SHORT).show();
        }
    }

    private NdefMessage createNdefMessage(String content) {
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");
            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);
            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(android.nfc.NdefRecord.TNF_WELL_KNOWN, android.nfc.NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("create text record", e.getMessage());
        }
        return null;
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable !!!", Toast.LENGTH_SHORT).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            Log.i(nfcDebugTag, "Tag formatat din format method");
            ndefFormatable.close();
        } catch (Exception e) {
            Log.e(nfcDebugTag, e.getMessage());
        }
    }

    public String getTextFromNdefRecord(android.nfc.NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        Log.i(nfcDebugTag, "nfc content " + tagContent);
        return tagContent;
    }


    //Nfc Desfire Logic
    private Nxpnfclibcallback mCallback = new Nxpnfclibcallback() {
        @Override
        public void onDESFireEV1CardDetected(IDESFireEV1 objDESFire) {

//            if (mIsPerformingCardOperations) {
//                //Already Some Operations are happening in the same card, discard the callback
//                Log.i("XX_INFO_XX", "----- Already Some Operations are happening in the same card, discard the callback: ");
//                return;
//            }
//            mIsPerformingCardOperations = true;
//            try {
            isDesfire = true;
            card = objDESFire;
            desfireCardNeedApp = false;
            try {

                int memoryAvailable = card.getFreeMem();
                Log.i(nfcDebugTag, "Memoria ramasa este " + memoryAvailable);

                card.selectApplication(11);
                Log.i(nfcDebugTag, "Applicatia 11 selectata");


//                card.selectApplication(0);
//                Log.i(nfcDebugTag, "Applicatia 0 selectata");
//                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0, (byte) 0, null);
//                Log.i(nfcDebugTag, "Applicatie authentificata");
//                card.format();
//                Log.i(nfcDebugTag,"Card Formatat");
                isCardEmpty = false;

            } catch (DESFireException exceptie) {
                Log.i(nfcDebugTag, "DesFire " + exceptie.getMessage());
                isCardEmpty = true;
                if (exceptie.getMessage().equals("invalidResponse: Application Not Found")) {
                    desfireCardNeedApp = true;
                }


            } catch (IOException e) {
                e.printStackTrace();
                Log.i(nfcDebugTag, "IO " + e.getMessage());
                isCardEmpty = true;

            } catch (SmartCardException e) {
                e.printStackTrace();
                Log.i(nfcDebugTag, "smart card ex " + e.getMessage());
                isCardEmpty = true;

            }
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    // upadte textView here
////                    EditText editTextHandler = (EditText) findViewById(R.id.editTextForHandler);
////                editTextHandler.setText("ceau");
////                    Button nfcState = (Button) findViewById(R.id.NfcStateButton);
//
//                    try {
//                        card.getReader().connect();
//                        card.getReader().close();
////                        nfcState.setText("Connected");
//                        Log.i(nfcDebugTag, "TagConnected");
//                    } catch (ReaderException e) {
//                        e.printStackTrace();
////                        nfcState.setText("Not Connected");
//                        String a;
//                        Log.i(nfcDebugTag, "TagDisconected");
//                    }
//
////                    Random rand = new Random();
////
////                    int  n = rand.nextInt(50) + 1;
////                    editTextHandler.setText(n + " ");
//                    handler.postDelayed(this, 1000); // set time here to refresh textView
//                }
//            });


//                card.getReader().connect();
//                card.getReader().setTimeout(5000);
//                Log.i(nfcDebugTag, "Card Detected : " + card.getCardDetails().cardName);
//
//
//                card.authenticate(DESFireEV1.AuthType.Native, 0, (byte) 0, 0,
//                        (byte) 0, null);
//
//
//                card.selectApplication(0x00);
//                Log.i(nfcDebugTag, "aplicatie 0 selectata");

            // card.selectApplication(0x12);
            // Log.i(nfcDebugTag, "aplicatie 12 selectata");

            //temporar
//                byte unByte = (byte) (0x00 | DESFireEV1.KSONE_CONFIG_CHANGABLE
//                        | DESFireEV1.KSONE_FILE_DEL_NO_MKEY
//                        | DESFireEV1.KSONE_GET_NO_MKEY | DESFireEV1.KSONE_APP_MKEY_CHANGABLE);


//                Log.i("XXX_MARIO_CARD_XXX", "aplicatie creata");


            //temporar

//                card.selectApplication(0x11);
//                Log.i(nfcDebugTag, "aplicatie 11 selectata");
////                card.createFile(0, new DESFireFile.StdDataFileSettings(
////                        CommunicationType.Plain, 0, 0, 0, 0, 1024));
//
//                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
//                        (byte) 0, null);


//
//                card.writeData(0, 0, new byte[]{0x11, 0x11, 0x11, 0x11,
//                        0x11});
//                String textCard = "Mario e tare";
//                byte[] textBytes = textCard.getBytes();
//                card.writeData(0, 0, textBytes);

            //temporar
//
//                Log.i(nfcDebugTag, readText);
//
//
//                Log.i(nfcDebugTag, Utilities.dumpBytes(card.readData(0, 0, 0)));


//            } catch (ReaderException e) {
//                Log.i("XX_DES Reader_ERROR_XX", e.getMessage());
//            } catch (SmartCardException e) {
//                Log.i(nfcDebugTag+" smart card e", e.getMessage());
//
//            } catch (IOException e) {
//                Log.i(nfcDebugTag + " IO e", e.getMessage());
//
//                e.printStackTrace();
//            } catch (GeneralSecurityException e) {
//                Log.i(nfcDebugTag + "security e", e.getMessage());
//                e.printStackTrace();
//            } catch (ReaderException e) {
//                Log.i(nfcDebugTag + "reader e", e.getMessage());
//                e.printStackTrace();
//            }
//            catch (GeneralSecurityException e) {
//
//                Log.i("XX_DES Card_Security_XX", e.getMessage());
//                e.printStackTrace();
//            } catch (IOException e) {
//                Log.i("XX_DES Card_IO_XX", e.getMessage());
//                e.printStackTrace();
//            }

        }
    };

    public void UpdatePageWithCredentials() throws GeneralSecurityException, SmartCardException, IOException {
        if (card != null) {
            ArrayList<WebsitesCredentials> credentials = null;
            credentials = GetWebsitesFromDesfire();
            FragmentCardContent fragmentCardContent = new FragmentCardContent();
            fragmentCardContent.InitializeCredentials(credentials);
            ChangeFragment(fragmentCardContent);
        }
    }

    private String DesFireReadFromCard(IDESFireEV1 card) throws GeneralSecurityException, IOException, SmartCardException {

        card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                (byte) 0, null);
        Log.i(nfcDebugTag, "Applicatie authentificata");
        card.selectApplication(2);
        Log.i(nfcDebugTag, "Applicatia 2 selectata");

        card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                (byte) 0, null);
        Log.i(nfcDebugTag, "Applicatie authentificata");
//        card.deleteFile(1);
//        card.deleteFile(2);
//        card.deleteFile(3);
//        card.deleteFile(4);

        byte[] readBytes = card.readData(1, 0, 0);
        String readText = new String(readBytes);
        Log.i(nfcDebugTag, readText);

        card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                (byte) 0, null);
        Log.i(nfcDebugTag, "Applicatie authentificata");
        Log.i(nfcDebugTag, Utilities.dumpBytes(card.readData(1, 0, 0)));

        return readText;
    }

    private void DesFireCreateApplication(IDESFireEV1 card, int applicationNumber) throws GeneralSecurityException, IOException, SmartCardException {

        card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                (byte) 0, null);
        Log.i(nfcDebugTag, "Tag Autentificat");
        card.createApplication(
                applicationNumber,
                (byte) (0x00 | DESFireEV1.KSONE_CONFIG_CHANGABLE
                        | DESFireEV1.KSONE_FILE_DEL_NO_MKEY
                        | DESFireEV1.KSONE_GET_NO_MKEY | DESFireEV1.KSONE_APP_MKEY_CHANGABLE),
                2, DESFireEV1.KeyType.TWOK3DES);
        card.selectApplication(applicationNumber);

    }


    private ArrayList<WebsitesCredentials> GetWebsitesFromDesfire() throws IOException, SmartCardException, GeneralSecurityException {
        ArrayList<WebsitesCredentials> websitesList = new ArrayList<WebsitesCredentials>();
        if (card != null) {
//            card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
//                    (byte) 0, null);
//            Log.i(nfcDebugTag, "Applicatie authentificata");
//            card.selectApplication(11);
//            Log.i(nfcDebugTag, "Applicatia 11 selectata");
//            card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
//                    (byte) 0, null);
//            Log.i(nfcDebugTag, "Applicatie authentificata");
            byte[] filesIdsByteArray = card.getFileIDs();
            for (int i = 0; i < filesIdsByteArray.length; i++) {
                byte[] readBytes = card.readData(filesIdsByteArray[i], 0, 0);
                String readText = new String(readBytes);
                Log.i(nfcDebugTag, readText + " " + i);
                String[] credentialsFromCard = readText.split("@@@");
                if (credentialsFromCard.length == 3) {
                    WebsitesCredentials credentials = new WebsitesCredentials(credentialsFromCard[0], credentialsFromCard[1], credentialsFromCard[2]);
                    websitesList.add(credentials);
                }
            }
        }
        return websitesList;
    }


    private void WriteCredentials(WebsitesCredentials credentials) throws IOException, SmartCardException, GeneralSecurityException {
        if (card != null) {
            byte[] filesIdBytes = new byte[0];
//            if(isCardEmpty)
//            {
//                card.selectApplication(0);
//                DesFireCreateApplication(card, 11);
//                isCardEmpty = true;
//            }
//            card.selectApplication(11);

            if (desfireCardNeedApp) {
                DesFireCreateApplication(card, 11);
            }

            filesIdBytes = card.getFileIDs();
            int lastIndexFromFiles = 0;
            if (filesIdBytes.length != 0) {
                for(int i = 0; i<filesIdBytes.length; i++)
                {
                    if(filesIdBytes[i]> lastIndexFromFiles)
                    {
                        lastIndexFromFiles = filesIdBytes[i];
                    }
                }
            }
            int indexForWrite = lastIndexFromFiles + 1;
            Log.i(nfcDebugTag, "Index fisier" + lastIndexFromFiles);


            String textForCard = credentials.getUrl() + "@@@" + credentials.getUserName() + "@@@" + credentials.getPassword();
            byte[] textBytes = textForCard.getBytes();

            card.createFile(indexForWrite, new DESFireFile.StdDataFileSettings(
                    DESFireEV1.CommunicationType.Plain, 0, 0, 0, 0, textBytes.length));
            Log.i(nfcDebugTag, "fisier creat");

//             write data to file nr 1
            card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                    (byte) 0, null);
            Log.i(nfcDebugTag, "Applicatie authentificata");
            card.writeData(indexForWrite, 0, textBytes);

            Log.i(nfcDebugTag, "fisier scris");


        }
    }

    public String GenerateJavascript(WebsitesCredentials credential) {
        String logInJs = "";
        switch (credential.getUrl()) {
            case WebSitesConstants.Facebook:
                logInJs = "javascript:if(document.getElementsByName('email')!= null){document.getElementsByName('email')[0].value = '"
                        + credential.getUserName() + "';}if(document.getElementsByName('pass')!= null){document.getElementsByName('pass')[0].value='"
                        + credential.getPassword() + "';}";
                break;
            case WebSitesConstants.Instagram:
                logInJs = "javascript:if(document.getElementsByClassName('_k6cv7')!= null){setTimeout(function(){ document.getElementsByClassName('_k6cv7')[0].click()},6000);}" +
                        "setTimeout(function(){" + " " +
                        "document.getElementsByName('username')[0].value = '" + credential.getUserName() + "';},8000);" +
                        "setTimeout(function(){if(document.getElementsByName('password')!= null){document.getElementsByName('password')[0].value='" + credential.getPassword() + "';}},8000);";
                break;
            case WebSitesConstants.LinkedIn:
                logInJs = "javascript: setTimeout(function(){if(document.getElementById('session_key-login')!= null){document.getElementById('session_key-login').value = '" + credential.getUserName() + "';}},2000);" +
                        "setTimeout(function(){if(document.getElementById('session_password-login')!= null){document.getElementById('session_password-login').value='" + credential.getPassword() + "';}},2000);";
                break;
            case WebSitesConstants.MySpace:
                logInJs = "javascript:setTimeout(function(){ if(document.getElementsByName('email')!= null){document.getElementsByName('email')[1].value = '" + credential.getUserName() + "';}},3000);" +
                        "setTimeout(function(){if(document.getElementsByName('password')!= null){document.getElementsByName('password')[1].value='" + credential.getPassword() + "';}},3000);";
                break;
            case WebSitesConstants.Twitter:
                logInJs = "javascript: setTimeout(function(){if(document.getElementById('session[username_or_email]')!= null){document.getElementById('session[username_or_email]').value = '" + credential.getUserName() + "';}},2000);" +
                        "setTimeout(function(){if(document.getElementById('session[password]')!= null){document.getElementById('session[password]').value='" + credential.getPassword() + "';}},2000);";
                break;
            case WebSitesConstants.Gmail:
                logInJs = "javascript: setTimeout(function(){if(document.getElementById('Email')!= null){document.getElementById('Email').value = '" + credential.getUserName() + "';}},2000);" +
                        "setTimeout(function(){if(document.getElementById('next') != null){document.getElementById('next').click();}},3000);" +
                        "setTimeout(function(){if(document.getElementById('Passwd')!= null){document.getElementById('Passwd').value='" + credential.getPassword() + "';}},5000);";
                break;
            case WebSitesConstants.Dropbox:
                logInJs = "javascript:setTimeout(function(){ if(document.getElementsByName('login_email')!= null){document.getElementsByName('login_email')[1].value = '' ; document.getElementsByName('login_email')[1].value = '" + credential.getUserName() + "';}},3000);" +
                        "setTimeout(function(){if(document.getElementsByName('login_password')!= null){document.getElementsByName('login_password')[1].value=''; document.getElementsByName('login_password')[1].value='" + credential.getPassword() + "';}},3000);";
                break;
        }
        return logInJs;
    }


    //region Click handlers
    public void EnterButtonClick(View view) throws ReaderException, GeneralSecurityException, IOException, SmartCardException {
        if (card != null) {
            try {
                mDBApi.getSession().startOAuth2Authentication(NavigationDrawerActivity.this);
                Log.i(nfcDebugTag, "Card Detected din buton : " + card.getCardDetails().cardName);
                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                        (byte) 0, null);
                Log.i(nfcDebugTag, "Applicatie authentificata");
                card.selectApplication(11);
                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                        (byte) 0, null);
                Log.i(nfcDebugTag, "Applicatie authentificata");
                card.getReader().setTimeout(5000);


                ArrayList<WebsitesCredentials> credentials = null;
                try {
                    credentials = GetWebsitesFromDesfire();
                } catch (IOException e) {
                    Log.i(nfcDebugTag, "io exception" + e.getMessage());
                    e.printStackTrace();
                } catch (SmartCardException e) {
                    Log.i(nfcDebugTag, "smart card exception" + e.getMessage());
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    Log.i(nfcDebugTag, "general security exception" + e.getMessage());
                    e.printStackTrace();
                }
                FragmentCardContent fragmentCardContent = new FragmentCardContent();
                fragmentCardContent.InitializeCredentials(credentials);
                ChangeFragment(fragmentCardContent);
                // DesFireCreateApplication(card, 4);
//               DesFireCreateApplication(card, 3);


//                String textCard = "Mario e tare";
//                byte[] textBytes = textCard.getBytes();


//                int[] appIds =  card.getApplicationIDs();
//
//                for(int i = 0; i < appIds.length; i++)
//                {
//                    Log.i(nfcDebugTag,appIds[i] + " ");
//                }


                //create file nr 1
//                card.createFile(1, new DESFireFile.StdDataFileSettings(
//                        DESFireEV1.CommunicationType.Plain, 0, 0, 0, 0, textBytes.length));
//                Log.i(nfcDebugTag, "fisier creat");

                //write data to file nr 1
//                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
//                        (byte) 0, null);
//                Log.i(nfcDebugTag, "Applicatie authentificata");
//                card.writeData(1, 0, textBytes);
//                Log.i(nfcDebugTag, "fisier scris");

//
//                card.selectApplication(0x00);
//                card.getReader().close();
//                Log.i(nfcDebugTag, "aplicatie 0 selectata din buton");
//                FragmentWebCredentialsOnCard chooseFragment = new FragmentWebCredentialsOnCard();
//                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.FragmentContainer, chooseFragment);
//                fragmentTransaction.commit();
            } catch (TagLostException e) {
                Log.i(nfcDebugTag, "Tag Lost" + e.getMessage());
                TextView errorLabel = (TextView) findViewById(R.id.textViewTagLostError);
                errorLabel.setTextColor(Color.RED);
                errorLabel.setText("Aproprie cardul nfc");

            } catch (DESFireException e) {
                Log.i(nfcDebugTag, "Desfire Exception" + e.getMessage());
            } catch (Exception e) {
                Log.i(nfcDebugTag, "Nu e card" + e + " ");
                Log.i(nfcDebugTag, "Nu e card " + e.getCause());
                TextView errorLabel = (TextView) findViewById(R.id.textViewTagLostError);
                errorLabel.setTextColor(Color.RED);
                errorLabel.setText("Aproprie cardul nfc");
            }


        }
    }


    public void WebsiteAuthenticationFragmentClick(View view) {
        ArrayList<WebsitesCredentials> credentials = null;
        try {
            credentials = GetWebsitesFromDesfire();
        } catch (IOException e) {
            Log.i(nfcDebugTag, "io exception" + e.getMessage());
            e.printStackTrace();
        } catch (SmartCardException e) {
            Log.i(nfcDebugTag, "smart card exception" + e.getMessage());
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            Log.i(nfcDebugTag, "general security exception" + e.getMessage());
            e.printStackTrace();
        }

        FragmentWebCredentialsOnCard fragmentWebCredentialsOnCard = new FragmentWebCredentialsOnCard();
        fragmentWebCredentialsOnCard.initDataset(credentials);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_left_animation, R.anim.slide_right_animation, R.anim.slide_left_back_animation, R.anim.slide_right_back_animation);
        fragmentTransaction.replace(R.id.FragmentContainer, fragmentWebCredentialsOnCard);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void AddWebSiteFragment(View view) {
        FragmentAddWebCredentials credentialsFragment = new FragmentAddWebCredentials();
        ChangeFragment(credentialsFragment);
    }

    public void NavigateCredentialsFragmentClick(View view) {
        FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.menuFab);
//        menu.toggle(true);
        menu.close(true);

        FragmentOptionsAddSites optionsFragment = new FragmentOptionsAddSites();
        ChangeFragment(optionsFragment);

    }

    public void NavigateToAddFileFragmentClick(View view) {
        FloatingActionMenu menu = (FloatingActionMenu) findViewById(R.id.menuFab);
        menu.close(true);

        FragmentAddFile fileFragment = new FragmentAddFile();
        ChangeFragment(fileFragment);

    }

    public void AddFileFabClick(View view) {

    }

    public void CreateApplicationButton(View view) {
        if (card != null) {
            try {
                DesFireCreateApplication(card, 2);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                Log.i(nfcDebugTag, "Create app security error" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(nfcDebugTag, "Create app io error" + e.getMessage());

            } catch (SmartCardException e) {
                e.printStackTrace();
                Log.i(nfcDebugTag, "Create app smart card error" + e.getMessage());

            }
        }
    }

    private final String passwordCardEncrypted = "1234";


    public AuthResponse AuthenticateOnDesfire(String passwordCard) {
        AuthResponse response = new AuthResponse();
        if (card != null) {

            try {
                if (passwordCard.equals(passwordCardEncrypted  )) {
                    card.selectApplication(11);
                    card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                            (byte) 0, null);
                    Log.i(nfcDebugTag, "Applicatie authentificata");
                    card.getReader().setTimeout(5000);
                    response.setIsValid(true);
                    return response;
                }
            } catch (IOException e) {
                e.printStackTrace();
                response.setIsValid(false);
                response.setErrorMessage(e.getMessage());
                return response;
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
                response.setIsValid(false);
                response.setErrorMessage(e.getMessage());
                return response;
            } catch (SmartCardException e) {
                e.printStackTrace();
                response.setIsValid(false);
                response.setErrorMessage(e.getMessage());
                return response;
            }
        } else
        {
            response.setErrorMessage("Card not found");
            response.setIsValid(false);
            return response;
        }
        return response;
    }


    public void WriteCredentialsOnTagButtonClick(View view) {
        EditText userNameTextBox = (EditText) findViewById(R.id.fragmentAddUserNameTextBox);
        String userName = userNameTextBox.getText().toString();
        Log.i(nfcDebugTag, userName);

        EditText passTextBox = (EditText) findViewById(R.id.fragmentAddWebPasswordTextBox);
        String pass = passTextBox.getText().toString();
        Log.i(nfcDebugTag, pass);

        EditText urlTextBox = (EditText) findViewById(R.id.fragmentAddWebUrlTextBox);
        String url = urlTextBox.getText().toString();
        Log.i(nfcDebugTag, url);


        if (!userName.equals("") && !pass.equals("") && !url.equals("")) {
            if (card != null) {
                try {
//                    card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
//                            (byte) 0, null);
//                    Log.i(nfcDebugTag, "Applicatie authentificata");
//                    card.selectApplication(0);
//                    Log.i(nfcDebugTag, "Applicatia 0 selectata");
//                    DesFireCreateApplication(card, 11);
                    WebsitesCredentials credentials = new WebsitesCredentials();
                    credentials.setUrl(url);
                    credentials.setUserName(userName);
                    credentials.setPassword(pass);
                    WriteCredentials(credentials);
                    Snackbar.make(view, "Tag Writen", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    Log.i(nfcDebugTag, "fisier scris");
                    UpdatePageWithCredentials();


//                    String textCard = "https://www.facebook.com" + "@@@" + userName + "@@@" + pass;
//                    byte[] textBytes = textCard.getBytes();
//
//                    card.createFile(1, new DESFireFile.StdDataFileSettings(
//                            DESFireEV1.CommunicationType.Plain, 0, 0, 0, 0, textBytes.length));
//                    Log.i(nfcDebugTag, "fisier creat");
//
//                    // write data to file nr 1
//                    card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
//                            (byte) 0, null);
//                    Log.i(nfcDebugTag, "Applicatie authentificata");
//                    card.writeData(1, 0, textBytes);

                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                    Log.i(nfcDebugTag, "Create app security error" + e.getMessage());

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(nfcDebugTag, "Create app io error" + e.getMessage());

                } catch (SmartCardException e) {
                    e.printStackTrace();
                    Log.i(nfcDebugTag, "Create app smart card error" + e.getMessage());

                }


            } else {
                Snackbar.make(view, "Tag not found", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        } else {
            Snackbar.make(view, "Campurile nu pot fi goale", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    public void FacebookCredentialsAddClick(View view) {
        AddDefaultCredentials(WebSitesConstants.Facebook);
    }

    public void InstagramCredentialsAddClick(View view) {
        AddDefaultCredentials(WebSitesConstants.Instagram);
    }

    public void LinkedInCredentialsAddClick(View view) {
        AddDefaultCredentials(WebSitesConstants.LinkedIn);
    }

    public void GmailCredentialsAddClick(View view) {
        AddDefaultCredentials(WebSitesConstants.Gmail);
    }

    public void DropboxCredentialsAddClick(View view) {
        AddDefaultCredentials(WebSitesConstants.Dropbox);
    }

    public void MyspaceCredentialsAddClick(View view) {
        AddDefaultCredentials(WebSitesConstants.MySpace);
    }

    public void TwitterCredentialsAddClick(View view) {
        AddDefaultCredentials(WebSitesConstants.Twitter);
    }

    public void AddDefaultWebsiteCredentialsClick(View view) {
        AddDefaultCredentials("");
    }

//endregion ClickHandlers


    public void OpenFacebook(String userName, String password) {
        WebView mWebview = (WebView) findViewById(R.id.webViewFb);
        String url = "https://www.facebook.com";
        js = "javascript:document.getElementsByName('email')[0].value = '" + userName + "';document.getElementsByName('pass')[0].value='" + password +
                "';document.getElementsByName('login')[0].click();";

        mWebview.loadUrl(url);
        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);


        mWebview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (Build.VERSION.SDK_INT >= 19) {
                    view.evaluateJavascript(js, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {

                        }
                    });
                } else {
                    view.loadUrl(js);
                }
            }
        });
    }

//Read Credentials from Desfire Card


    //region addWebsite Credentials
    public void AddDefaultCredentials(String defaultUrl) {
        FragmentAddWebCredentials fragment = new FragmentAddWebCredentials();
        fragment.SetDefaultUrl(defaultUrl);
        ChangeFragment(fragment);
    }


    public void EditCredential(String url, String userName) {
        FragmentAddWebCredentials fragment = new FragmentAddWebCredentials();
        fragment.SetDefaultUrl(url);
        fragment.SetUserName(userName);
        ChangeFragment(fragment);

    }


//endregion

    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, NavigationDrawerActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }


}
