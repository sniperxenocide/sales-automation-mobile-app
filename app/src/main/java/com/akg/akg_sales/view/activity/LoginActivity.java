package com.akg.akg_sales.view.activity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.akg.akg_sales.BuildConfig;
import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.databinding.ActivityLoginBinding;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.util.SPHelper;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.viewmodel.LoginViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    public LoginViewModel loginViewModel;
    ActivityLoginBinding loginBinding;
    private final String LOG_TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
    }

    private void loadPage(){
        loginViewModel = new LoginViewModel(this);
        loginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        loginBinding.setVm(loginViewModel);
        loginBinding.executePendingBindings();

        getDeviceInfo();
        checkForUpdate();
        //handleServerSelection();
    }

    private void checkForUpdate(){
        if(SPHelper.shouldBlockApiCall(this, SPHelper.KEY_NEXT_APP_UPDATE_CHECK_TIMESTAMP))
            return;

        Log.d(LOG_TAG, "checkForUpdate: App Update Check Initiating...");
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(this);
        API.getClient().create(LoginApi.class).getLatestVersion()
                .enqueue(API.getCallback(this,v->{
                    SPHelper.setNextApiCallTimestamp(this, SPHelper.KEY_NEXT_APP_UPDATE_CHECK_TIMESTAMP);
                    if(v.getVersionCode()>BuildConfig.VERSION_CODE){
                        String msg = "New Version (v"+v.getVersionName()+ ") Available. Download Now?";
                        new ConfirmationDialog(this, msg,i->{
                            try {startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(v.getDownloadUrl())));
                            }catch (Exception e){Log.e(LOG_TAG, "checkForUpdate: ",e);}
                        });
                    }},e->{},progressDialog));
    }

    private void getDeviceInfo(){
        CommonUtil.deviceModel = Build.MANUFACTURER + " " + Build.BRAND + " " + Build.MODEL;
        CommonUtil.appVersion = BuildConfig.VERSION_CODE + ":" + BuildConfig.VERSION_NAME;
        CommonUtil.osVersion = Build.VERSION.RELEASE + " SDK: " + Build.VERSION.SDK_INT;
        CommonUtil.deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        requestAllPermissions();
        setGpsLocation();
    }

    private void requestAllPermissions(){
        String[] PERMISSIONS = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }

        // Android 13+ notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[0]), 105);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            for (int result : grantResults)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    CommonUtil.showToast(this,"All Permissions are required to Function the App Properly!",false);
                    break;
                }
            setGpsLocation();
        }
    }

    private void setGpsLocation(){
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        Log.d("LOGIN", "setGpsLocation: ");
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(location ->{
            try {
                if(location!=null){
                    CommonUtil.gpsLocation = location;
                    CommonUtil.gpsAddress = getGpsAddress(location);
                    Log.d(LOG_TAG, "GPS Location: "+CommonUtil.gpsLocation.toString());
                    Log.d(LOG_TAG, "GPS Address: "+CommonUtil.gpsAddress);
                }
                else System.out.println("################### Location Null");
            }catch (Exception e){
                Log.e(LOG_TAG, "setGpsLocation: ",e );
            }

        });
    }

    private String getGpsAddress(Location location){
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && !addressList.isEmpty()) return getGpsAddress(addressList);
        }catch (Exception e){Log.e(LOG_TAG, "getGpsAddress: ",e);}
        return null;
    }

    private String getGpsAddress(List<Address> addressList) {
        Address address = addressList.get(0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            sb.append(address.getAddressLine(i)).append(",");
        }
        sb.append(address.getFeatureName()).append(",")
                .append(address.getSubLocality()).append(",")
                .append(address.getLocality()).append(",")
                .append(address.getCountryName());
        String addr = sb.toString();
        if(addr.length()>200) addr = addr.substring(0,200);
        return addr;
    }


    private void handleServerSelection(){
        loadUrlFromMemory();
        if(API.baseUrl.trim().length()==0) loadOperatingUnitSelectionDropdown();
        else showCredentialUI();
    }

    private void loadUrlFromMemory(){
        try {
            System.out.println("Loading Base URL from Memory");
            SharedPreferences sp = getSharedPreferences("baseUrlMem", Context.MODE_PRIVATE);
            API.baseUrl = sp.getString("baseUrl","");
        }catch (Exception e){e.printStackTrace();}

    }

    private void loadOperatingUnitSelectionDropdown() {
        loginBinding.operatingUnitDropdownContainer.setVisibility(View.VISIBLE);
        loginBinding.credentialContainer.setVisibility(View.GONE);
        API.loadServerUrl();

        HashMap<String,String> baseUrlMap = API.baseUrlMap;
        AutoCompleteTextView tView=loginBinding.operatingUnitDropdown;
        String[] businessList = new String[baseUrlMap.size()];
        String[] urlList = new String[baseUrlMap.size()];
        int cnt = 0;
        for(String k:baseUrlMap.keySet()){
            businessList[cnt] = k;
            urlList[cnt] = baseUrlMap.get(k);
            cnt++;
        }
        ArrayAdapter<String> ptAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, businessList);
        tView.setAdapter(ptAdapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            tView.setText(businessList[i],false);
            API.baseUrl = urlList[i];
            showCredentialUI();
        });
    }

    private void showCredentialUI(){
        loginBinding.operatingUnitDropdownContainer.setVisibility(View.GONE);
        loginBinding.credentialContainer.setVisibility(View.VISIBLE);
        checkForUpdate();
    }



}