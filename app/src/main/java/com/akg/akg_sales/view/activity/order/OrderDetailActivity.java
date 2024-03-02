package com.akg.akg_sales.view.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.databinding.ActivityOrderDetailBinding;
import com.akg.akg_sales.dto.StatusFlow;
import com.akg.akg_sales.dto.delivery.DeliveryOrderDto;
import com.akg.akg_sales.dto.order.OrderApprovalDto;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderLineDto;
import com.akg.akg_sales.dto.order.OrderLineRequest;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.dto.payment.PaymentDto;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.service.PaymentService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.delivery.DeliveryListActivity;
import com.akg.akg_sales.view.activity.payment.PaymentListActivity;
import com.akg.akg_sales.view.adapter.order.OrderLineAdapter;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.DeliveryOrderDoLineDialog;
import com.akg.akg_sales.view.dialog.OrderItemDialog;
import com.akg.akg_sales.view.dialog.StatusFlowDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class OrderDetailActivity extends AppCompatActivity {
    public boolean canApproveOrder = false;
    public RecyclerView recyclerView;
    public ActivityOrderDetailBinding binding;
    public OrderDto orderDto;
    public PaymentDto paymentDto;
    public OrderItemDialog orderItemDialog;
    public DeliveryOrderDoLineDialog deliveryOrderDoLineDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFirebaseUserId();
        try {fetchOrderDetailFromServer(getIntent().getStringExtra("orderId"));
        }catch (Exception e){finish();}
    }

    private void loadPage(){
        if(orderDto==null) finish();
        binding = DataBindingUtil.setContentView(this,R.layout.activity_order_detail);
        binding.setActivity(this);
        binding.setVm(orderDto);
        binding.executePendingBindings();
        setOrderActionUi();
        loadOrderLines();
        loadStatusFlowDialog();
        loadDeliveryOrders();
        orderItemDialog = new OrderItemDialog(this);
        loadAttachment();
        setItemSummary();
    }

    private void fetchOrderDetailFromServer(String orderId){
        OrderService.fetchOrderDetailFromServer(orderId,this,res->{
            orderDto = res;
            loadPage();
        });
    }

    private void fetchLastPaymentFromServer(){
        HashMap<String,String> filter = new HashMap<>();
        filter.put("page","1");
        filter.put("customerNumber",orderDto.getCustomerNumber());
        PaymentService.getPayments(this,filter,res->{
            if(res!=null && res.getData().size()>0){
                for(PaymentDto p:res.getData()){
                    //getting the latest valid payment
                    if(!p.getCurrentStatusCode().equals("REVERSED")){
                        paymentDto = p;
                        break;
                    }
                }
                if(paymentDto==null) return;
                binding.paymentValue.setText(String.valueOf(paymentDto.getPaymentAmount()));
                binding.paymentDate.setText(paymentDto.getPaymentDate());
            }
        });
    }

    public void loadOrderLines(){
        binding.orderLinesLabel.setText("Order Lines ("+orderDto.getOrderLines().size()+")");
        recyclerView = binding.orderLinesList;
        recyclerView.setItemViewCacheSize(orderDto.getOrderLines().size());
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        OrderLineAdapter adapter = new OrderLineAdapter(this,
                (ArrayList<OrderLineDto>) orderDto.getOrderLines());
        recyclerView.setAdapter(adapter);
    }

    private void loadDeliveryOrders(){
        try {
            if(orderDto.getDeliveryOrders()==null || orderDto.getDeliveryOrders().isEmpty())
                return;
            StringBuilder sb = new StringBuilder();
            Set<String> doNumbers = new HashSet<>();
            for(DeliveryOrderDto d:orderDto.getDeliveryOrders())
                doNumbers.add(d.getDoNumber());
            for(String s:doNumbers) sb.append(s).append(" , ");
            binding.doNumberContainer.setVisibility(View.VISIBLE);
            binding.doNumber.setText(sb.substring(0,sb.length()-3));
            deliveryOrderDoLineDialog = new DeliveryOrderDoLineDialog(this);
        }catch (Exception e){e.printStackTrace();}
    }

    public void onClickApprove(){
        new ConfirmationDialog(this,"Order Approval",i->{
            OrderRequest body = new OrderRequest();
            body.setOrderId(orderDto.getId()).setCustomerId(orderDto.getCustomerId());
            for (OrderLineDto l : orderDto.getOrderLines()) {
                if(!l.isLineCanceled()) body.addLine(l.getItemId(),l.getQuantity());
            }
            body.setNote(addNote());
            OrderService.approveOrder(body,this,res->{
                CommonUtil.showToast(getApplicationContext(),"Order Approved",true);
                // Returning to Order List Page
                Intent intent = new Intent(this, PendingOrderActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        });
    }

    private String addNote(){
        try {
            JSONObject noteObject =null;
            try {noteObject = new JSONObject(orderDto.getNote());
            }catch (Exception e){}
            if(noteObject==null) noteObject = new JSONObject();
            String note = Objects.requireNonNull(binding.noteField.getText()).toString();
            if(note.length()>0) noteObject.put(orderDto.getCurrentApproverSalesDesk()+
                    " ("+orderDto.getCurrentApproverUsername()+")", note);
            return noteObject.toString();
        }catch (Exception e){e.printStackTrace();}
        return orderDto.getNote();
    }

    public void onClickCancel(){
        new ConfirmationDialog(this,"Cancel Order?",i->{
            OrderService.cancelOrder(orderDto.getId().toString(),OrderDetailActivity.this,
                    res->{
                        CommonUtil.showToast(OrderDetailActivity.this,"Order Canceled",true);
                        // Returning to Order List Page
                        Intent intent = new Intent(this, PendingOrderActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    });
        });
    }

    public void onClickViewPayments(){
        try {
            Intent payment = new Intent(this, PaymentListActivity.class);
            payment.putExtra("customerNumber",orderDto.getCustomerNumber());
            startActivity(payment);
        }catch (Exception e){e.printStackTrace();}
    }

    private void setOrderActionUi(){
        canApproveOrder = orderDto.getCurrentApproverUsername()
                .equals(CommonUtil.loggedInUser.getUsername());
        if(canApproveOrder) {
            binding.orderAction.setVisibility(View.VISIBLE);
            fetchLastPaymentFromServer();
        }
        else binding.orderAction.setVisibility(View.GONE);
    }

    private void loadStatusFlowDialog(){
        try {
            if(orderDto.getCurrentStatus().toUpperCase().contains("CANCELED")) return;
            ArrayList<StatusFlow> statusFlows = new ArrayList<>();
            boolean beforeCurrentStatus = true;
            for(OrderStatusDto sd:CommonUtil.statusList){
                if(sd.getSequence()!=null){
                    int passed = 1;
                    if(!beforeCurrentStatus) passed = -1;
                    if(sd.getStatus().equals(orderDto.getCurrentStatus())){
                        beforeCurrentStatus = false;
                        passed = 0;
                        if(CommonUtil.statusList.indexOf(sd)==CommonUtil.statusList.size()-1)
                            passed=1;
                    }
                    statusFlows.add(new StatusFlow(sd.getSequence(),passed,sd.getStatus()));
                }
            }

            binding.statusLayout.setOnClickListener(v->
                    new StatusFlowDialog(statusFlows,OrderDetailActivity.this));
        }catch (Exception e){e.printStackTrace();}
    }

    public Spanned getApprovalHistory(){
        StringBuilder htmlBuilder = new StringBuilder();
        try {
            for(OrderApprovalDto a:orderDto.getApprovals()){
                htmlBuilder.append("<b>").append(a.getSequenceNo()).append(". ")
                        .append(a.getSalesDeskName())
                        .append(" (").append(a.getApproverUsername())
                        .append(")</b><br>").append("Approval Time: ")
                        .append(a.getApprovalTime());
                if(orderDto.getApprovals().indexOf(a) < orderDto.getApprovals().size()-1)
                    htmlBuilder.append("<br><br>");
            }
        }catch (Exception e){e.printStackTrace();}
        return Html.fromHtml(htmlBuilder.toString());
    }

    public Spanned getNotes(){
        StringBuilder htmlBuilder = new StringBuilder();
        try {
            JSONObject note = new JSONObject(orderDto.getNote());
            for (Iterator<String> it = note.keys(); it.hasNext(); ) {
                String k = it.next();
                htmlBuilder.append("<b>").append(k).append(" : </b><br>")
                        .append(note.getString(k));
                if(it.hasNext()) htmlBuilder.append("<br><br>");
            }
        }catch (Exception e){e.printStackTrace();}
        return Html.fromHtml(htmlBuilder.toString());
    }

    public void setItemSummary(){
        try {
            Hashtable<String,Double> initialItemCntMap = new Hashtable<>();
            Hashtable<String,Double> approvedItemCntMap = new Hashtable<>();
            Hashtable<String,Double> bookedItemCntMap = new Hashtable<>();

            for(OrderLineDto l:orderDto.getOrderLines()){
                Double initialQty = initialItemCntMap.get(l.getUom());
                Double approvedQty = approvedItemCntMap.get(l.getUom());
                Double bookedQty = bookedItemCntMap.get(l.getUom());
                if(initialQty==null) initialQty = 0.0;
                if(approvedQty==null) approvedQty = 0.0;
                if(bookedQty==null) bookedQty = 0.0;
                initialItemCntMap.put(l.getUom(),initialQty+l.getInitialQuantity());
                approvedItemCntMap.put(l.getUom(),approvedQty+l.getQuantity());
                bookedItemCntMap.put(l.getUom(),
                        bookedQty+(l.getBookedQuantityDbl()==null?0.0:l.getBookedQuantityDbl()));
            }
            StringBuilder sb = new StringBuilder();
            sb.append("<b>Requested: </b>");
            for(String k:initialItemCntMap.keySet())
                sb.append(initialItemCntMap.get(k)).append(" ").append(k).append(", ");
            sb.replace(sb.length() - 2, sb.length(), "");
            sb.append("<br>");

            sb.append("<b>Approved: </b>");
            for(String k:approvedItemCntMap.keySet())
                sb.append(approvedItemCntMap.get(k)).append(" ").append(k).append(", ");
            sb.replace(sb.length() - 2, sb.length(), "");
            sb.append("<br>");

            sb.append("<b>Booked: </b>");
            for(String k:bookedItemCntMap.keySet())
                sb.append(bookedItemCntMap.get(k)).append(" ").append(k).append(", ");
            sb.replace(sb.length() - 2, sb.length(), "");

            binding.itemSummary.setText(Html.fromHtml(sb.toString()));
        }catch (Exception ignored){}
    }

    public void showDeliveryList(){
        try {
            if(orderDto.getOracleOrderNumber()==null || orderDto.getOracleOrderNumber().isEmpty() )
                return;
            Intent intent = new Intent(this, DeliveryListActivity.class);
            intent.putExtra("orderNumber",orderDto.getOracleOrderNumber());
            startActivity(intent);
        }catch (Exception e){e.printStackTrace();}
    }

    private void loadAttachment(){
        String url = API.baseUrl + "/order-service/api/order/attachment?id="+orderDto.getId();
        ImageView attachmentView = binding.attachment;
        GlideUrl glideUrl = new GlideUrl(url,
                new LazyHeaders.Builder()
                        .addHeader("Authorization","Bearer "+CommonUtil.loggedInUser.getToken())
                        .build());

        Glide.with(this)
                .load(glideUrl)
                .into(attachmentView);

    }

}