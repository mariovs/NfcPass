package com.mario22gmail.license.nfc_project;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
//import android.nfc.NdefMessage;
//import android.nfc.NdefMessage;
//import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;

import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.nxp.nfclib.NxpNfcLib;
import com.nxp.nfclib.Nxpnfclibcallback;
import com.nxp.nfclib.desfire.DESFireEV1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Locale;

import com.nxp.nfclib.desfire.DESFireEV1.AuthType;
import com.nxp.nfclib.desfire.IDESFireEV1;
import com.nxp.nfclib.classic.IMFClassicEV1;
import com.nxp.nfclib.exceptions.CloneDetectedException;
import com.nxp.nfclib.exceptions.ReaderException;
import com.nxp.nfclib.exceptions.SmartCardException;
import com.nxp.nfclib.keystore.common.IKeyConstants;
import com.nxp.nfclib.keystore.common.IKeyStore;
import com.nxp.nfclib.keystore.common.KeyStoreFactory;
import com.nxp.nfclib.ndef.NdefMessageWrapper;
import com.nxp.nfclib.ndef.NdefRecordWrapper;
import com.nxp.nfclib.utils.Utilities;
//import com.nxp.nfcliblite.NxpNfcLibLite;
//import com.nxp.nfcliblite.Nxpnfcliblitecallback;
//import com.nxp.nfclib.cards.IDESFireEV1;

import static com.mario22gmail.license.nfc_project.R.id.nfcText;

public class MainActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;
    public static String EXTRA_MESSAGE = "com.mario22gmail.vasile.nfc_writer.MESSAGE";
    private boolean isNfcReady = false;
    EditText txtTagContent;
    Switch switchReadWrite;
    EditText nfcEdit;
    EditText passText;
    private WebView mWebview;
    public String js;
    private boolean mIsPerformingCardOperations = false;
    private NxpNfcLib libInstance = null;
    private IKeyStore ks = null;
    private IDESFireEV1 card;
    public static final byte[] KEY_DEFAULT_FF = {(byte) 0xFF, (byte) 0xFF,
            (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    public static final byte[] KEY_DEFAULT_DES = {(byte) 0x01, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

    public static final byte[] KEY_2KTDES = {(byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    public byte[] masterKey = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    public String fbUrl = "www.facebook.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Log.d("Mario_MainActivity", "OnCreate");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        libInstance = NxpNfcLib.getInstance();
//        libInstance.registerActivity(this, "5d882d0704faa5f1b424e0e4281079ec");

        byte[] ksByteArray = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        try {
            ks = KeyStoreFactory.getInstance().getSoftwareKeyStore();

            ks.formatKeyEntry(0, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_AES128);
            ks.setKey(0, (byte) 0, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_AES128, ksByteArray);

            ks.formatKeyEntry(1, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_DES);
            ks.setKey(1, (byte) 0,
                    IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_DES,
                    KEY_DEFAULT_DES);

            ks.formatKeyEntry(2, IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_2K3DES);
            ks.setKey(2, (byte) 0,
                    IKeyConstants.KeyType.KEYSTORE_KEY_TYPE_2K3DES, KEY_2KTDES);
        } catch (SmartCardException e) {
            e.printStackTrace();
        }


        libInstance.loadKeyStore(ks);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        switchReadWrite = (Switch) findViewById(R.id.switchReadWrite);
        txtTagContent = (EditText) findViewById(R.id.txtTagContent);
        nfcEdit = (EditText) findViewById(nfcText);
        passText = (EditText) findViewById(R.id.passText);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Mario_MainActivity", "OnStart");


    }

    protected void onDestroy() {
//        mWebview.clearCache(true);
//        mWebview.clearHistory();
//        mWebview.clearFormData();
        Log.d("Mario_MainActivity", "OnDestroy");

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Mario_MainActivity", "OnResume");

        enableForegroundDispatchSystem();
        if (isNfcReady == false) {
            Button nfcButton = (Button) findViewById(R.id.NfcReady);
            nfcButton.setText("Disconected");
            nfcButton.setBackgroundColor(102);
        } else {
            Button nfcReadyButton = (Button) findViewById(R.id.NfcReady);
            nfcReadyButton.setBackgroundColor(61523);
            nfcReadyButton.setText("Connected");
        }

    }


    private Nxpnfclibcallback mCallback = new Nxpnfclibcallback() {
        @Override
        public void onClassicEV1CardDetected(IMFClassicEV1 imfClassicEV1) {

        }


        @Override
        public void onDESFireEV1CardDetected(IDESFireEV1 objDESFire) {
            //objDESFire.
            // card1 =objDESFire;
//            DESFireEV1 card =
//            DESFireFactory factory =DESFireFactory.getInstance();
//            factory.getDESFire(objDESFire,null);
//
//            DESFireEV1 cardDesFire = new DESFireEV1(objDESFire) ;
//
//            objDESFire.
//            cardDesFire.getCardDetails()
//            cardDesFire = objDESFire;
            if (mIsPerformingCardOperations) {
                //Already Some Operations are happening in the same card, discard the callback
                Log.i("XX_INFO_XX", "----- Already Some Operations are happening in the same card, discard the callback: ");
                return;
            }
            mIsPerformingCardOperations = true;
            try {
                card = objDESFire;

                card.getReader().connect();
                card.getReader().setTimeout(5000);
                Log.i("XXXX_Card Detected_XXXX", "Card Detected : " + card.getCardDetails().cardName);


                card.authenticate(AuthType.Native, 0, (byte) 0, 0,
                        (byte) 0, null);
                //  card.getKeySettings().
//                card.MKNO_DES_2K3DES;
                Log.i("XXX_MARIO_CARD_XXX", "autentificat");

                card.selectApplication(0x00);
                Log.i("XXX_MARIO_CARD_XXX", "aplicatie 0 selectata");

                // card.selectApplication(0x12);
                Log.i("XXX_MARIO_CARD_XXX", "aplicatie 12 selectata");
                byte unByte = (byte) (0x00 | DESFireEV1.KSONE_CONFIG_CHANGABLE
                        | DESFireEV1.KSONE_FILE_DEL_NO_MKEY
                        | DESFireEV1.KSONE_GET_NO_MKEY | DESFireEV1.KSONE_APP_MKEY_CHANGABLE);
//                card
//                        .createApplication(
//                                0x11,
//                                (byte) (0x00 | DESFireEV1.KSONE_CONFIG_CHANGABLE
//                                        | DESFireEV1.KSONE_FILE_DEL_NO_MKEY
//                                        | DESFireEV1.KSONE_GET_NO_MKEY | DESFireEV1.KSONE_APP_MKEY_CHANGABLE),
//                                2, DESFireEV1.KeyType.TWOK3DES);
//                Log.i("XXX_MARIO_CARD_XXX", "aplicatie creata");
                card.selectApplication(0x11);
                Log.i("XXX_MARIO_CARD_XXX", "aplicatie 11 selectata");
//                card.createFile(0, new DESFireFile.StdDataFileSettings(
//                        CommunicationType.Plain, 0, 0, 0, 0, 1024));

                card.authenticate(AuthType.Native, 2, (byte) 0, 0,
                        (byte) 0, null);
//
//                card.writeData(0, 0, new byte[]{0x11, 0x11, 0x11, 0x11,
//                        0x11});
                String textCard = "Mario e tare";
                byte[] textBytes = textCard.getBytes();
                card.writeData(0, 0, textBytes);
                byte[] readBytes = card.readData(0, 0, 0);
                String readText = new String(readBytes);
                Log.i("XXX_Mari_Read_XXX", readText);


                Log.i("XX_Mario_Read__XXX", Utilities.dumpBytes(card.readData(0, 0, 0)));


//                card.authenticate(AuthType.Native, 2, (byte) 0, 0,
//                        (byte) 0, null);
//                Log.i("XXX_MARIO_CARD_XXX", "card autentificat");

//                card.changeKey(0, 2, (byte) 0, 2, (byte) 0, DESFireEV1.KeyType.TWOK3DES,
//
//                        IKeyConstants.DIV_OPTION_NODIVERSIFICATION,
//
//                        IKeyConstants.DIV_OPTION_NODIVERSIFICATION, null);
                //            Log.i("XXX_MARIO_CARD_XXX", "schimbat");

//                card.authenticate(AuthType.Native, 2, (byte) 0, 0,
//                        (byte) 0, null);
//                Log.i("XXX_MARIO_CARD_XXX", "card autentificat");
//                card.getReader().setTimeout(3000);

//                Log.i("XXX_MARIO_CARD_XXX", " " + card.isT4T());
//                card.getReader().close();

                // card.getKeyVersion(0);
//                card.changeKey();


                // card.getReader().close();
                // card.formatT4T(1024, 0, (byte) 0, 1, (byte) 0);
                //Log.i("XXX_MARIO_CARD_XXX", "formatat T4T");

//

//                card.formatPiccKeySetting(card.KSONE_APP_MKEY_CHANGABLE);
//                Log.i("XXX_MARIO_CARD_XXX", "formatat");
//                card.formatPiccKeySetting(card.KSONE_APP_MKEY_CHANGABLE);
//                Log.i("XXX_MARIO_CARD_XXX", "formatat");
//                card.formatPiccKeySetting(card.KSONE_APP_MKEY_CHANGABLE);
//                Log.i("XXX_MARIO_CARD_XXX", "formatat");
//                card.formatPiccKeySetting(card.KSONE_APP_MKEY_CHANGABLE);
//                Log.i("XXX_MARIO_CARD_XXX", "formatat");

                //card.commitTransaction();

//                byte[] piccKey = card.getVersion();
//                Log.i("XXX_CARD_XXX","Genereaza Picc");

//                for(int i = 0 ; i <= piccKey.length; i++)
//                {
//                    Log.i("XXX_APP_ID_XXX", "Picc Key: " + piccKey[i]);
//                }

//                int[] aplicationIds=card.getApplicationIDs();
//                for(int i = 0 ; i <= aplicationIds.length; i++)
//                {
//                    Log.i("XXX_APP_ID_XXX", "Aplication: " + aplicationIds[i]);
//                }
//                Log.i("XXXX_Card Detected_XXXX", "Card Detected : " + card.getCardDetails().cardName);
//                //card.deleteApplication(0);

//


//                card.isT4T()
//                card.format();
                // Log.i("XXX_MARIO_CARD_XXX", "card formatat");

//                Log.i("XXX_MARIO_CARD_XXX", "aplicatie 11 selectata");
////                card.createFile(0, new DESFireFile.StdDataFileSettings(
////                        CommunicationType.Plain, 0, 0, 0, 0, 1024));
//
//                card.authenticate(AuthType.Native, 2, (byte) 0, 0,
//                        (byte) 0, null);

//                card.writeData(0, 0, new byte[]{0x11, 0x11, 0x11, 0x11,
//                        0x11});
//                Log.i("XX_Mario_Read__XXX", Utilities.dumpBytes(card.readData(0, 0, 5)));
//                Log.i("XX_Mario_Read__XXX", " " + card.getCardDetails().freeMemory);


//
//                card.selectApplication(0x12);
//                Log.i("XXX_MARIO_CARD_XXX", "aplicatie 12 selectata");
//                card
//                        .createApplication(
//                                0x11,
//                                (byte) (0x10 | DESFireEV1.KSONE_CONFIG_CHANGABLE
//                                        | DESFireEV1.KSONE_FILE_DEL_NO_MKEY
//                                        | DESFireEV1.KSONE_GET_NO_MKEY | DESFireEV1.KSONE_APP_MKEY_CHANGABLE),
//                                2, DESFireEV1.KeyType.TWOK3DES);
//                Log.i("XXX_MARIO_CARD_XXX", "aplicatie creata");

                // card.getReader().close();
//                DESFireEV1.CardDetails details = card.getCardDetails();
//                Log.i("XXX_MARIO_CARD_Name_XXX",Integer.toString(details.totalMemory));
//                Log.i("XXX_CARD_Free_Mem_XXX", Integer.toString(details.freeMemory));
//
//                card.authenticate(AuthType.Native, 2, (byte) 0, 0,
//                        (byte) 0, null);
//                Log.i("XXX_MARIO_CARD_XXX", "autentificat");
//
//
////                card.selectApplication(0);
//                Log.i("XXX_MARIO_CARD_XXX", "selected");

//                card.authenticate(AuthType.Native, 2, (byte) 0, 0,
//                        (byte) 0, null);
                // card.format();
//

//                DESFireFile.FileSettings settings = card.getFileSettings(0);
//                Log.i("XXX_MARIO_CARD_XXX", "setarile  sunt " +settings.toString());


//                card
//                        .createApplication(
//                                iAppId,
//                                (byte) (0x10 | DESFireEV1.KSONE_CONFIG_CHANGABLE
//                                        | DESFireEV1.KSONE_FILE_DEL_NO_MKEY
//                                        | DESFireEV1.KSONE_GET_NO_MKEY | DESFireEV1.KSONE_APP_MKEY_CHANGABLE),
//                                2, DESFireEV1.KeyType.TWOK3DES);
//                Log.i("XXX_MARIO_CARD_XXX", "aplicatie creata");

//
//                card.selectApplication(iAppId);
//
//                card.createFile(0, new DESFireFile.StdDataFileSettings(
//                        CommunicationType.Plain, 0, 0, 0, 0, 1024));
//
//                card.authenticate(AuthType.Native, 2, (byte) 0, 0,
//                        (byte) 0, null);
//
//                card.writeData(0, 0, new byte[]{0x11, 0x11, 0x11, 0x11,
//                        0x11});
//                Log.i("XX_Mario_Read__XXX", Utilities.dumpBytes(card.readData(0, 0, 5)));
//                        "Data Read from the card..."
//                                + Utilities.dumpBytes(objDESFireEV1.readData(0, 0,
//                                5)), 'd');
                // Utilities.dumpBytes(card.getVersion());
//                Log.i("XXX_MARIO_CARD_XXX", Utilities.dumpBytes(card.getVersion()));
//
//                Log.i("XXX_MARIO_CARD_XXX", Utilities.dumpBytes(card.getCardUID()));
//                Log.i("XXX_MARIO_CARD_XXX", "Finish");


//                DESFireEV1 myCard;
//                myCard.
//                card.


                // Log.i("XXX_MARIO_CARD_XXX", "autentificat");

                //testDESFireupdatePICCMasterKey
//                byte[] oldKey = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
//                byte[] newKey = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
//                card.updatePICCMasterKey(oldKey, newKey);
//                Log.i("XXX_MARIO_CARD_XXX", "piccMatsterKey");

                //card.format(mykey);
//                Log.i("XXX_MARIO_CARD_XXX", "formatat");

                //testDESFireupdateApplicationMasterKey
//                 byte[] oldKey = new byte[] { 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
//                 byte[] newKey = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
//
//                 masterKey = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
//                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
//
//                byte[] appId2 = { 0x12, 0x12, 0x12 };
//                card.updateApplicationMasterKey(masterKey, appId2, oldKey,
//                        newKey);
//                Log.i("XXX_MARIO_CARD_XXX", "MASTERKEY");

                //

//               // testDESFireauthenticate();
//            } catch (ReaderException e) {
//                Log.i("XX_DES Reader_ERROR_XX", e.getMessage());
            } catch (SmartCardException e) {
                Log.i("XX_DES Card_Error_XX", e.getMessage());

            } catch (IOException e) {
                Log.i("XX_DES Card_IO_XX", e.getMessage());

                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                Log.i("XX_DES Card_Security_XX", e.getMessage());
                e.printStackTrace();
            } catch (ReaderException e) {
                e.printStackTrace();
            }
//            catch (GeneralSecurityException e) {
//
//                Log.i("XX_DES Card_Security_XX", e.getMessage());
//                e.printStackTrace();
//            } catch (IOException e) {
//                Log.i("XX_DES Card_IO_XX", e.getMessage());
//                e.printStackTrace();
//            }
            //Log.i("Des Fire DEtectat","Mario des fier detectat");
            //Toast.makeText(,"NfcIntent",Toast.LENGTH_SHORT).show();
//            Button button = (Button) findViewById(R.id.NfcReady);
//            button.setText("Disconected");
            mIsPerformingCardOperations = false;
        }
    };

    private void testDESFireauthenticate() {
        byte[] masterKey = new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte[] appId = {0x12, 0x12, 0x12};
        byte[] appkey = new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

        boolean res = false;
        try {
            // card.authenticate(masterKey, appId, appkey);
            res = true;
            Log.i("XXXX_CARD_DEtails_XXXXX", "Authenticate: " + card.getCardDetails().cardName.toString());
        } catch (SmartCardException e) {
            Log.i("XXXX_DESFIRE ERROR_XXXX", "Authenticate: " + e.getMessage());
            e.printStackTrace();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Mario_MainActivity", "OnPause");

        disableForegroundDispatchSystem();
    }


    @Override
    protected void onNewIntent(final Intent intent) {

//        Vibrator vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        long[] duration = { 50, 100, 200, 300 };
        //vib.vibrate(duration, -1);

        // MifareUltralight.get(tag)
        try {
            //tv.setText(" ");
            Button button = (Button) findViewById(R.id.NfcReady);
            button.setText("Connected");
            Log.i("Des Fire DEtectat", "Mario inca nu ");
            libInstance.filterIntent(intent, mCallback);
        } catch (CloneDetectedException e) {
            Toast.makeText(this, "Error_with_warning", Toast.LENGTH_SHORT).show();
            //showMessage("Unknown Error Tap Again!", 't');
        } catch (Exception e) {
            Log.i("XXX_CARD_ERROR_XXX", "Exceptia este" + e.getMessage());
        }
    }


    //Pentru a testa functionalitatea
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//
//        if(intent.hasExtra(NfcAdapter.EXTRA_TAG))
//        {
//            Toast.makeText(this,"NfcIntent",Toast.LENGTH_SHORT).show();
//
//
//            if(switchReadWrite.isChecked())
//            {
//                Parcelable[] parcelables =  intent.getParcelableArrayExtra(nfcAdapter.EXTRA_NDEF_MESSAGES);
//                if(parcelables != null && parcelables.length > 0)
//                {
//
//                    readTextFromTag((NdefMessage)parcelables[0]);
//
//
//                }else {
//                    Toast.makeText(this,"No NDEF Message Found",Toast.LENGTH_SHORT).show();
//
//                }
//
//            }else {
//                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//                String textForWrite = nfcEdit.getText() + "&&&" + passText.getText();
//                NdefMessage ndefMessage = createNdefMessage(textForWrite);
//                writeNdefMessage(tag,ndefMessage);
//            }
//
//        }
//
//
//    }


    //fosta metoda pentru button incarca o pagina web intr-un webview am sters butonul
    //am lasat metoda pentru a vedea cum incarci java script in web view
    public void navToWeb(View view) {
        //Uri uri =Uri.parse("http://facebook.com");
        //Intent intent = new  Intent(Intent.ACTION_VIEW,uri);
        //Intent intent = new Intent(this,DisplayMessageActivity.class);


        mWebview = (WebView) findViewById(R.id.webView);
        String url = "https://www.facebook.com";
        String user = "mario";
        String pwd = "password";
        js = "javascript:document.getElementsByName('email')[0].value = '" + user + "';document.getElementsByName('pass')[0].value='" + pwd + "';";
        // mWebview.setWebViewClient(new WebViewController());
        // mWebview.getSettings().setJavaScriptEnabled(true);
        //mWebview.loadUrl(url);

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




    //methoda care deschide facebook si umple casutele cu username si password
    //am folosito in metoda de readtxt ceva de genu nu o lasa acolo
    public void OpenFacebook(String userName, String password) {
        mWebview = (WebView) findViewById(R.id.webView);
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

//    private void injectJS() {
//        try {
//            InputStream inputStream = getAssets().open("jscript.js");
//            byte[] buffer = new byte[inputStream.available()];
//            inputStream.read(buffer);
//            inputStream.close();
//            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
//            String user="mario";
//            String pwd="pass";
//            mWebview.loadUrl("javascript:document.getElementById('Email').value = '" + user + "';document.getElementById
//('Passwd').value='" + pwd + "';");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void readTextFromTag(NdefMessageWrapper ndefMessage) {
        NdefRecordWrapper[] records = ndefMessage.getRecords();
        if (records != null && records.length > 0) {
            NdefRecordWrapper ndefRecord = records[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            //txtTagContent.setText(tagContent);
            String[] itemstFromCard = tagContent.split("&&&");
            if (itemstFromCard.length == 2) {
                OpenFacebook(itemstFromCard[0], itemstFromCard[1]);

            } else {
                Toast.makeText(this, "No Ndef Records", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "No Ndef Records", Toast.LENGTH_SHORT).show();
        }
    }

    public String getTextFromNdefRecord(NdefRecordWrapper ndefRecord) {
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
        return tagContent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
//            Toast.makeText(MainActivity.this,"Salut baaa",Toast.LENGTH_LONG).show();
            String js = "<script>javascript:document.getElementsByName('email')[0].value = '" + "mario" + "';document.getElementsByName('pass')[0].value='" + "ceva" +
                    "';document.getElementsByName('login')[0].click(); </script>";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));

            intent.putExtra(Intent.EXTRA_HTML_TEXT,js);

            if(intent.resolveActivity(getPackageManager()) != null )
            {
                intent.putExtra("javascript",js);
                startActivity(intent);
            }
            Snackbar.make(findViewById(nfcText), "Ceau", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void enableForegroundDispatchSystem() {

        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }


    private void disableForegroundDispatchSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

//    private void formatTag(Tag tag, NdefMessage ndefMessage)
//    {
//        try
//        {
//            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
//
//            if(ndefFormatable == null)
//            {
//                Toast.makeText(this,"Tag is not ndef formatable !!!",Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            ndefFormatable.connect();
//            ndefFormatable.format(ndefMessage);
//            ndefFormatable.close();
//        }catch (Exception e)
//        {
//            Log.e("format tag",e.getMessage());
//        }
//    }

//    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage)
//    {
//        try
//        {
//            if(tag == null)
//            {
//                Toast.makeText(this,"Tag cannot be null",Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Ndef ndef =Ndef.get(tag);
//            if(ndef == null)
//            {
//                formatTag(tag,ndefMessage);
//            }
//            else
//            {
//                ndef.connect();
//
//
//                if(!ndef.isWritable())
//                {
//                    Toast.makeText(this,"Tag is not writable" , Toast.LENGTH_SHORT).show();
//                    ndef.close();
//                    return;
//                }
//
//                ndef.writeNdefMessage(ndefMessage);
//                ndef.close();
//
//                Toast.makeText(this,"Tag writen" , Toast.LENGTH_SHORT).show();
//            }
//        }catch(Exception e)
//        {
//            Log.e("Write Ndef", e.getMessage());
//        }
//    }

    private NdefRecordWrapper createTextRecord(String content) {
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

            return new NdefRecordWrapper(android.nfc.NdefRecord.TNF_WELL_KNOWN, android.nfc.NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("create text record", e.getMessage());

        }
        return null;
    }

    private NdefMessageWrapper createNdefMessage(String content) {
        NdefRecordWrapper ndefRecord = createTextRecord(content);

        NdefMessageWrapper ndefMessage = new NdefMessageWrapper(new NdefRecordWrapper[]{ndefRecord});

        return ndefMessage;
    }


    public void ClickMeHandler(View view) {
        EditText username = (EditText) findViewById(R.id.nfcText);
        EditText password = (EditText) findViewById(R.id.passText);
        Snackbar.make(view, "UserName: " + username.getText() + " Password: " + password.getText(), Snackbar.LENGTH_LONG).show();
    }
}

