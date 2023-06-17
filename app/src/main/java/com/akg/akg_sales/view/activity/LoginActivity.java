package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.akg.akg_sales.BuildConfig;
import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.databinding.ActivityLoginBinding;
import com.akg.akg_sales.dto.AppVersion;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.viewmodel.LoginViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public LoginViewModel loginViewModel;
    ActivityLoginBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = new LoginViewModel(this);
        loginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        loginBinding.setVm(loginViewModel);
        loginBinding.executePendingBindings();

        checkForUpdate();
    }

    private void checkForUpdate(){
        fetchAppUpdateInfo(v->{
            //if(v.getVersionCode()>BuildConfig.VERSION_CODE){
                new ConfirmationDialog(this,
                        "New Version Available. Download Now?", i->{
                    try {startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(v.getDownloadUrl())));
                    }catch (Exception e){e.printStackTrace();}
                });
            //}
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
}