package com.akg.akg_sales.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityHomeBinding;
import com.akg.akg_sales.viewmodel.HomeViewModel;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding homeBinding = DataBindingUtil.setContentView(this,R.layout.activity_home);
        homeBinding.setVm(new HomeViewModel(this));
        homeBinding.executePendingBindings();
    }
}