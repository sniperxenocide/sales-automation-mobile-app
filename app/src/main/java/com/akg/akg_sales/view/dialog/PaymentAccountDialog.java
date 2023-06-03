package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.util.Consumer;

import com.akg.akg_sales.databinding.DialogPaymentAccountBinding;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.payment.NewPaymentActivity;

import java.util.ArrayList;

public class PaymentAccountDialog {

    public PaymentAccountDialog(NewPaymentActivity activity, ArrayList<String> list,
                                Consumer<Integer> callback){
        Dialog dialog=new Dialog(activity);
        DialogPaymentAccountBinding binding = DialogPaymentAccountBinding.inflate(LayoutInflater.from(activity));
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(activity,dialog);

        ListView listView = binding.selectListview;
        ArrayAdapter<String> adapter=new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);
        binding.selectSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            callback.accept(list.indexOf(adapter.getItem(position)));
            dialog.dismiss();
        });
    }
}
