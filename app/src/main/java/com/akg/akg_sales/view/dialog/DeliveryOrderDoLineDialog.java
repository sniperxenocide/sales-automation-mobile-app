package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import com.akg.akg_sales.databinding.DialogDeliveryOrderDoLineBinding;
import com.akg.akg_sales.dto.delivery.DeliveryOrderDto;
import com.akg.akg_sales.dto.order.OrderLineDto;
import com.akg.akg_sales.view.activity.order.OrderDetailActivity;

public class DeliveryOrderDoLineDialog {
    public Dialog dialog;
    private String page;
    DialogDeliveryOrderDoLineBinding binding;
    OrderDetailActivity activity;

    public DeliveryOrderDoLineDialog(OrderDetailActivity activity){
        this.activity = activity;
        dialog=new Dialog(activity);
        generateTable();
    }

    public void show(){
        binding = DialogDeliveryOrderDoLineBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(0.95*displayMetrics.widthPixels);
        int height = (int)(0.9*displayMetrics.heightPixels);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        binding.webview.loadDataWithBaseURL(null, page, "text/html", "UTF-8", null);
        binding.webview.getSettings().setBuiltInZoomControls(true);
        binding.webview.getSettings().setDisplayZoomControls(true);
    }

    private void generateTable(){
        String style = "<style>" +
                "@media print { " +
                "   table { page-break-after:auto } " +
                "   tr { page-break-inside:avoid; page-break-after:auto } " +
                "   td    { page-break-inside:avoid; page-break-after:auto } " +
                "   thead { display:table-header-group }"+
                "} " +
                "table, th, td " +
                "{ padding:10px;" +
                "  border: 1px solid black;" +
                "  border-collapse:collapse;" +
                "  width:100%;" +
                "}</style>";
        String header = "<tr><th></th><th>DO Number</th>" +
                "<th>Item</th><th>Qty</th><th>UOM</th></tr>";

        StringBuilder bb = new StringBuilder();
        int sl = 1;
        for(DeliveryOrderDto d:activity.orderDto.getDeliveryOrders()){
            bb.append("<tr>");
            bb.append("<td>").append(sl).append(". ").append("</td>");
            bb.append("<td>").append(d.getDoNumber()).append("</td>");
            bb.append("<td>").append(d.getItemDescription()).append("</td>");
            bb.append("<td>").append(d.getDoQuantity()).append("</td>");
            bb.append("<td>").append(d.getUomCode()).append("</td>");
            bb.append("</tr>");
            sl++;
        }
        String body = bb.toString();
        page = style+"<table>"+header+body+"</table>";
    }
}
