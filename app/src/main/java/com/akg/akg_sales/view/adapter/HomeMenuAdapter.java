package com.akg.akg_sales.view.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ListitemHomepageMenuBinding;
import com.akg.akg_sales.dto.HomeMenuItem;
import com.akg.akg_sales.view.activity.ComplainManagementActivity;
import com.akg.akg_sales.view.activity.HomeActivity;
import com.akg.akg_sales.view.activity.ReportWebActivity;

import java.util.ArrayList;

public class HomeMenuAdapter extends ArrayAdapter<HomeMenuItem> {
    private final HomeActivity activity;

    public HomeMenuAdapter(HomeActivity activity, ArrayList<HomeMenuItem> list){
        super(activity,0,list);
        this.activity=activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ListitemHomepageMenuBinding binding;
        View view = convertView;
        if (view == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.listitem_homepage_menu, parent,false);
            view = binding.getRoot();
            view.setTag(binding);
        }
        else {
            binding = (ListitemHomepageMenuBinding) view.getTag();
        }

        HomeMenuItem item = getItem(position);
        binding.icon.setBackgroundResource(item.getIcon());
        binding.title.setText(item.getTitle());

        view.setOnClickListener(v -> {
            Intent intent = new Intent(activity, item.getActivityClass());
            activity.startActivity(intent);
        });

        return view;
    }

}
