package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.databinding.ActivityReportWebBinding;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.util.WebViewConfig;

public class ReportWebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadChromeCustomTab();

        finish();

    }

    public void loadChromeCustomTab(){

        try {
            String url = API.baseUrl+"/web/login?token="
                    +CommonUtil.loggedInUser.getToken();
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                    .setUrlBarHidingEnabled(true);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(url));
        }catch (Exception ignored){}

//        CookieManager.getInstance().setCookie(API.baseUrl,"Authorization="+CommonUtil.loggedInUser.getToken(),
//                r->{});
    }


}