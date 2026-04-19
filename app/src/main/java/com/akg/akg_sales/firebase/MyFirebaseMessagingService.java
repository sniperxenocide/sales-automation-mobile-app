package com.akg.akg_sales.firebase;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.dto.User;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.LoginActivity;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

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
            try {
                if(Objects.equals(remoteMessage.getData().get("type"), "notification")){
                    String title = remoteMessage.getData().get("title");
                    String message = remoteMessage.getData().get("message");
                    showNotification(title, message);
                }
            } catch (Exception e) {
                Log.e(TAG, "onMessageReceived: ",e );
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
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
        }catch (Exception e){Log.e(TAG, e.getMessage(),e);}
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
                    },e->{},null));
        }catch (Exception e){Log.e(TAG, e.getMessage(),e );}
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("fcm_channel_id",
                    "FCM Notifications", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Firebase Cloud Messages");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build());
            channel.enableLights(true);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String message) {
        createNotificationChannel();

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "fcm_channel_id")
                        .setSmallIcon(R.drawable.akg_logo)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setAutoCancel(false)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);
//                        .setContentIntent(pendingIntent);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            manager.notify((int) System.currentTimeMillis(), builder.build());
        else Log.d(TAG, "Notification Permission Denied");
    }
}