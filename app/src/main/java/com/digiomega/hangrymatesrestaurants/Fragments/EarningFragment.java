package com.digiomega.hangrymatesrestaurants.Fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.digiomega.hangrymatesrestaurants.AllConstant.Config;
import com.digiomega.hangrymatesrestaurants.AllConstant.PreferenceClass;
import com.digiomega.hangrymatesrestaurants.R;


/**
 * Created by qboxus on 10/18/2019.
 */

public class EarningFragment extends Fragment {


    WebView mWebview;

    SharedPreferences sPre;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.earning_fragment, container, false);

        sPre = getContext().getSharedPreferences(PreferenceClass.user, getContext().MODE_PRIVATE);


        init(v);

        return v;
    }

    public void init(View v){
         callWebView(v);



    }


    public void callWebView(View v){


        mWebview = v.findViewById(R.id.web_view);
        mWebview.getSettings().setJavaScriptEnabled(true);

        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setBuiltInZoomControls(true);


        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(getContext(), rerr.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                String webUrl = mWebview.getUrl();

            }



    });
        mWebview.loadUrl(Config.earning_url);}


}
