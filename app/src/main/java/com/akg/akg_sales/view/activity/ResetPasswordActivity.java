package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.IntentCompat;
import androidx.core.util.Consumer;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.databinding.ActivityResetPasswordBinding;
import com.akg.akg_sales.dto.ResetPasswordDto;
import com.akg.akg_sales.dto.User;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.GeneralDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    ActivityResetPasswordBinding binding;
    private boolean oldPasswordValidated = false;
    private boolean newPasswordValidated = false;
    String passwordRule = "^[a-zA-Z0-9]{4,15}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFirebaseUserId();
        loadPage();
        oldPasswordValidation();
        newPasswordValidation();
    }

    private void loadPage(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_reset_password);
        binding.setActivity(this);
        binding.executePendingBindings();
    }

    public void submitPassword(){
        if(oldPasswordValidated && newPasswordValidated){
            new ConfirmationDialog(this,"Password Reset Confirmation!",
                    i->{
                        String oldPass = binding.oldPassword.getText().toString();
                        String newPass = binding.newPassword.getText().toString();

                        ResetPasswordDto body = new ResetPasswordDto(oldPass,newPass);
                        resetPassword(body,res->{
                            CommonUtil.showToast(this,"Password Reset Successful",true);
                            User user = res;
                            CommonUtil.loggedInUser = null;
                            try {
                                System.out.println("Storing Cred in Memory");
                                SharedPreferences sp = getSharedPreferences("cred", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("username",user.getUsername());
                                editor.putString("password","");
                                editor.apply();
                            }catch (Exception e){e.printStackTrace();}

                            Intent intent = new Intent(this,LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        });
            });

        }

    }

    private void oldPasswordValidation(){
        binding.oldPassErrMsg.setText("Invalid Old Password");
        binding.oldPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().equals(CommonUtil.loggedInUser.getPassword())){
                    binding.oldPassErrMsg.setVisibility(View.VISIBLE);
                    oldPasswordValidated = false;
                }
                else {
                    binding.oldPassErrMsg.setVisibility(View.GONE);
                    oldPasswordValidated = true;
                }
            }
        });
    }

    private void newPasswordValidation(){
        binding.newPassErrMsg.setText("Password Rule Violation");
        binding.newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("New Pass Validation");
                if(!editable.toString().matches(passwordRule)){
                    binding.newPassErrMsg.setVisibility(View.VISIBLE);
                    newPasswordValidated = false;
                }
                else {
                    binding.newPassErrMsg.setVisibility(View.GONE);
                    newPasswordValidated = true;
                }
            }
        });
    }


    private void resetPassword(ResetPasswordDto body,Consumer<User> callback){
        ProgressDialog progressDialog = CommonUtil.showProgressDialog(this);
        API.getClient().create(LoginApi.class).resetPassword(body)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        progressDialog.dismiss();
                        try {
                            if(response.code()==200){
                                callback.accept(response.body());
                            }
                            else throw new Exception(response.code()+"."+response.message());
                        }catch (Exception e){
                            e.printStackTrace();
                            CommonUtil.showToast(getApplicationContext(),e.getMessage(),false);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        progressDialog.dismiss();
                        call.cancel();
                        t.printStackTrace();
                    }
                });
    }

}