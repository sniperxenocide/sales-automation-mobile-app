package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import com.akg.akg_sales.databinding.DialogDeliveryDetailReportBinding;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;

public class DeliveryDetailReportDialog {
    DialogDeliveryDetailReportBinding binding;
    public Dialog dialog;
    DeliveryDetailActivity activity;
    private String table;

    public DeliveryDetailReportDialog(DeliveryDetailActivity activity){
        this.activity = activity;
        dialog=new Dialog(activity);
        generateTable();
    }

    public void showReport(){
        binding = DialogDeliveryDetailReportBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(0.95*displayMetrics.widthPixels);
        int height = (int)(0.9*displayMetrics.heightPixels);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        binding.webview.loadDataWithBaseURL(null, table, "text/html", "UTF-8", null);
        binding.webview.getSettings().setBuiltInZoomControls(true);
    }

    private void generateTable(){
        String style = "<style>" +
                "table, th, td " +
                "{ padding:10px;" +
                "  border: 1px solid black;" +
                "  border-collapse:collapse;" +
                "  width:100%;" +
                "}</style>";
        String header = "<tr><th>Customer</th><th>Order</th>" +
                "<th>DO</th><th>Item</th><th>Quantity</th></tr>";
        StringBuilder bb = new StringBuilder();
        String customer="";String order="";String doN="";
        for(MoveOrderConfirmedLineDto l:activity.deliveryLines){
            bb.append("<tr>");


            String c = l.getCustomerName()+" ("+l.getCustomerNumber()+")";
            if(!c.equals(customer)){
                bb.append("<td rowspan='"+activity.dataFrequency.get(c)+"' >");
                customer=c; bb.append(c);
                bb.append("</td>");
            }

            String o = l.getOrderNumber().toString();
            if(!o.equals(order)){
                bb.append("<td rowspan='"+activity.dataFrequency.get(o)+"' >");
                bb.append(o);order=o;
                bb.append("</td>");
            }

            String d = l.getDoNumber();
            if(!d.equals(doN)){
                bb.append("<td rowspan='"+activity.dataFrequency.get(d)+"' >");
                bb.append(d);doN=d;
                bb.append("</td>");
            }

            bb.append("<td>").append(l.getItemDescription()).append("</td>")
                    .append("<td>").append(l.getLineQuantity()).append(" ").append(l.getUomCode()).append("</td>")
                    .append("</tr>");
        }
        String body = bb.toString();
        table = "<table>"+style+header+body+"</table>";
    }
}
