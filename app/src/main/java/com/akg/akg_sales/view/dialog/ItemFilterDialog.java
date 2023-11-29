package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akg.akg_sales.databinding.DialogItemFilterBinding;
import com.akg.akg_sales.dto.item.ItemBrandDto;
import com.akg.akg_sales.dto.item.ItemCategory;
import com.akg.akg_sales.dto.item.ItemColorDto;
import com.akg.akg_sales.dto.item.ItemSubTypeDto;
import com.akg.akg_sales.dto.item.ItemTypeDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemFilterDialog {
    DialogItemFilterBinding binding;
    private OrderActivity activity;
    private Dialog dialog;

    private Long selectedTypeId;
    private Long selectedSubTypeId;
    private Long selectedBrandId;
    private Long selectedColorId;

    List<ItemTypeDto> itemTypes;
    List<ItemSubTypeDto> itemSubTypes;
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
        if(!activity.itemMaster.getItemSubTypeActive())
            binding.itemSubTypeDropdownContainer.setVisibility(View.GONE);

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
        Collections.sort(itemTypes,(a,b)-> a.getType().compareTo(b.getType()));

        AutoCompleteTextView tView=binding.productTypeDropdown;
        String[] productTypes = new String[itemTypes.size()];
        for (int i=0;i< itemTypes.size();i++) productTypes[i]=itemTypes.get(i).getType();
        ArrayAdapter<String> ptAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, productTypes);
        tView.setAdapter(ptAdapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> onSelectType(i));
    }

    private void loadItemSubType(){
        itemSubTypes = new ArrayList<>();
        Map<Long,ItemSubTypeDto> subTypeMap = new HashMap<>();
        for(ItemCategory c:activity.itemMaster.getCategories()){
            try {
                if(shouldSelectSubType(c)) subTypeMap.put(c.getItemSubType().getId(),c.getItemSubType());
            }catch (Exception ignored){}
        }
        for(Long k: subTypeMap.keySet()) itemSubTypes.add(subTypeMap.get(k));
        Collections.sort(itemSubTypes,(a,b)-> a.getSubType().compareTo(b.getSubType()));

        AutoCompleteTextView tView=binding.itemSubTypeDropdown;
        String[] itemSubTypeList = new String[itemSubTypes.size()];
        for (int i=0;i< itemSubTypes.size();i++) itemSubTypeList[i]=itemSubTypes.get(i).getSubType();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, itemSubTypeList);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> onSelectSubType(tView,i,itemSubTypeList[i]));

        tView.setText("");
        selectedSubTypeId=null;
    }

    private void loadItemBrand(){
        itemBrands = new ArrayList<>();
        Map<Long,ItemBrandDto> brandMap = new HashMap<>();
        for(ItemCategory c:activity.itemMaster.getCategories()){
            try {
                if(shouldSelectBrand(c)) brandMap.put(c.getItemBrand().getId(),c.getItemBrand());
            }catch (Exception ignored){}
        }
        for(Long k: brandMap.keySet()) itemBrands.add(brandMap.get(k));
        Collections.sort(itemBrands,(a,b)-> a.getBrand().compareTo(b.getBrand()));

        AutoCompleteTextView tView=binding.itemBrandDropdown;
        String[] itemBrandList = new String[itemBrands.size()];
        for (int i=0;i< itemBrands.size();i++) itemBrandList[i]=itemBrands.get(i).getBrand();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, itemBrandList);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> onSelectBrand(tView,i,itemBrandList[i]));

        tView.setText("");
        selectedBrandId=null;
    }

    private void loadItemColor(){
        itemColors = new ArrayList<>();
        Map<Long,ItemColorDto> colorMap = new HashMap<>();
        for(ItemCategory c:activity.itemMaster.getCategories()){
            try {
                if(shouldSelectColor(c) ) colorMap.put(c.getItemColor().getId(),c.getItemColor());
            }catch (Exception ignored){}
        }
        for(Long k: colorMap.keySet()) itemColors.add(colorMap.get(k));
        Collections.sort(itemColors,(a,b)-> a.getColor().compareTo(b.getColor()));

        AutoCompleteTextView tView=binding.itemColorDropdown;
        String[] itemColorList = new String[itemColors.size()];
        for (int i=0;i< itemColors.size();i++) itemColorList[i]=itemColors.get(i).getColor();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, itemColorList);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> onSelectColor(tView,i,itemColorList[i]));

        tView.setText("");
        selectedColorId=null;
    }


    private void onClickApply(){
        if(selectedTypeId==null){
            CommonUtil.showToast(activity,"Must Select Item Type",false);
            return;
        }
        filterParam.put("typeId",selectedTypeId.toString());
        if(selectedSubTypeId!=null) filterParam.put("subTypeId",selectedSubTypeId.toString());
        if(selectedBrandId!=null) filterParam.put("brandId",selectedBrandId.toString());
        if(selectedColorId!=null) filterParam.put("colorId",selectedColorId.toString());
        activity.fetchItemFromServer(filterParam);
        dialog.dismiss();
    }

    private void onSelectType(int i){
        selectedTypeId = itemTypes.get(i).getId();
        if(activity.itemMaster.getItemSubTypeActive()) loadItemSubType();
        if(activity.itemMaster.getItemBrandActive()) loadItemBrand();
        if(activity.itemMaster.getItemColorActive()) loadItemColor();

        if(!(activity.itemMaster.getItemSubTypeActive() && activity.itemMaster.getItemBrandActive()
                && activity.itemMaster.getItemColorActive())) onClickApply();
    }

    private void onSelectSubType(AutoCompleteTextView tView,int i,String txt){
        try {
            selectedSubTypeId = itemSubTypes.get(i).getId();
            tView.setText(txt,false);
            if(activity.itemMaster.getItemBrandActive()) loadItemBrand();
            if(activity.itemMaster.getItemColorActive()) loadItemColor();
        }catch (Exception ignored){}
    }

    private void onSelectBrand(AutoCompleteTextView tView,int i,String txt){
        try {
            selectedBrandId = itemBrands.get(i).getId();
            tView.setText(txt,false);
            if(activity.itemMaster.getItemColorActive()) loadItemColor();
        }catch (Exception ignored){}
    }

    private void onSelectColor(AutoCompleteTextView tView,int i,String txt){
        try {
            selectedColorId = itemColors.get(i).getId();
            tView.setText(txt,false);
        }catch (Exception ignored){}
    }

    private boolean shouldSelectSubType(ItemCategory c){
        return c.getItemType().getId().longValue()==selectedTypeId;
    }

    private boolean shouldSelectBrand(ItemCategory c){
        return shouldSelectSubType(c)
                && (selectedSubTypeId==null || c.getItemSubType().getId().longValue()==selectedSubTypeId);
    }

    private boolean shouldSelectColor(ItemCategory c){
        return shouldSelectBrand(c)
                && (selectedBrandId==null || c.getItemBrand().getId().longValue()==selectedBrandId);

    }

}
