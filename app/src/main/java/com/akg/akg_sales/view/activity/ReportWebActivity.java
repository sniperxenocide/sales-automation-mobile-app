package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.databinding.ActivityReportWebBinding;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.util.WebViewConfig;

public class ReportWebActivity extends AppCompatActivity {
    ActivityReportWebBinding binding;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFirebaseUserId();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report_web);
        binding.setVm(this);
        binding.executePendingBindings();

        webView = binding.webview;
        String url = API.baseUrl+"/report-service/api/web/dashboard";
        WebViewConfig.setConfig(webView,this);
        CookieManager.getInstance().setCookie(API.baseUrl,"Authorization="+ CommonUtil.loggedInUser.getToken(),
                r-> webView.loadUrl(url));



    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public void navigateBack(){
        System.out.println("Back Pressed");
        try {
            if (webView.copyBackForwardList().getCurrentIndex() > 0) {
                webView.goBack();
            }
            else finish();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}