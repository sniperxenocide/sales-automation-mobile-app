package com.akg.akg_sales.viewmodel;

import android.content.Intent;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.dto.UserDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.LoginActivity;
import com.akg.akg_sales.view.activity.notification.NotificationActivity;

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

    public void loginAction(){
        if(userDto.getUsername()==null || userDto.getUsername().length()==0)
            CommonUtil.showToast(activity,"Username Cannot be Empty",false);
        else {
            CommonUtil.loggedInUser = this.userDto;
            Intent notificationIntent = new Intent(activity, NotificationActivity.class);
            activity.startActivity(notificationIntent);
        }
    }


}
