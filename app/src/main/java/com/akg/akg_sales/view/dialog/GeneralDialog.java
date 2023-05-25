package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.util.Consumer;

import com.akg.akg_sales.databinding.DialogConfirmationBinding;
import com.akg.akg_sales.databinding.DialogGeneralBinding;
import com.akg.akg_sales.util.CommonUtil;

public class GeneralDialog {
    public String msg;
    private Context context;
    private Dialog dialog;

    public GeneralDialog(Context context, String msg){
        this.context=context;
        this.msg=msg;
        dialog=new Dialog(context);
        DialogGeneralBinding binding = DialogGeneralBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        binding.dialogMsg.setText(msg);
        binding.okBtn.setOnClickListener(view -> dialog.dismiss());
        CommonUtil.setDialogWindowParams(context,dialog);
    }
}
