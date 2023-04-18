package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import com.akg.akg_sales.databinding.DialogOrderItemQtyBinding;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.util.CommonUtil;

import java.util.Objects;

public class OrderItemQuantityDialog {
    DialogOrderItemQtyBinding binding;
    public ItemDto itemDto;
    private Context context;
    private Dialog dialog;

    public OrderItemQuantityDialog(Context context, ItemDto itemDto){
        this.context=context;
        this.itemDto=itemDto;
        dialog=new Dialog(context);
        binding = DialogOrderItemQtyBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        binding.setVm(this);
        CommonUtil.setDialogWindowParams(context,dialog);
    }

    public void onClickAddToCart(){
        try {
            int qty = Integer.parseInt(binding.quantity.getText().toString());
            if(qty<=0) throw new Exception("Invalid Quantity");
            if(itemExistInCart(itemDto)) throw new Exception("Item Already Exist in Cart");
            CommonUtil.cartItems.add(
                    new CartItemDto(CommonUtil.selectedCustomer,itemDto,qty));
            CommonUtil.showToast(dialog.getContext(), "Item Added to Cart",true);
            dialog.dismiss();
        }catch (Exception e){
            CommonUtil.showToast(dialog.getContext(),e.getMessage(),false);
        }
    }

    private boolean itemExistInCart(ItemDto itemDto){
        for (CartItemDto i:CommonUtil.cartItems) {
            if(Objects.equals(i.getItemDto().getId(), itemDto.getId())
            && Objects.equals(i.getCustomerDto().getId(), CommonUtil.selectedCustomer.getId()))
                return true;
        }
        return false;
    }
}
