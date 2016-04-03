package com.mario22gmail.license.nfc_writer;


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
    private WebView mWebview;

    public void InitString(String text)
    {
        js = text;
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
        OpenFacebook("a","v");
        Log.i("nfc_debug","A trecut");
        return v;
//        inflater.inflate(R.layout.fragment_browser, container, false);



    }
    public void OpenFacebook(String userName, String password) {

        String url = "https://www.facebook.com";
//        js = "javascript:document.getElementsByName('email')[0].value = '" + userName + "';document.getElementsByName('pass')[0].value='" + password +
//                "';document.getElementsByName('login')[0].click();";

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
}
