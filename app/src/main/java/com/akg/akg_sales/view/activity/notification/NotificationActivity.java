package com.akg.akg_sales.view.activity.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityNotificationBinding;
import com.akg.akg_sales.viewmodel.notification.NotificationViewModel;

public class NotificationActivity extends AppCompatActivity {
    public ActivityNotificationBinding notificationBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationBinding = DataBindingUtil.setContentView(this,R.layout.activity_notification);
        notificationBinding.setVm(new NotificationViewModel(this));
        notificationBinding.executePendingBindings();
    }
}