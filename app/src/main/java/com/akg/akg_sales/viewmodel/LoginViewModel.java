package com.akg.akg_sales.viewmodel;

import android.content.Intent;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.dto.UserDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.HomeActivity;
import com.akg.akg_sales.view.activity.LoginActivity;
import com.akg.akg_sales.view.activity.notification.NotificationActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends BaseObservable {
    public LoginActivity activity;
    private UserDto userDto;

    public LoginViewModel(LoginActivity activity){
        this.activity = activity;
        userDto = new UserDto();
    }

    @Bindable
    public String getUsername() {
        return this.userDto.getUsername();
    }

    public void setUsername(String username) {
        this.userDto.setUsername(username);
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getPassword() {
        return this.userDto.getPassword();
    }

    public void setPassword(String password) {
        this.userDto.setPassword(password);
        notifyPropertyChanged(BR.password);
    }

    // Customer Number 194311
    public void loginAction(){
        if(userDto.getUsername()==null || userDto.getUsername().length()==0)
            CommonUtil.showToast(activity,"Username Cannot be Empty",false);
        else {
            try {
                LoginApi loginApi = API.getClient().create(LoginApi.class);
                Call<UserDto> call = loginApi.authenticate(userDto);
                call.enqueue(new Callback<UserDto>() {
                    @Override
                    public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                        UserDto validUser = response.body();
                        if(!validUser.getToken().isEmpty()){
                            validUser.setUsername(userDto.getUsername()).setPassword(userDto.getPassword());
                            CommonUtil.showToast(activity,"Login Success",true);
                            CommonUtil.loggedInUser = validUser;
                            Intent homeIntent = new Intent(activity, HomeActivity.class);
                            activity.startActivity(homeIntent);
                        }
                        else CommonUtil.showToast(activity,"Login Failed",false);
                    }

                    @Override
                    public void onFailure(Call<UserDto> call, Throwable t) {
                        call.cancel();
                        CommonUtil.showToast(activity,"Login Failed",false);
                    }
                });
            }catch (Exception e){
                CommonUtil.showToast(activity,e.getMessage(),false);
                e.printStackTrace();
            }


        }
    }


}
