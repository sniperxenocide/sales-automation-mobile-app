package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.ItemApi;
import com.akg.akg_sales.databinding.DialogItemFilterBinding;
import com.akg.akg_sales.dto.item.ItemSubTypeDto;
import com.akg.akg_sales.dto.item.ItemTypeDto;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemFilterDialog {
    DialogItemFilterBinding binding;
    private OrderActivity activity;
    private Dialog dialog;
    private Long selectedTypeId;
    private Long selectedSubTypeId;
    List<ItemTypeDto> itemTypes;

    public ItemFilterDialog(OrderActivity activity){
        this.activity=activity;
        dialog=new Dialog(activity);
        binding = DialogItemFilterBinding.inflate(LayoutInflater.from(activity));
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(this.activity,this.dialog);
        binding.applyBtn.setOnClickListener(view -> onClickApply());
        loadItemType();
    }

    private void loadItemType(){
        itemTypes = activity.itemTypes;
        AutoCompleteTextView tView=binding.productTypeDropdown;
        String[] productTypes = new String[itemTypes.size()];
        for (int i=0;i< itemTypes.size();i++) productTypes[i]=itemTypes.get(i).getType();
        ArrayAdapter<String> ptAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, productTypes);
        tView.setAdapter(ptAdapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            selectedTypeId = itemTypes.get(i).getId();
            //loadItemSubType(i);
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


    private void onClickApply(){
        if(selectedTypeId==null){
            CommonUtil.showToast(activity,"Must Select all options",false);
            return;
        }
        activity.fetchItemFromServer(selectedTypeId);
        dialog.dismiss();
    }

}
