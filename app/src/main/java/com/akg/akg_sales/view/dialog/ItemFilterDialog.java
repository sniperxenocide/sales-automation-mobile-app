package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.ItemApi;
import com.akg.akg_sales.databinding.DialogItemFilterBinding;
import com.akg.akg_sales.dto.item.ItemBrandDto;
import com.akg.akg_sales.dto.item.ItemColorDto;
import com.akg.akg_sales.dto.item.ItemSubTypeDto;
import com.akg.akg_sales.dto.item.ItemTypeDto;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemFilterDialog {
    DialogItemFilterBinding binding;
    private OrderActivity activity;
    private Dialog dialog;

    private Long selectedTypeId;
    private Long selectedBrandId;
    private Long selectedColorId;

    List<ItemTypeDto> itemTypes;
    List<ItemBrandDto> itemBrands;
    List<ItemColorDto> itemColors;

    Map<String,String> filterParam = new HashMap<>();
    boolean onlyOneFilter = true;

    public ItemFilterDialog(OrderActivity activity){
        this.activity=activity;
        dialog=new Dialog(activity);
        binding = DialogItemFilterBinding.inflate(LayoutInflater.from(activity));
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(this.activity,this.dialog);
        binding.applyBtn.setOnClickListener(view -> onClickApply());
        loadItemType();
        loadItemBrand();
        loadItemColor();
    }

    private void loadItemType(){
        itemTypes = activity.itemMaster.getItemTypes();
        AutoCompleteTextView tView=binding.productTypeDropdown;
        String[] productTypes = new String[itemTypes.size()];
        for (int i=0;i< itemTypes.size();i++) productTypes[i]=itemTypes.get(i).getType();
        ArrayAdapter<String> ptAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, productTypes);
        tView.setAdapter(ptAdapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedTypeId = itemTypes.get(i).getId();
            if(onlyOneFilter) onClickApply();
        });
    }

    private void loadItemBrand(){
        itemBrands = activity.itemMaster.getItemBrands();
        if(itemBrands==null || itemBrands.isEmpty()){
            binding.itemBrandDropdownContainer.setVisibility(View.GONE);
            return;
        }
        onlyOneFilter=false;

        AutoCompleteTextView tView=binding.itemBrandDropdown;
        String[] itemBrandList = new String[itemBrands.size()];
        for (int i=0;i< itemBrands.size();i++) itemBrandList[i]=itemBrands.get(i).getBrand();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, itemBrandList);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedBrandId = itemBrands.get(i).getId();
        });
    }

    private void loadItemColor(){
        itemColors = activity.itemMaster.getItemColors();
        if(itemColors==null || itemColors.isEmpty()){
            binding.itemColorDropdownContainer.setVisibility(View.GONE);
            return;
        }
        onlyOneFilter=false;

        AutoCompleteTextView tView=binding.itemColorDropdown;
        String[] itemColorList = new String[itemColors.size()];
        for (int i=0;i< itemColors.size();i++) itemColorList[i]=itemColors.get(i).getColor();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, itemColorList);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedColorId = itemColors.get(i).getId();
        });
    }


    private void onClickApply(){
        if(selectedTypeId==null){
            CommonUtil.showToast(activity,"Must Select Item Type",false);
            return;
        }
        filterParam.put("typeId",selectedTypeId.toString());
        if(selectedBrandId!=null) filterParam.put("brandId",selectedBrandId.toString());
        if(selectedColorId!=null) filterParam.put("colorId",selectedColorId.toString());
        activity.fetchItemFromServer(filterParam);
        dialog.dismiss();
    }

}
