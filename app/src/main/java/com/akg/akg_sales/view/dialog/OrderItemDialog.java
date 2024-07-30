package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import com.akg.akg_sales.databinding.DialogOrderItemBinding;
import com.akg.akg_sales.dto.order.OrderLineDto;
import com.akg.akg_sales.view.activity.order.OrderDetailActivity;

public class OrderItemDialog {
    public Dialog dialog;
    private String page;
    DialogOrderItemBinding binding;
    OrderDetailActivity activity;

    public OrderItemDialog(OrderDetailActivity activity){
        this.activity = activity;
        dialog=new Dialog(activity);
        generateTable();
    }

    public void show(){
        binding = DialogOrderItemBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int)(0.95*displayMetrics.widthPixels);
        int height = (int)(0.9*displayMetrics.heightPixels);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        binding.webview.loadDataWithBaseURL(null, page, "text/html", "UTF-8", null);
        binding.webview.getSettings().setBuiltInZoomControls(true);
        binding.webview.getSettings().setDisplayZoomControls(true);
        dialog.show();
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
        String header = "<tr><th>Item</th>" +
                "<th>Requested</th><th>Approved</th><th>Booked</th><th>UOM</th></tr>";

        StringBuilder bb = new StringBuilder();
        int sl = 1;
        for(OrderLineDto l:activity.orderDto.getOrderLines()){
            bb.append("<tr>");
            bb.append("<td>").append(sl).append(". ").append(l.getItemDescription())
                    .append(l.isLineCanceled()?"<span style='color:red'> &nbsp(Canceled)</span>":"").append("</td>");
            bb.append("<td>").append(l.getInitialQuantity()).append("</td>");
            bb.append("<td>").append(l.getQuantity()).append("</td>");
            bb.append("<td>").append(l.getBookedQuantity()).append("</td>");
            bb.append("<td>").append(l.getUom()).append("</td>");
            bb.append("</tr>");
            sl++;
        }
        String body = bb.toString();
        String top =
                "<label><b>Customer:</b>&nbsp"+activity.orderDto.getCustomerName()+"&nbsp"+activity.orderDto.getCustomerNumber()+"</label><br>"+
                "<label><b>Order Date:</b>&nbsp"+activity.orderDto.getCreationTime()+"</label><br>"+
                "<label><b>Book Date:</b>&nbsp"+activity.orderDto.getBookedDate()+"</label><br>"+
                "<label><b>Customer Order No:</b>&nbsp"+activity.orderDto.getOrderNumber()+"</label><br>"+
                "<label><b>System Order No:</b>&nbsp"+activity.orderDto.getOracleOrderNumber()+"</label><br><br>"+
                "<label><b>Order Summary</b></label><br>"+
                "<label><b>Gross Value:</b> "+activity.orderDto.getValue()+" Tk</label><br>"+
                "<label><b>Book Value:</b> "+activity.orderDto.getBookedValue()+" Tk</label><br>"+
                "<label>"+activity.generateSummaryString()+"</label><br><br>";

        page = top+style+"<table>"+header+body+"</table>";
    }

    public void createWebPrintJob() {
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        String jobName = activity.orderDto.getOrderNumber();
        PrintDocumentAdapter printAdapter = binding.webview.createPrintDocumentAdapter(jobName);
        printManager.print(jobName, printAdapter, null);
    }

}
