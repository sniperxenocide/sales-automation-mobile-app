package com.akg.akg_sales.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.dto.User;
import com.akg.akg_sales.firebase.MyFirebaseMessagingService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.HomeActivity;
import com.akg.akg_sales.view.activity.LoginActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends BaseObservable {
    public LoginActivity activity;
    private final String LOG_TAG = "LoginViewModel";
    private User user;

    public LoginViewModel(LoginActivity activity){
        this.activity = activity;
        user = new User();
        loadCred();
    }

    private void loadCred(){
        try {
            System.out.println("Loading Cred from Memory");
            SharedPreferences sp = activity.getSharedPreferences("cred", Context.MODE_PRIVATE);
            setUsername(sp.getString("username",""));
            setPassword(sp.getString("password",""));
        }catch (Exception e){e.printStackTrace();}
    }

    private void storeCred(User u){
        try {
            System.out.println("Storing Cred in Memory");
            SharedPreferences sp = activity.getSharedPreferences("cred", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("username",u.getUsername());
            editor.putString("password",u.getPassword());
            editor.apply();
        }catch (Exception e){e.printStackTrace();}
    }

    private void storeUrlToMemory(){
        try {
            System.out.println("Storing Cred in Memory");
            SharedPreferences sp = activity.getSharedPreferences("baseUrlMem", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("baseUrl",API.baseUrl);
            editor.apply();
        }catch (Exception e){e.printStackTrace();}
    }

    @Bindable
    public String getUsername() {
        return this.user.getUsername();
    }

    public void setUsername(String username) {
        this.user.setUsername(username);
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getPassword() {
        return this.user.getPassword();
    }

    public void setPassword(String password) {
        this.user.setPassword(password);
        notifyPropertyChanged(BR.password);
    }

    private void prepareLoginPostBody(){
        user.setDeviceModel(CommonUtil.deviceModel).setDeviceId(CommonUtil.deviceId)
                .setDevicePhone(CommonUtil.devicePhone).setAppVersion(CommonUtil.appVersion)
                .setOsVersion(CommonUtil.osVersion);
        if(CommonUtil.gpsLocation !=null){
            try {
                JSONObject gps = new JSONObject();
                gps.put("gpsLocation",CommonUtil.gpsLocation.getLatitude()+","+CommonUtil.gpsLocation.getLongitude());
                gps.put("gpsAddress",CommonUtil.gpsAddress);
                user.setGpsLocation(gps.toString());
            }catch (Exception ignored){}
        }
    }

    // Customer Number 194311
    public void loginAction(){
        if(user.getUsername()==null || user.getUsername().isEmpty()){
            CommonUtil.showToast(activity,"Username Cannot be Empty",false);
            return;
        }
        prepareLoginPostBody();
        try {
            ProgressDialog progressDialog=CommonUtil.showProgressDialog(activity);
            API.getClient().create(LoginApi.class).authenticate(user)
                    .enqueue(API.getCallback(activity,validUser->{
                        System.out.println("Login Success****************");
                        if (!validUser.getToken().isEmpty()) {
                            validUser.setUsername(user.getUsername()).setPassword(user.getPassword());
                            storeCred(validUser);
                            //storeUrlToMemory();
                            CommonUtil.showToast(activity, "Login Success", true);
                            CommonUtil.loggedInUser = validUser;
                            Intent homeIntent = new Intent(activity, HomeActivity.class);
                            activity.startActivity(homeIntent);
                            MyFirebaseMessagingService.fetchFirebaseToken();
                        } else CommonUtil.showToast(activity, "Login Failed", false);
                    }, e-> CommonUtil.showToast(activity,"Login Failed. Incorrect Username or Password.",false),
                            progressDialog));

        }catch (Exception e){
            CommonUtil.showToast(activity,e.getMessage(),false);
            Log.e(LOG_TAG, "loginAction: ",e);
        }

    }


}
