package com.akg.akg_sales.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.dto.User;
import com.akg.akg_sales.util.CommonUtil;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {}
    private static final String TAG = "FCM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG,"********* New Token: "+token);
        submitNewFcmToken(token);
    }

    public static void fetchFirebaseToken(){
        try {
            Log.d(TAG,"*** Getting FCM Token...");
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String newToken = task.getResult();
                    Log.d(TAG,"***** FCM Token: "+newToken);
                    submitNewFcmToken(newToken);
                }
                else Log.d(TAG,"Token Fetching Failed");
            });
        }catch (Exception e){
            Log.e(TAG, e.getMessage(),e );
        }
    }

    public static void submitNewFcmToken(String newToken){
        try {
            if(CommonUtil.loggedInUser==null) {
                Log.e(TAG, "User is not Logged In: " );
                return;
            }
            CommonUtil.loggedInUser.setFcmToken(newToken);
            CommonUtil.loggedInUser.setDeviceId(CommonUtil.deviceId);

            API.getClient().create(LoginApi.class).submitNewFcmToken(CommonUtil.loggedInUser)
                    .enqueue(API.getCallback(null,user -> {
                        Log.d("FCM", "FCM Token Updated.");
                        Log.d("FCM", String.valueOf(CommonUtil.loggedInUser));
                    },null));
        }catch (Exception e){
            Log.e(TAG, e.getMessage(),e );
        }
    }
}