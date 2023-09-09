package com.akg.akg_sales.view.activity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;

import com.akg.akg_sales.BuildConfig;
import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.databinding.ActivityLoginBinding;
import com.akg.akg_sales.dto.AppVersion;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.viewmodel.LoginViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public LoginViewModel loginViewModel;
    ActivityLoginBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDeviceInfo();
        checkForUpdate();
        loadPage();
    }

    private void loadPage(){
        loginViewModel = new LoginViewModel(this);
        loginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        loginBinding.setVm(loginViewModel);
        loginBinding.executePendingBindings();
    }

    private void checkForUpdate(){
        fetchAppUpdateInfo(v->{
            if(v.getVersionCode()>BuildConfig.VERSION_CODE){
                new ConfirmationDialog(this,
                        "New Version (v"+v.getVersionName()+") Available. Download Now?", i->{
                    try {startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(v.getDownloadUrl())));
                    }catch (Exception e){e.printStackTrace();}
                });
            }
        });

    }

    private void fetchAppUpdateInfo(Consumer<AppVersion> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(this);
        API.getClient().create(LoginApi.class).getLatestVersion()
                .enqueue(new Callback<AppVersion>() {
                    @Override
                    public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                        progressDialog.dismiss();
                        try {
                            if(response.code()==200){
                                callback.accept(response.body());
                            }
                            else throw new Exception(response.code()+"."+response.message());
                        }catch (Exception e){
                            CommonUtil.showToast(getApplicationContext(),e.getMessage(),false);
                        }
                    }

                    @Override
                    public void onFailure(Call<AppVersion> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                    }
                });
    }

    private void getDeviceInfo(){
        try {
            CommonUtil.deviceModel = Build.MANUFACTURER+" "+Build.MODEL;
        }catch (Exception e){e.printStackTrace();}
        try {
            CommonUtil.appVersion = BuildConfig.VERSION_CODE+":"+BuildConfig.VERSION_NAME;
        }catch (Exception e){e.printStackTrace();}
        try {
            CommonUtil.deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }catch (Exception e){e.printStackTrace();}

        getGpsLocation();
        getPhoneNumber();
    }

    public void getPhoneNumber() {
        if (ActivityCompat.checkSelfPermission(this, READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
        {
            setPhone();
        }
        else {
            String[] permissions = new String[]{READ_SMS, READ_PHONE_STATE};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissions = new String[]{READ_PHONE_NUMBERS,READ_SMS, READ_PHONE_STATE};
            }
            requestPermissions(permissions, 100);
        }

    }

    private void getGpsLocation(){
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            setLocation();
        }
        else {
            String[] permissions = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
            requestPermissions(permissions, 101);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100) {
            if (ActivityCompat.checkSelfPermission(this, READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            setPhone();
        }
        else if(requestCode==101){
            if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                return;
            }
            setLocation();
        }
        else System.out.println("Permission Failed");
    }

    private void setLocation(){
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(location ->{
            try {
                if(location!=null){
                    CommonUtil.gpsLocation = location;
                    System.out.println(CommonUtil.gpsLocation.toString());
                }
                else System.out.println("################### Location Null");
            }catch (Exception e){e.printStackTrace();}

        });
    }

    private void setPhone(){
        try {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            CommonUtil.devicePhone = telephonyManager.getLine1Number();
            System.out.println("Device Phone "+CommonUtil.devicePhone);
        }catch (Exception e){e.printStackTrace();}
    }

}