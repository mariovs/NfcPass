package com.mario22gmail.license.nfc_project;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.CookieManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentBrowser extends Fragment {

    private String js ="";
    private String url = "";
    private WebView mWebview;
    private EditText urlEditText ;



    public void InitString(String text)
    {
        js = text;
    }

    public void InitUrl(String text)
    {
        url = text;
    }


    public FragmentBrowser() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_browser, container, false);
        mWebview = (WebView) v.findViewById(R.id.webViewFb);

        Intent myIntent = new Intent("fragment.setTitle");
        myIntent.putExtra("Title","Browser");
        NavigationDrawerActivity.getAppContext().sendBroadcast(myIntent);
        urlEditText = (EditText) v.findViewById(R.id.searchForGoogle);
        EditTextFocusChangeListner  textLisner = new EditTextFocusChangeListner(getContext(),R.id.searchForGoogle);
        urlEditText.setOnFocusChangeListener(textLisner);
        urlEditText.setSingleLine();
        urlEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm =  (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String query = urlEditText.getText().toString();
                    if(!query.isEmpty())
                    {

                        String url ="https://www.google.com/search?q=" + query;
                        mWebview.loadUrl(url);
                        mWebview.setWebViewClient(new WebViewClient() {
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }});
                    }
                }
                return true;
            }
        });

        final Button searchButton = (Button) v.findViewById(R.id.searchOnGoogleButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =  (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String query = urlEditText.getText().toString();
                if(!query.isEmpty())
                {
                    String url ="https://www.google.com/search?q=" + query;
                    mWebview.loadUrl(url);
                    mWebview.setWebViewClient(new WebViewClient() {
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            view.loadUrl(url);
                            return true;
                        }});
                }
            }
        });


        if(!js.equals("") && !url.equals(""))
        {
            OpenWebSite(url,js);
            Log.i("nfc_debug", "A trecut");
        }

        return v;
//        inflater.inflate(R.layout.fragment_browser, container, false);



    }

    public boolean MyCallBack()
    {
        return true;
    }


    @Override
    public void onPause() {
        Log.i("nfc_debug", "browser on pause");

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                // a callback which is executed when the cookies have been removed
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    Log.d("nfc_debug", "Cookie removed: " + aBoolean);
                }
            });
        }
        else cookieManager.removeAllCookie();

        mWebview.clearCache(true);
        mWebview.removeAllViews();
        mWebview.clearHistory();
        mWebview.clearFormData();

        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("nfc_debug", "browser on stop");
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                // a callback which is executed when the cookies have been removed
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    Log.d("nfc_debug", "Cookie removed: " + aBoolean);
                }
            });
        }
        else cookieManager.removeAllCookie();
        mWebview.clearCache(true);
        mWebview.clearFormData();
        mWebview.removeAllViews();
        mWebview.clearHistory();
        mWebview.loadUrl("www.google.com");

        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.i("nfc_debug","browser on stop");
        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                // a callback which is executed when the cookies have been removed
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    Log.d("nfc_debug", "Cookie removed: " + aBoolean);
                }
            });
        }
        else cookieManager.removeAllCookie();
        mWebview.clearCache(true);
        mWebview.removeAllViews();
        mWebview.clearFormData();
        mWebview.clearHistory();
        super.onDestroy();
    }

    public void OpenWebSite(String urlFromCredential,String javaScript) {

        mWebview.loadUrl(urlFromCredential);
        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);

        urlEditText.setText(urlFromCredential);
        final String javaScriptFinal = javaScript;
        final String urlFromCredentialFinal = urlFromCredential;

        mWebview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if(url.startsWith(urlFromCredentialFinal)) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        view.evaluateJavascript(javaScriptFinal, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {

                            }
                        });
                    } else {
                        view.loadUrl(javaScriptFinal);
                    }
                }
            }
        });
    }
}
