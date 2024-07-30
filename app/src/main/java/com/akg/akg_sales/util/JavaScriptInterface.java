package com.akg.akg_sales.util;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class JavaScriptInterface {
    private Context context;
    public JavaScriptInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void getBase64FromBlobData(String base64Data,String extension) throws IOException {
        convertBase64StringToPdfAndStoreIt(base64Data,extension);
    }
    public static String getBase64StringFromBlobUrl(String blobUrl,String extension) {
        if(blobUrl.startsWith("blob")){
            return "javascript: var xhr = new XMLHttpRequest();" +
                    "xhr.open('GET', '"+ blobUrl +"', true);" +
                    "xhr.setRequestHeader('Content-type','application/*');" +
                    "xhr.responseType = 'blob';" +
                    "xhr.onload = function(e) {" +
                    "    if (this.status == 200) {" +
                    "        var blobPdf = this.response;" +
                    "        var reader = new FileReader();" +
                    "        reader.readAsDataURL(blobPdf);" +
                    "        reader.onloadend = function() {" +
                    "            base64data = reader.result;" +
                    "            Android.getBase64FromBlobData(base64data,'"+extension+"');" +
                    "        }" +
                    "    }" +
                    "};" +
                    "xhr.send();";
        }
        return "javascript: console.log('It is not a Blob URL');";
    }
    private void convertBase64StringToPdfAndStoreIt(String base64PDf,String extension) throws IOException {
        try {
            System.out.println(base64PDf);
            System.out.println(extension);
            String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
            final File filepath = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS) + "/report_" + currentDateTime + "."+extension);
            byte[] pdfAsBytes = Base64.decode(base64PDf.replaceFirst("^data:.*;base64,", ""), 0);
            FileOutputStream os;
            os = new FileOutputStream(filepath, false);
            os.write(pdfAsBytes);
            os.flush();

            if (filepath.exists()) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                Uri apkURI = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider", filepath);
                intent.setDataAndType(apkURI, MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                PendingIntent pendingIntent = PendingIntent.getActivity(context,1, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                CommonUtil.showToast(context,"Download Complete. Check Download Folder.",true);
                context.startActivity(intent);
            }
        }catch (Exception e){
            Log.e("convertBase64StringToPdfAndStoreIt", e.getMessage(),e );
        }
    }
}
