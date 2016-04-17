package com.mario22gmail.license.nfc_project;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrowserFragment extends Fragment {

    private String js ="";
    private String url = "";
    private WebView mWebview;

    public void InitString(String text)
    {
        js = text;
    }

    public void InitUrl(String text)
    {
        url = text;
    }


    public BrowserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_browser, container, false);
        mWebview = (WebView) v.findViewById(R.id.webViewFb);

        if(!js.equals("") && !url.equals(""))
        {
            OpenWebSite(url,js);
            Log.i("nfc_debug", "A trecut");
        }

        return v;
//        inflater.inflate(R.layout.fragment_browser, container, false);



    }

    @Override
    public void onPause() {
        Log.i("nfc_debug", "browser on pause");
        mWebview.clearCache(true);
        mWebview.clearHistory();
        mWebview.clearFormData();
        mWebview.loadUrl("www.google.com");


        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i("nfc_debug","browser on stop");
        mWebview.clearCache(true);

        mWebview.clearFormData();
        mWebview.clearHistory();
        mWebview.loadUrl("www.google.com");

        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.i("nfc_debug","browser on stop");
        mWebview.loadUrl("www.google.com");
        mWebview.clearCache(true);
        mWebview.clearFormData();
        mWebview.clearHistory();
        super.onDestroy();
    }

    public void OpenWebSite(String urlFromCredential,String javaScript) {

        mWebview.loadUrl(urlFromCredential);
        WebSettings settings = mWebview.getSettings();
        settings.setJavaScriptEnabled(true);

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
