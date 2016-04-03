package com.mario22gmail.license.nfc_writer;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Credentials;
import android.nfc.NfcAdapter;
import android.nfc.TagLostException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nxp.nfclib.NxpNfcLib;
import com.nxp.nfclib.Nxpnfclibcallback;
import com.nxp.nfclib.classic.IMFClassicEV1;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static Context context;
    NfcAdapter nfcAdapter;
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    private NxpNfcLib libInstance = null;
    private IDESFireEV1 card;
    private IKeyStore ks = null;
    private boolean mIsPerformingCardOperations = false;
    private final String nfcDebugTag = "nfc_debug";
    private final Handler handler = new Handler();
    private String js = "";

    public static final byte[] KEY_2KTDES = {(byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public static final byte[] MY_KEY_2KTDES ={(byte) 0x01, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    private SecondFragment secondFragment=new SecondFragment();
    private boolean isCardEmpty = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NavigationDrawerActivity.context = getApplicationContext();

        setContentView(R.layout.navigation_drawer);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);



        //inregistrez contextul si filtru
        context.registerReceiver(mBroadcastReceiver, new IntentFilter("start.fragment.action"));
        //set the main fragment
        MainFragment mainFragment = new MainFragment();
        ChangeFragment(mainFragment);




        libInstance = NxpNfcLib.getInstance();
        libInstance.registerActivity(this, "1b9f530ff2277ac77a257c9e497f2aaa");

        try {
            ks = KeyStoreFactory.getInstance().getSoftwareKeyStore();
            ks.formatKeyEntry(2, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_2K3DES);
            ks.setKey(2, (byte) 0,
                    IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_2K3DES, KEY_2KTDES);

            ks.formatKeyEntry(3, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_DES);
            ks.setKey(3, (byte) 0, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_DES, MY_KEY_2KTDES);

            toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //This piece of code will be executed when you click on your item
            // Call your fragment...
            WebsitesCredentials credential = (WebsitesCredentials)intent.getSerializableExtra("myCredentials");
            Log.i(nfcDebugTag,"A ajuns aici");
            js = "javascript:document.getElementsByName('email')[0].value = '" + credential.getUserName() + "';document.getElementsByName('pass')[0].value='" + credential.getPassword() +
                    "';document.getElementsByName('login')[0].click();";

            BrowserFragment fragment = new BrowserFragment();
            fragment.InitString(js);
            ChangeFragment(fragment);
        }
    };

    public void ChangeFragment(Fragment fragment)
    {
        Log.i(nfcDebugTag,"Change fragment method");
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_left_animation, R.anim.slide_right_animation, R.anim.slide_left_back_animation, R.anim.slide_right_back_animation);
        fragmentTransaction.replace(R.id.FragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);

        fragmentTransaction.commit();

    }

    public static Context getAppContext() {
        return NavigationDrawerActivity.context;
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatchSystem();
        Log.i(nfcDebugTag, "Is On Resume");


    }

    @Override
    protected void onPause() {
        super.onPause();
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
        if(getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {

            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(nfcDebugTag, "Tag detected ");
        isCardEmpty=false;
        try {
            libInstance.filterIntent(intent, mCallback);
            if(isCardEmpty)
            {
                FacebookCredentials Fragment = new FacebookCredentials();
                ChangeFragment(Fragment);
            }
            else
            {
                secondFragment = new SecondFragment();
                ChangeFragment(secondFragment);
            }

        } catch (CloneDetectedException e) {
            Toast.makeText(this, "Error_with_warning", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.i(nfcDebugTag, "Exceptia este" + e.getMessage());
        }
    }


    ///Meniu navigare
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            MainFragment mainFragment = new MainFragment();
           ChangeFragment(mainFragment);
        } else if (id == R.id.nav_gallery) {
            secondFragment = new SecondFragment();
            ChangeFragment(secondFragment);

        } else if (id == R.id.nav_slideshow) {
            FacebookCredentials fbFragment = new FacebookCredentials();
            ChangeFragment(fbFragment);

        } else if (id == R.id.nav_manage) {
            ChooseOptions chooseOptions = new ChooseOptions();
            ChangeFragment(chooseOptions);

        } else if (id == R.id.nav_share) {
            FragmentOptiuni optiuni = new FragmentOptiuni();
            ChangeFragment(optiuni);

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Nfc Logic
    private Nxpnfclibcallback mCallback = new Nxpnfclibcallback() {
        @Override
        public void onClassicEV1CardDetected(IMFClassicEV1 imfClassicEV1) {

        }


        @Override
        public void onDESFireEV1CardDetected(IDESFireEV1 objDESFire) {

//            if (mIsPerformingCardOperations) {
//                //Already Some Operations are happening in the same card, discard the callback
//                Log.i("XX_INFO_XX", "----- Already Some Operations are happening in the same card, discard the callback: ");
//                return;
//            }
//            mIsPerformingCardOperations = true;
//            try {
            card = objDESFire;
            try{
//                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0, (byte) 0, null);
//                Log.i(nfcDebugTag, "Applicatie authentificata");
                card.selectApplication(11);
                Log.i(nfcDebugTag, "Applicatia 11 selectata");
                isCardEmpty =false;

            }
            catch (DESFireException exceptie)
            {
                Log.i(nfcDebugTag,"DesFire " + exceptie.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
                Log.i(nfcDebugTag, "IO " + e.getMessage());

            } catch (SmartCardException e) {
                e.printStackTrace();
                Log.i(nfcDebugTag, "smart card ex " + e.getMessage());

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


//region Click handlers
    public void EnterButtonClick(View view) throws ReaderException, GeneralSecurityException, IOException, SmartCardException
    {
        if (card != null) {
            try {
                Log.i(nfcDebugTag, "Card Detected din buton : " + card.getCardDetails().cardName);
                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                        (byte) 0, null);
                Log.i(nfcDebugTag, "Applicatie authentificata");
                card.selectApplication(11);
                card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
                        (byte) 0, null);
                Log.i(nfcDebugTag, "Applicatie authentificata");
                card.getReader().setTimeout(5000);

                FragmentOptiuni optiuni = new FragmentOptiuni();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_left_animation, R.anim.slide_right_animation,R.anim.slide_left_back_animation,R.anim.slide_right_back_animation);
                fragmentTransaction.replace(R.id.FragmentContainer, optiuni);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();




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
//                ChooseOptions chooseFragment = new ChooseOptions();
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


    public void WebsiteAuthenticationFragmentClick(View view)
    {
        ArrayList<WebsitesCredentials> credentials = null;
        try {
            credentials = GetWebsitesFromDesfire();
        } catch (IOException e) {
            Log.i(nfcDebugTag,"io exception" + e.getMessage());
            e.printStackTrace();
        } catch (SmartCardException e) {
            Log.i(nfcDebugTag,"smart card exception" + e.getMessage());
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            Log.i(nfcDebugTag,"general security exception" + e.getMessage());
            e.printStackTrace();
        }

        ChooseOptions chooseOptions = new ChooseOptions();
        chooseOptions.initDataset(credentials);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_left_animation, R.anim.slide_right_animation,R.anim.slide_left_back_animation,R.anim.slide_right_back_animation);
        fragmentTransaction.replace(R.id.FragmentContainer, chooseOptions);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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

    public void ReadFromFileButton(View view) {
        if (card != null) {
            try {
                String text = DesFireReadFromCard(card);
                EditText textField = (EditText) findViewById(R.id.editTextForHandler);
                textField.setText(text);

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


    public void GoToFacebookCredentialsFragmentButton(View view) {
        FacebookCredentials fbCredentialsFragment = new FacebookCredentials();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.FragmentContainer, fbCredentialsFragment);
        fragmentTransaction.commit();
    }

    public void WriteFbCredentialsOnTagButton(View view) {
        EditText userNameTextBox = (EditText) findViewById(R.id.userNameFb);
        String userName = userNameTextBox.getText().toString();
        Log.i(nfcDebugTag, userName);

        EditText passTextBox = (EditText) findViewById(R.id.passwordFb);
        String pass = passTextBox.getText().toString();
        Log.i(nfcDebugTag, pass);



        if (userName != "" && pass != "") {
            if (card != null) {
                try {
//                    card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
//                            (byte) 0, null);
//                    Log.i(nfcDebugTag, "Applicatie authentificata");
//                    card.selectApplication(0);
//                    Log.i(nfcDebugTag, "Applicatia 0 selectata");
//                    DesFireCreateApplication(card, 11);
                    WebsitesCredentials credentials = new WebsitesCredentials();
                    credentials.setUrl("https://www.facebook.com");
                    credentials.setUserName(userName);
                    credentials.setPassword(pass);
                    WriteCredentials(credentials);
                    Snackbar.make(view, "Tag Writen", Snackbar.LENGTH_LONG);
                    Log.i(nfcDebugTag, "fisier scris");

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
            Snackbar.make(view, "User name or pass empty", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    public void NavToFbButton(View view) {
        if (card != null) {
            try {
                String textFromTag = DesFireReadFromCard(card);
                String[] itemstFromCard = textFromTag.split("@@@");
                if (itemstFromCard.length == 2) {
                    BrowserFragment fragment = new BrowserFragment();
                    js = "javascript:document.getElementsByName('email')[0].value = '" + itemstFromCard[0] + "';document.getElementsByName('pass')[0].value='" + itemstFromCard[1] +
                            "';document.getElementsByName('login')[0].click();";
                    fragment.InitString(js);
                    android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.FragmentContainer, fragment);
                    fragmentTransaction.commit();
                    //OpenFacebook(itemstFromCard[0], itemstFromCard[1]);
                }

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SmartCardException e) {
                e.printStackTrace();
            }

        }
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
            filesIdBytes = card.getFileIDs();
            int lastIndexFromFiles = filesIdBytes.length;
            Log.i(nfcDebugTag, "Index fisier" + lastIndexFromFiles);


            String textForCard = credentials.getUrl() + "@@@" + credentials.getUserName() + "@@@" + credentials.getPassword();
            byte[] textBytes = textForCard.getBytes();

            card.createFile(lastIndexFromFiles, new DESFireFile.StdDataFileSettings(
                    DESFireEV1.CommunicationType.Plain, 0, 0, 0, 0, textBytes.length));
            Log.i(nfcDebugTag, "fisier creat");

//             write data to file nr 1
//            card.authenticate(DESFireEV1.AuthType.Native, 2, (byte) 0, 0,
//                    (byte) 0, null);
//            Log.i(nfcDebugTag, "Applicatie authentificata");
            card.writeData(lastIndexFromFiles, 0, textBytes);
            Log.i(nfcDebugTag, "fisier scris");


        }
    }


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