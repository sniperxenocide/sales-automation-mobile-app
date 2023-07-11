package com.akg.akg_sales.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemStatusFlowItemBinding;
import com.akg.akg_sales.dto.StatusFlow;

import java.util.ArrayList;

public class StatusFlowListAdapter extends RecyclerView.Adapter<StatusFlowListAdapter.ViewHolder>{
    private final ArrayList<StatusFlow> statusFlows;
    private final Context context;

    public StatusFlowListAdapter(ArrayList<StatusFlow> statusFlows,Context context){
        this.statusFlows = statusFlows;
        this.context = context;
    }

    @NonNull
    @Override
    public StatusFlowListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemStatusFlowItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_status_flow_item,
                parent,false);
        return new StatusFlowListAdapter.ViewHolder(itemBinding);
    }



    @Override
    public void onBindViewHolder(@NonNull StatusFlowListAdapter.ViewHolder holder, int position) {
        StatusFlow status = statusFlows.get(position);
        holder.bind(status,position);
    }


    @Override
    public int getItemCount() {
        return statusFlows.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemStatusFlowItemBinding itemBinding;
        public ViewHolder(ListitemStatusFlowItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
        public void bind(Object obj,int pos) {
            itemBinding.setVariable(BR.vm, obj);
            if(pos == statusFlows.size()-1) itemBinding.gapDot.setVisibility(View.GONE);
            itemBinding.executePendingBindings();
        }
    }


}
