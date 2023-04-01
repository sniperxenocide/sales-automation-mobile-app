package com.akg.akg_sales.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.WindowManager;

import com.akg.akg_sales.databinding.DialogOrderItemQtyBinding;
import com.akg.akg_sales.dto.ItemDto;
import com.akg.akg_sales.util.CommonUtil;

public class OrderItemQuantityDialog {
    public String itemName;
    public int qty;
    public String uom;
    private Context context;
    private Dialog dialog;

    public OrderItemQuantityDialog(Context context, ItemDto itemDto){
        this.context=context;
        this.itemName=itemDto.getDescription();
        this.uom=itemDto.getUom();
        dialog=new Dialog(context);
        DialogOrderItemQtyBinding binding = DialogOrderItemQtyBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        binding.setVm(this);
        CommonUtil.setDialogWindowParams(context,dialog);
    }

    public void onClickAddToCart(){
        CommonUtil.showToast(context,"Item Added to Cart",true);
        dialog.dismiss();
    }
}
