package com.akg.akg_sales.view.activity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

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
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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
        loadPage();
    }

    private void loadPage(){
        loginViewModel = new LoginViewModel(this);
        loginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        loginBinding.setVm(loginViewModel);
        loginBinding.executePendingBindings();

        checkForUpdate();
        //handleServerSelection();
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
                    CommonUtil.gpsAddress = getGpsAddress(location);
                    System.out.println(CommonUtil.gpsLocation.toString());
                    System.out.println(CommonUtil.gpsAddress);
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

    private String getGpsAddress(Location location){
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(
                    location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {
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
        }catch (Exception e){e.printStackTrace();}
        return null;
    }

}