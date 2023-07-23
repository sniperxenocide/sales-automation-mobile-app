package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.databinding.DialogStatusFlowBinding;
import com.akg.akg_sales.dto.StatusFlow;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.StatusFlowListAdapter;

import java.util.ArrayList;

public class StatusFlowDialog {
    ArrayList<StatusFlow> statusFlows;
    Context context;
    Dialog dialog;
    DialogStatusFlowBinding binding;

    public StatusFlowDialog(ArrayList<StatusFlow> statusFlows, Context context){
        try {
            this.statusFlows = statusFlows;
            this.context = context;
            dialog=new Dialog(context);
            binding = DialogStatusFlowBinding.inflate(LayoutInflater.from(context));
            dialog.setContentView(binding.getRoot());
            CommonUtil.setDialogWindowParams(this.context,this.dialog);
//            dialog.show();
            loadUi();
        }catch (Exception e){e.printStackTrace();}
    }

    private void loadUi(){
        RecyclerView recyclerView = binding.statusFlowListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        StatusFlowListAdapter adapter = new StatusFlowListAdapter(statusFlows,context);
        recyclerView.setAdapter(adapter);
    }
}
