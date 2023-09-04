package com.akg.akg_sales.view.adapter.order;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemOrderSkuBinding;
import com.akg.akg_sales.dto.item.ItemDto;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.OrderActivity;

import java.util.ArrayList;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{
    private ArrayList<ItemDto> headers;
    private final OrderActivity activity;

    public OrderItemAdapter(OrderActivity activity,ArrayList<ItemDto> objects){
        this.headers = objects;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemOrderSkuBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_order_sku,
                parent,false);
        return new OrderItemAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.ViewHolder holder, int position) {
        ItemDto header = headers.get(position);
        holder.bind(header,Integer.toString(position+1));
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemOrderSkuBinding itemBinding;
        public ViewHolder(ListitemOrderSkuBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj,String idx) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.setVariable(BR.index, idx);
            handleItemQuantityChange((ItemDto) obj,itemBinding);
            itemBinding.executePendingBindings();
        }
    }


    private CartItemDto itemExistInCart(ItemDto itemDto){
        ArrayList<CartItemDto> cartItems = CommonUtil.orderCart
                .get(activity.customerList.get(activity.selectedCustomerIdx).getId());
        if(cartItems==null || cartItems.isEmpty()) return null;
        for(CartItemDto c:cartItems){
            if(c.getItemDto().getId().longValue()==itemDto.getId().longValue()) return c;
        }
        return null;
    }

    private void handleItemQuantityChange(ItemDto itemDto,ListitemOrderSkuBinding itemBinding){
        EditText editTextQuantity = itemBinding.quantity;
        try {
            CartItemDto existingItem = itemExistInCart(itemDto);
            if(existingItem!=null) {
                editTextQuantity.setText(String.valueOf(existingItem.getQuantity()));
                editTextQuantity.setBackgroundResource(R.drawable.deep_green_background);
            }
        }catch (Exception e){e.printStackTrace();}
        try {
            editTextQuantity.setOnKeyListener((v, keyCode, event) -> {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    try {
                        if(editTextQuantity.getText().length()<=0 ||
                                Double.parseDouble(String.valueOf(editTextQuantity.getText()))<=0){
                            CartItemDto cartItemDto = itemExistInCart(itemDto);
                            ArrayList<CartItemDto> cList = CommonUtil.orderCart
                                    .get(activity.customerList.get(activity.selectedCustomerIdx).getId());
                            if(cartItemDto!=null && cList!=null) {
                                cList.remove(cartItemDto);
                                editTextQuantity.setBackgroundResource(R.drawable.white_background);
                                activity.updateCartBtnLabel();

                                System.out.println("Item Removed from Cart "+itemDto.getItemDescription());
                                CommonUtil.printCart();
                            }
                            editTextQuantity.getText().clear();
                            return false;
                        }
                        Double qty = Double.parseDouble(String.valueOf(editTextQuantity.getText()));
                        ArrayList<CartItemDto> cartItems = CommonUtil.orderCart
                                .get(activity.customerList.get(activity.selectedCustomerIdx).getId());
                        if(cartItems==null) cartItems = new ArrayList<>();
                        CartItemDto existingItem = itemExistInCart(itemDto);
                        if(existingItem==null) cartItems.add(new CartItemDto(activity.customerList.get(activity.selectedCustomerIdx),itemDto,qty));
                        else existingItem.setQuantity(qty);
                        CommonUtil.orderCart.put(activity.customerList.get(activity.selectedCustomerIdx).getId(), cartItems);
                        editTextQuantity.setBackgroundResource(R.drawable.deep_green_background);
                        CommonUtil.showToast(activity, "Item Added to Cart. "+
                                itemDto.getItemDescription()+" "+qty+" "+itemDto.getPrimaryUom(),true);
                        activity.updateCartBtnLabel();
                        CommonUtil.printCart();
                    }catch (Exception e){e.printStackTrace();}

                    return true;
                }
                return false;
            });
        }catch (Exception e){e.printStackTrace();}
    }
}
