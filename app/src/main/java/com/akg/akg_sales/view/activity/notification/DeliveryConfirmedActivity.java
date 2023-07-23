package com.akg.akg_sales.view.activity.notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.NotificationApi;
import com.akg.akg_sales.databinding.ActivityDeliveryConfirmedBinding;
import com.akg.akg_sales.dto.notification.DeliveryConfirmedHeaderDto;
import com.akg.akg_sales.dto.notification.DeliveryConfirmedLineDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.notification.DeliveryConfirmedLineAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryConfirmedActivity extends AppCompatActivity {
    ActivityDeliveryConfirmedBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String headerId = getIntent().getStringExtra("headerId");
        fetchDeliveryDetail(headerId);
    }

    private void fetchDeliveryDetail(String headerId){
        System.out.println(headerId);
        NotificationApi api = API.getClient().create(NotificationApi.class);
        Call<DeliveryConfirmedHeaderDto> call = api.getDeliveryConfirmedDetail(headerId);
        call.enqueue(new Callback<DeliveryConfirmedHeaderDto>() {
            @Override
            public void onResponse(Call<DeliveryConfirmedHeaderDto> call, Response<DeliveryConfirmedHeaderDto> response) {
                DeliveryConfirmedHeaderDto dto = response.body();
                loadPage(dto);
            }

            @Override
            public void onFailure(Call<DeliveryConfirmedHeaderDto> call, Throwable t) {
                call.cancel(); finish();
                CommonUtil.showToast(getApplicationContext(),
                        "Failed to fetch Delivery Detail",false);
            }
        });
    }

    private void loadPage(DeliveryConfirmedHeaderDto dto){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_delivery_confirmed);
        binding.setVm(dto);
        binding.setActivity(this);
        binding.executePendingBindings();
        loadDeliveryLineList((ArrayList<DeliveryConfirmedLineDto>) dto.getDeliveryConfirmedLines());
    }

    private void loadDeliveryLineList(ArrayList<DeliveryConfirmedLineDto> lines){
        RecyclerView recyclerView = binding.deliveryLinesList;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DeliveryConfirmedLineAdapter adapter = new DeliveryConfirmedLineAdapter(this,lines);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this, layoutManager.getOrientation()));
    }
}