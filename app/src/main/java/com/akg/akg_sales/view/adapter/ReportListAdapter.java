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
import com.akg.akg_sales.databinding.ListitemReportBinding;
import com.akg.akg_sales.dto.report.ReportDto;
import com.akg.akg_sales.view.activity.ReportActivity;
import com.akg.akg_sales.view.dialog.ReportViewDialog;

import java.util.ArrayList;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ViewHolder>{
    private ArrayList<ReportDto> headers;
    private ReportActivity activity;

    public ReportListAdapter(ReportActivity activity,ArrayList<ReportDto> objects){
        this.headers = objects;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ReportListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListitemReportBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.listitem_report,
                parent,false);
        return new ReportListAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportListAdapter.ViewHolder holder, int position) {
        ReportDto header = headers.get(position);
        holder.bind(header,Integer.toString(position+1));
        holder.binding.cardItem.setOnClickListener(view -> {
            ReportViewDialog reportViewDialog = new ReportViewDialog(activity,header);
            reportViewDialog.showReport();
        });
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ListitemReportBinding binding;
        public ViewHolder(ListitemReportBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Object obj,String idx) {
            binding.setVariable(BR.vm, obj);
            binding.setVariable(BR.index, idx);
            binding.executePendingBindings();
        }
    }
}
