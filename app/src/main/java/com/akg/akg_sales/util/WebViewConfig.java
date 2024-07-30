package com.akg.akg_sales.util;

import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;

public class WebViewConfig {

    public static void setConfig(WebView webView, Context context){
        ProgressDialog progressBar = new ProgressDialog(context);
        progressBar.setMessage("Loading.Please wait...");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setSupportMultipleWindows(true);

        //webView.setInitialScale(100);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        try {
            String ua = webView.getSettings().getUserAgentString();
            String androidOSString = webView.getSettings().getUserAgentString().substring(ua.indexOf("("), ua.indexOf(")") + 1);
            String newUserAgent = webView.getSettings().getUserAgentString().replace(androidOSString, "(X11; Linux x86_64)");
            webView.getSettings().setUserAgentString(newUserAgent);
        } catch (Exception e) {
            e.printStackTrace();
        }


        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                progressBar.show();
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("WebView", "onPageStarted: "+url);
            }

            @Override
            public void onPageCommitVisible (WebView view, String url){
                super.onPageCommitVisible(view,url);
                progressBar.dismiss();
            }
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "onPageFinished: "+url);
                try {
                    progressBar.dismiss();
                }catch (Exception e){}
            }

        });

    }

    public static void configPdfDownloader(WebView webView, Context context){
        webView.addJavascriptInterface(new JavaScriptInterface(context), "Android");
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {

            String fileName = URLUtil.guessFileName(url,contentDisposition,mimetype);
            System.out.println("Filename "+fileName);
            String extention = "";
            if(fileName.endsWith(".pdf")) extention = "pdf";
            else if(fileName.endsWith(".xls")) extention = "xls";
            else if (fileName.endsWith(".xlsx")) extention = "xlsx";
            else if (fileName.endsWith(".html")) extention = "html";
            System.out.println(mimetype);
            System.out.println(userAgent);

            webView.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(url,extention));
        });
    }

}
