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

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("T", "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d("T", "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d("T", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        System.out.println("********* New Token: "+token);
    }

    public static void fetchFirebaseToken(){
        try {
            System.out.println("*** Getting FCM Token...");
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    String newToken = task.getResult();
                    System.out.println("***** FCM Token: "+newToken);
                    if(CommonUtil.loggedInUser!=null &&
                            (CommonUtil.loggedInUser.getFcmToken()==null || !CommonUtil.loggedInUser.getFcmToken().equals(newToken))){
                        System.out.println("Submitting FCM Token to Server");
                        submitNewFcmToken(newToken);
                    }
                }
                else System.out.println("Token Fetching Failed");
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void submitNewFcmToken(String newToken){
        try {
            RequestBody fcmTokenBody = RequestBody.create(MediaType.parse("text/plain"),newToken);
            API.getClient().create(LoginApi.class).submitNewFcmToken(fcmTokenBody)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            try {
                                if(response.isSuccessful()){
                                    System.out.println("FCM Token Updated.");
                                    CommonUtil.loggedInUser.setFcmToken(response.body().getFcmToken());
                                    System.out.println(CommonUtil.loggedInUser);
                                }
                                else {
                                    throw new Exception(response.message());
                                }
                            }catch (Exception e){e.printStackTrace();}
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            t.printStackTrace();
                            call.cancel();
                        }
                    });
        }catch (Exception e){e.printStackTrace();}
    }
}