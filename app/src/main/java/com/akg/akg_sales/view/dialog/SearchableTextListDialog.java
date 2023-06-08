package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.util.Consumer;

import com.akg.akg_sales.databinding.DialogSearchableTextListBinding;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.payment.NewPaymentActivity;

import java.util.ArrayList;

public class SearchableTextListDialog {

    public SearchableTextListDialog(Context context, ArrayList<String> list,
                                    Consumer<Integer> callback){
        Dialog dialog=new Dialog(context);
        DialogSearchableTextListBinding binding = DialogSearchableTextListBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(context,dialog);

        ListView listView = binding.selectListview;
        ArrayAdapter<String> adapter=new ArrayAdapter<>(context, android.R.layout.simple_list_item_1,list);
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
