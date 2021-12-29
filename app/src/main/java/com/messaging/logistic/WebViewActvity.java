package com.messaging.logistic;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.adapter.logistic.ProgressItem;

public class WebViewActvity extends Activity {

    String googleDocUrl = "http://docs.google.com/gview?embedded=true&url=";
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        progressBar  = new ProgressBar(this);
        WebView view = (WebView) findViewById(R.id.webView);
        progressBar  = (ProgressBar) findViewById(R.id.webViewProgress);

        Bundle bundle = getIntent().getExtras();
        String path = bundle.getString("Path");
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        view.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                try{
                    if (view.getTitle().equals("")) {
                        view.reload();
                    }else {
                        if (progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }

        });
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setSupportZoom(true);
        progressBar.setVisibility(View.VISIBLE);
        view.loadUrl(googleDocUrl+path);
    }

}
