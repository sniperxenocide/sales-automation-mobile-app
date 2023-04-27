package com.akg.akg_sales.view.adapter;

import android.content.Context;
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
import com.akg.akg_sales.view.dialog.ItemQuantityDialog;

import java.util.ArrayList;
import java.util.Objects;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{
    private ArrayList<ItemDto> headers;
    private final Context context;

    public OrderItemAdapter(Context context,ArrayList<ItemDto> objects){
        this.headers = objects;
        this.context = context;
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
        holder.itemBinding.cardItem
                .setOnClickListener((v)->onItemSelect(header));
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
            itemBinding.executePendingBindings();
        }
    }

    private void onItemSelect(ItemDto itemDto){
        if(itemExistInCart(itemDto)){
            CommonUtil.showToast(context,"Item Already Exist in Cart",false);
        }
        else {
            ItemQuantityDialog dialog = new ItemQuantityDialog(context, itemDto.getItemDescription(),
                    itemDto.getPrimaryUom(),0,"Add to Cart",
                    qty->{
                        CommonUtil.cartItems.add(
                                new CartItemDto(CommonUtil.selectedCustomer,itemDto,qty));
                        CommonUtil.showToast(context, "Item Added to Cart",true);
            });
        }
    }

    private boolean itemExistInCart(ItemDto itemDto){
        for (CartItemDto i: CommonUtil.cartItems) {
            if(Objects.equals(i.getItemDto().getId(), itemDto.getId())
                    && Objects.equals(i.getCustomerDto().getId(), CommonUtil.selectedCustomer.getId()))
                return true;
        }
        return false;
    }
}
