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
    public ItemDto itemDto;
    public Integer quantity;
    public String btnLabel;
    private Context context;
    private Dialog dialog;
    Consumer<Integer> callback;

    public ItemQuantityDialog(
            Context context, ItemDto itemDto,
            Integer quantity,String btnLabel,Consumer<Integer> callback){
        this.context = context;
        this.itemDto = itemDto;
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
            int qty = Integer.parseInt(binding.quantity.getText().toString());
            if(qty<=0) throw new Exception("Invalid Quantity");
            this.callback.accept(qty);
            dialog.dismiss();
        }catch (Exception e){
            CommonUtil.showToast(dialog.getContext(),e.getMessage(),false);
        }
    }


}
