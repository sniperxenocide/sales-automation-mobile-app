package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.core.util.Consumer;

import com.akg.akg_sales.databinding.DialogItemQtyBinding;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.util.CommonUtil;

public class ItemQuantityDialog {
    DialogItemQtyBinding binding;
    public String itemName;
    public String itemUom;
    public Double quantity;
    public String btnLabel;
    private Context context;
    private Dialog dialog;
    Consumer<Double> callback;

    public ItemQuantityDialog(
            Context context, String itemName,String itemUom,
            Double quantity,String btnLabel,Consumer<Double> callback){
        this.context = context;
        this.itemName = itemName;
        this.itemUom = itemUom;
        this.quantity= quantity;
        this.btnLabel = btnLabel;
        this.callback = callback;

        dialog=new Dialog(context);
        binding = DialogItemQtyBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        binding.setVm(this);
        CommonUtil.setDialogWindowParams(context,dialog);
    }

    public void onClickAddToCart(){
        try {
            double qty = Double.parseDouble(binding.quantity.getText().toString());
            if(qty<=0) throw new Exception("Invalid Quantity");
            this.callback.accept(qty);
            dialog.dismiss();
        }catch (Exception e){
            CommonUtil.showToast(dialog.getContext(),e.getMessage(),false);
        }
    }


}
