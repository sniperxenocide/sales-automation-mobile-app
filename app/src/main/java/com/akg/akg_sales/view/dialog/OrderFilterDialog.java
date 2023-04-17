package com.akg.akg_sales.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.ItemApi;
import com.akg.akg_sales.databinding.DialogOrderFilterBinding;
import com.akg.akg_sales.databinding.DialogOrderItemQtyBinding;
import com.akg.akg_sales.dto.item.ItemSubTypeDto;
import com.akg.akg_sales.dto.item.ItemTypeDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFilterDialog {
    DialogOrderFilterBinding binding;
    private OrderActivity activity;
    private Dialog dialog;
    private List<ItemTypeDto> itemTypes;
    private Long selectedSubTypeId;

    public OrderFilterDialog(OrderActivity activity){
        this.activity=activity;
        dialog=new Dialog(activity);
        binding = DialogOrderFilterBinding.inflate(LayoutInflater.from(activity));
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(this.activity,this.dialog);
        binding.applyBtn.setOnClickListener(view -> onClickApply());
        fetchData();
    }

    private void loadItemType(){
        AutoCompleteTextView tView=binding.productTypeDropdown;
        String[] productTypes = new String[itemTypes.size()];
        for (int i=0;i< itemTypes.size();i++) productTypes[i]=itemTypes.get(i).getType();
        ArrayAdapter<String> ptAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, productTypes);
        tView.setAdapter(ptAdapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            loadItemSubType(i);
        });
    }

    private void loadItemSubType(int i){
        List<ItemSubTypeDto> subTypes = itemTypes.get(i).getSubTypes();
        AutoCompleteTextView tView= binding.productSubtypeDropdown;
        tView.setText("");selectedSubTypeId=null;
        String[] productSubtypes = new String[subTypes.size()];
        for (int j=0;j< subTypes.size();j++) productSubtypes[j]=subTypes.get(j).getSubType();
        ArrayAdapter<String> pstAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, productSubtypes);
        tView.setAdapter(pstAdapter);
        tView.setOnItemClickListener((adapterView, view, j, l) -> selectedSubTypeId=subTypes.get(j).getId());
    }

    private void fetchData(){
        API.getClient().create(ItemApi.class)
                .getItemTypes(activity.selectedCustomer.getId())
                .enqueue(new Callback<List<ItemTypeDto>>() {
                    @Override
                    public void onResponse(Call<List<ItemTypeDto>> call, Response<List<ItemTypeDto>> response) {
                        itemTypes = response.body();
                        loadItemType();
                    }
                    @Override
                    public void onFailure(Call<List<ItemTypeDto>> call, Throwable t) {
                        call.cancel();
                    }
                });
    }

    private void onClickApply(){
        if(selectedSubTypeId==null){
            CommonUtil.showToast(activity,"Must Select all options",false);
            return;
        }
        activity.fetchItemFromServer(selectedSubTypeId);
        dialog.dismiss();
    }

}
