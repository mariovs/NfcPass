package com.mario22gmail.license.nfc_project;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Mario Vasile on 12/7/2015.
 */
public class WebViewController extends WebViewClient
{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

}
