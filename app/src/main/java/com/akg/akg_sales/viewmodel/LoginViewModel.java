package com.akg.akg_sales.viewmodel;

import android.content.Intent;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.LoginApi;
import com.akg.akg_sales.dto.User;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.HomeActivity;
import com.akg.akg_sales.view.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends BaseObservable {
    public LoginActivity activity;
    private User user;

    public LoginViewModel(LoginActivity activity){
        this.activity = activity;
        user = new User();
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

    // Customer Number 194311
    public void loginAction(){
        if(user.getUsername()==null || user.getUsername().length()==0)
            CommonUtil.showToast(activity,"Username Cannot be Empty",false);
        else {
            try {
                LoginApi loginApi = API.getClient().create(LoginApi.class);
                Call<User> call = loginApi.authenticate(user);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User validUser = response.body();
                        if(!validUser.getToken().isEmpty()){
                            validUser.setUsername(user.getUsername()).setPassword(user.getPassword());
                            CommonUtil.showToast(activity,"Login Success",true);
                            CommonUtil.loggedInUser = validUser;
                            Intent homeIntent = new Intent(activity, HomeActivity.class);
                            activity.startActivity(homeIntent);
                        }
                        else CommonUtil.showToast(activity,"Login Failed",false);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
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
