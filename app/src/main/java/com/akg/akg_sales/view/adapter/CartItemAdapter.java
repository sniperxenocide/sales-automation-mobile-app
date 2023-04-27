package com.akg.akg_sales.view.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemCartItemBinding;
import com.akg.akg_sales.dto.order.CartItemDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.CartActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.ItemQuantityDialog;

import java.util.ArrayList;
import java.util.Objects;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder>{
    private final ArrayList<CartItemDto> headers;
    private final CartActivity activity;

    public CartItemAdapter(CartActivity activity,ArrayList<CartItemDto> objects){
        this.headers = objects;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemCartItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_cart_item,
                parent,false);
        return new CartItemAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolder holder, int position) {
        CartItemDto header = headers.get(position);
        holder.bind(header,Integer.toString(position+1));
        holder.itemBinding.deleteCartItem.setOnClickListener(view -> {
            new ConfirmationDialog(activity,"Delete Item from Cart?",i->{
                CommonUtil.cartItems.remove(header);
                activity.loadCartListView();
            });
        });
        holder.itemBinding.quantity.setOnClickListener(view -> onClickQuantityUpdate(header));
    }


    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemCartItemBinding itemBinding;
        public ViewHolder(ListitemCartItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj,String idx) {
            itemBinding.setVariable(BR.vm, obj);
            itemBinding.setVariable(BR.index, idx);
            itemBinding.executePendingBindings();
        }
    }

    private void onClickQuantityUpdate(CartItemDto cartItem){
        ItemQuantityDialog dialog = new ItemQuantityDialog(activity, cartItem.getItemDto().getItemDescription(),
                cartItem.getItemDto().getPrimaryUom(),cartItem.getQuantity(), "Update Quantity",
                qty->{
                    for (CartItemDto i:CommonUtil.cartItems) {
                        if(Objects.equals(i.getItemDto().getId(), cartItem.getItemDto().getId())){
                            i.setQuantity(qty);
                            break;
                        }
                    }
                    try {
                        Objects.requireNonNull(activity.recyclerView.getAdapter())
                                .notifyItemChanged(headers.indexOf(cartItem));
                    }catch (Exception ignored){}
                });
    }
}
