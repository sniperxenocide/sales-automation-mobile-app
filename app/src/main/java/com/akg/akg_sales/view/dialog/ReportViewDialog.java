package com.akg.akg_sales.view.dialog;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.databinding.DialogDeliveryDetailReportBinding;
import com.akg.akg_sales.databinding.DialogReportViewBinding;
import com.akg.akg_sales.dto.report.ReportDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.ReportActivity;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;

import java.net.CookieHandler;

public class ReportViewDialog {
    DialogReportViewBinding binding;
    public Dialog dialog;
    ReportActivity activity;
    private ReportDto reportDto;

    public ReportViewDialog(ReportActivity activity,ReportDto report){
        this.activity = activity;
        dialog=new Dialog(activity);
        this.reportDto = report;
    }

    public void showReport(){
        binding = DialogReportViewBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(0.95*displayMetrics.widthPixels);
        int height = (int)(0.9*displayMetrics.heightPixels);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        CookieManager.getInstance().setCookie(API.baseUrl,"Authorization="+ CommonUtil.loggedInUser.getToken(),
                r->{loadWebView();});
    }

    private void loadWebView(){
        ProgressDialog progressBar = new ProgressDialog(activity);
        progressBar.setMessage("Loading.Please wait...");
        binding.reportHeader.setText(reportDto.getReportName());
        WebView webView = binding.webview;  //WebViewClient
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                progressBar.show();
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageCommitVisible (WebView view, String url){
                super.onPageCommitVisible(view,url);
                progressBar.dismiss();
            }
            public void onPageFinished(WebView view, String url) {}

        });
//        webView.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                android.util.Log.d("WebView", consoleMessage.message());
//                return true;
//            }
//        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);

        //webView.setInitialScale(100);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);

        webView.loadUrl(API.baseUrl+reportDto.getReportUrl());
    }


    public void createWebPrintJob() {
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        String jobName = reportDto.getReportName();
        PrintDocumentAdapter printAdapter = binding.webview.createPrintDocumentAdapter(jobName);
        printManager.print(jobName, printAdapter, null);
    }
}
