package com.akg.akg_sales.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.DialogOrderFilterBinding;
import com.akg.akg_sales.databinding.DialogOrderItemQtyBinding;
import com.akg.akg_sales.util.CommonUtil;

public class OrderFilterDialog {
    private Context context;
    private Dialog dialog;

    public OrderFilterDialog(Context context){
        this.context=context;
        dialog=new Dialog(context);
        DialogOrderFilterBinding binding = DialogOrderFilterBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(this.context,this.dialog);
        loadFilters();
    }

    private void loadFilters(){
        AutoCompleteTextView businessUnitView=dialog.findViewById(R.id.business_unit_dropdown);
        String[] business = new String[]{"CGD", "Shah Cement", "Steel","Ceramic"};
        ArrayAdapter<String> buAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, business);
        businessUnitView.setAdapter(buAdapter);

        AutoCompleteTextView productTypeView=dialog.findViewById(R.id.product_type_dropdown);
        String[] productTypes = new String[]{"Condensed", "Powder", "Tea","Beverage"};
        ArrayAdapter<String> ptAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, productTypes);
        productTypeView.setAdapter(ptAdapter);

        AutoCompleteTextView productSubtypeView=dialog.findViewById(R.id.product_subtype_dropdown);
        String[] productSubtypes = new String[]{"CGD", "Shah Cement", "Steel","Ceramic"};
        ArrayAdapter<String> pstAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, productSubtypes);
        productSubtypeView.setAdapter(pstAdapter);
    }

}
