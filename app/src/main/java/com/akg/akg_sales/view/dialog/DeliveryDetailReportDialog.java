package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;

import com.akg.akg_sales.databinding.DialogDeliveryDetailReportBinding;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedLineDto;
import com.akg.akg_sales.view.activity.delivery.DeliveryDetailActivity;

public class DeliveryDetailReportDialog {
    DialogDeliveryDetailReportBinding binding;
    public Dialog dialog;
    DeliveryDetailActivity activity;
    private String page;

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
        binding.webview.loadDataWithBaseURL(null, page, "text/html", "UTF-8", null);
        binding.webview.getSettings().setBuiltInZoomControls(true);
        binding.webview.getSettings().setDisplayZoomControls(true);
    }

    private void generateTable(){
        MoveOrderConfirmedHeaderDto mov = activity.moveConfirmedHeaderDto;
        String title = "<h3>Move Order: <span style='font-weight:normal'>"+mov.getMovOrderNo() +"</span></h3>" +
                "<h3>Loading Date: <span style='font-weight:normal'>"+mov.getMoveConfirmedDate() +"</span></h3>" +
                "<h3>Gate-Out Date: <span style='font-weight:normal'>"+" " +"</span></h3>" +
                "<h3>Vehicle No: <span style='font-weight:normal'>"+mov.getVehicleNo() +"</span></h3>" +
                "<h3>Driver: <span style='font-weight:normal'>"+mov.getDriverName()+" "+mov.getDriverMobile() +"</span></h3>";

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
            if(!(o+d).equals(doN)){    // Multiple Order can be in One DO
                bb.append("<td rowspan='"+activity.dataFrequency.get(o+d)+"' >");
                bb.append(d);doN=o+d;
                bb.append("</td>");
            }

            bb.append("<td>").append(l.getItemDescription()).append("</td>")
                    .append("<td>").append(l.getLineQuantity()).append(" ").append(l.getUomCode()).append("</td>")
                    .append("</tr>");
        }
        String body = bb.toString();
        page = style+title+"<table>"+header+body+"</table>";
    }

    public void createWebPrintJob() {
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        String jobName = activity.moveConfirmedHeaderDto.getMovOrderNo();
        PrintDocumentAdapter printAdapter = binding.webview.createPrintDocumentAdapter(jobName);
        printManager.print(jobName, printAdapter, null);
    }

//    PrintAttributes attributes = new PrintAttributes.Builder()
//            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
//            .setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600))
//            .setMinMargins(PrintAttributes.Margins.NO_MARGINS).build();
}
