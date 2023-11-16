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
import com.akg.akg_sales.dto.item.ItemCategory;
import com.akg.akg_sales.dto.item.ItemColorDto;
import com.akg.akg_sales.dto.item.ItemSubTypeDto;
import com.akg.akg_sales.dto.item.ItemTypeDto;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderActivity;

import java.util.ArrayList;
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

    public ItemFilterDialog(OrderActivity activity){
        this.activity=activity;
        dialog=new Dialog(activity);
        binding = DialogItemFilterBinding.inflate(LayoutInflater.from(activity));
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(this.activity,this.dialog);
        binding.applyBtn.setOnClickListener(view -> onClickApply());
        setUiVisibility();
        loadItemType();
    }

    private void setUiVisibility(){
        if(!activity.itemMaster.getItemBrandActive())
            binding.itemBrandDropdownContainer.setVisibility(View.GONE);

        if(!activity.itemMaster.getItemColorActive())
            binding.itemColorDropdownContainer.setVisibility(View.GONE);
    }

    private void loadItemType(){
        itemTypes = new ArrayList<>();
        Map<Long,ItemTypeDto> typeMap = new HashMap<>();
        for(ItemCategory c:activity.itemMaster.getCategories())
            typeMap.put(c.getItemType().getId(),c.getItemType());
        for(Long k: typeMap.keySet()) itemTypes.add(typeMap.get(k));

        AutoCompleteTextView tView=binding.productTypeDropdown;
        String[] productTypes = new String[itemTypes.size()];
        for (int i=0;i< itemTypes.size();i++) productTypes[i]=itemTypes.get(i).getType();
        ArrayAdapter<String> ptAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, productTypes);
        tView.setAdapter(ptAdapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedTypeId = itemTypes.get(i).getId();
            if(activity.itemMaster.getItemBrandActive()) {
                loadItemBrand();
                loadItemColor();
            }
            else onClickApply();
        });
    }

    private void loadItemBrand(){
        itemBrands = new ArrayList<>();
        Map<Long,ItemBrandDto> brandMap = new HashMap<>();
        for(ItemCategory c:activity.itemMaster.getCategories()){
            try {
                if(c.getItemType().getId().longValue()==selectedTypeId)
                    brandMap.put(c.getItemBrand().getId(),c.getItemBrand());
            }catch (Exception ignored){}
        }
        for(Long k: brandMap.keySet()) itemBrands.add(brandMap.get(k));

        AutoCompleteTextView tView=binding.itemBrandDropdown;
        String[] itemBrandList = new String[itemBrands.size()];
        for (int i=0;i< itemBrands.size();i++) itemBrandList[i]=itemBrands.get(i).getBrand();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, itemBrandList);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedBrandId = itemBrands.get(i).getId();
            if(activity.itemMaster.getItemBrandActive()) loadItemColor();
        });
    }

    private void loadItemColor(){
        itemColors = new ArrayList<>();
        Map<Long,ItemColorDto> colorMap = new HashMap<>();
        for(ItemCategory c:activity.itemMaster.getCategories()){
            try {
                if((selectedBrandId==null || c.getItemBrand().getId().longValue()==selectedBrandId)
                        && c.getItemType().getId().longValue()==selectedTypeId )
                    colorMap.put(c.getItemColor().getId(),c.getItemColor());
            }catch (Exception ignored){}
        }
        for(Long k: colorMap.keySet()) itemColors.add(colorMap.get(k));

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
