package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.core.util.Consumer;

import com.akg.akg_sales.databinding.DialogConfirmationBinding;
import com.akg.akg_sales.util.CommonUtil;

public class ConfirmationDialog {
    public String msg;
    private Context context;
    private Dialog dialog;
    Consumer<Integer> callback;

    public ConfirmationDialog(Context context, String msg, Consumer<Integer> callback){
        this.context=context;
        this.msg=msg;
        this.callback=callback;
        dialog=new Dialog(context);
        DialogConfirmationBinding binding = DialogConfirmationBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        binding.setVm(this);
        CommonUtil.setDialogWindowParams(context,dialog);
    }

    public void onClickYesBtn(){
        this.callback.accept(1);
        dialog.dismiss();
    }

    public void onClickNoBtn(){
        dialog.dismiss();
    }


}
