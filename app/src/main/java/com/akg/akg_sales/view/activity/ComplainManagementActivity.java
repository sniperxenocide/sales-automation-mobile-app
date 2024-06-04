package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.databinding.ActivityComplainManagementBinding;
import com.akg.akg_sales.service.CommonService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.util.WebViewConfig;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ComplainManagementActivity extends AppCompatActivity {
    ActivityComplainManagementBinding binding;
    WebView webView;
    ProgressDialog progressBar;
    ValueCallback<Uri[]> vc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_complain_management);
        binding.setVm(this);
        binding.executePendingBindings();

        CommonService.fetchComplaintHandlingConfig(this,config->{
            if(config==null) finish();
            else loadWebView(config.getBaseUrl()+"?"+config.getUrlParams());
        });
    }

    private void loadWebView(String url){
        webView = binding.webview;
        WebViewConfig.setConfig(webView,this);



        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams){

                Intent intent = fileChooserParams.createIntent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("*/*");
                startActivityForResult( intent, 100);
                vc = filePathCallback;
                return true;
            }
        });

        webView.loadUrl(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri[] selectedFileUri = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            System.out.println("selectedFileUri "+ Arrays.toString(selectedFileUri));
            System.out.println("DataString "+data.getDataString());
            System.out.println("ClipData "+data.getClipData());
            if(selectedFileUri==null) {
                ClipData clipData = data.getClipData();
                selectedFileUri = new Uri[clipData.getItemCount()];
                for(int i=0;i<clipData.getItemCount();i++){
                    selectedFileUri[i] = clipData.getItemAt(i).getUri();
                }
            }
            vc.onReceiveValue(selectedFileUri);
        }catch (Exception e){
            e.printStackTrace();
            CommonUtil.showToast(this,"File Selection Error: "+e.getMessage(),false);
        }
    }

    public void backToHomeDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit Complaint Handling ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> super.onBackPressed())
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public void navigateBack(){
        System.out.println("Back Pressed");
        try {
            if (webView.copyBackForwardList().getCurrentIndex() > 0) {
//                progressBar.show();
                webView.goBack();
            }
            else backToHomeDialog();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}