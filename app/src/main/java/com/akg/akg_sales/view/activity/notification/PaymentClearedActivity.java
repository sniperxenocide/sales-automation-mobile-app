package com.akg.akg_sales.view.activity.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityPaymentClearedBinding;
import com.akg.akg_sales.dto.notification.PaymentDto;
import com.akg.akg_sales.util.CommonUtil;

public class PaymentClearedActivity extends AppCompatActivity {

    ActivityPaymentClearedBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PaymentDto dto = (PaymentDto) getIntent().getSerializableExtra("payment");
            binding = DataBindingUtil.setContentView(this,R.layout.activity_payment_cleared);
            binding.setVm(dto);
            binding.setActivity(this);
            binding.executePendingBindings();
        }catch (Exception e){
            CommonUtil.showToast(this,e.getMessage(),false);
            finish();
        }
    }

}