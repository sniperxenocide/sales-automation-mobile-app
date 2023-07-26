package com.akg.akg_sales.view.adapter.order;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
import com.akg.akg_sales.view.dialog.ItemQuantityDialog;

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
        holder.itemBinding.cardItem.setOnClickListener((v)->onItemSelect(header));
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

    private void onItemSelect(ItemDto itemDto){
        if(itemExistInCart(itemDto)){
            CommonUtil.showToast(activity,"Item Already Exist in Cart",false);
        }
        else {
            ItemQuantityDialog dialog = new ItemQuantityDialog(activity, itemDto.getItemDescription(),
                    itemDto.getPrimaryUom(),0,"Add to Cart",
                    qty->{
                        ArrayList<CartItemDto> cartItems = CommonUtil.orderCart.get(CommonUtil.selectedCustomer.getId());
                        if(cartItems==null) cartItems = new ArrayList<>();
                        cartItems.add(new CartItemDto(CommonUtil.selectedCustomer,itemDto,qty));
                        CommonUtil.orderCart.put(CommonUtil.selectedCustomer.getId(), cartItems);

                        CommonUtil.showToast(activity, "Item Added to Cart",true);
                        activity.updateCartBtnLabel();
            });
        }
    }

    private boolean itemExistInCart(ItemDto itemDto){
        ArrayList<CartItemDto> cartItems = CommonUtil.orderCart.get(CommonUtil.selectedCustomer.getId());
        if(cartItems==null || cartItems.isEmpty()) return false;
        for(CartItemDto c:cartItems){
            if(c.getItemDto().getId().intValue()==itemDto.getId().intValue()) return true;
        }
        return false;
    }

    private void handleItemQuantityChange(ItemDto header,ListitemOrderSkuBinding itemBinding){
        try {
//            if(activity.itemListQty.get(header.getId())==null) {
//                activity.itemListQty.put(header.getId(),0);
//            }
//            itemBinding.quantity.setText(String.valueOf(activity.itemListQty.get(header.getId())));
            itemBinding.quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        System.out.println(header.getId()+" "+header.getItemDescription()+" "+editable.toString());
                        //activity.itemListQty.put(header.getId(),Integer.parseInt(editable.toString()));
                        //System.out.println(activity.itemListQty);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }catch (Exception e){e.printStackTrace();}
    }
}
