package com.digiomega.hangrymatesrestaurants;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digiomega.hangrymatesrestaurants.AllConstant.Config;
import com.digiomega.hangrymatesrestaurants.AllConstant.Functions;

public class TermsConditionActivity extends AppCompatActivity {

    WebView mWebview;
    ImageView close_icon;
    TextView rider_jobs;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_condition);

        rider_jobs = findViewById(R.id.rider_jobs);

        type = getIntent().getStringExtra("type");

        rider_jobs.setText(type);

        callWebView();

        close_icon= findViewById(R.id.close_btn);
        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                finish();

            }
        });

    }

    private void callWebView() {

        mWebview = findViewById(R.id.web_view);
        mWebview.getSettings().setJavaScriptEnabled(true);

        mWebview.getSettings().setLoadWithOverviewMode(true);
        mWebview.getSettings().setUseWideViewPort(true);
        mWebview.getSettings().setBuiltInZoomControls(true);

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(TermsConditionActivity.this, rerr.getDescription(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon)
            {
                Functions.Show_loader(TermsConditionActivity.this,false,false);

            }


            @Override
            public void onPageFinished(WebView view, String url) {
                Functions.cancel_loader();

                String webUrl = mWebview.getUrl();

            }



        });
        if (type.equals("Privacy Policy")) {
            mWebview.loadUrl(Config.Privacy_policy);
        }else {
            mWebview.loadUrl(Config.Terms_of_Use);
        }
    }
}