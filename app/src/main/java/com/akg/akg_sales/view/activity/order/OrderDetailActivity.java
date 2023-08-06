package com.akg.akg_sales.view.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityOrderDetailBinding;
import com.akg.akg_sales.dto.StatusFlow;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderLineDto;
import com.akg.akg_sales.dto.order.OrderRequest;
import com.akg.akg_sales.dto.payment.PaymentDto;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.service.PaymentService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.delivery.DeliveryListActivity;
import com.akg.akg_sales.view.activity.payment.PaymentListActivity;
import com.akg.akg_sales.view.adapter.order.OrderLineAdapter;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.StatusFlowDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;

public class OrderDetailActivity extends AppCompatActivity {
    public boolean orderActionPermitted=false;
    public RecyclerView recyclerView;
    public ActivityOrderDetailBinding binding;
    public OrderDto orderDto;
    public PaymentDto paymentDto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                paymentDto = res.getData().get(0);
                binding.paymentValue.setText(String.valueOf(paymentDto.getPaymentAmount()));
                binding.paymentDate.setText(paymentDto.getPaymentDate());
            }
        });
    }

    public void loadOrderLines(){
        recyclerView = binding.orderLinesList;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        OrderLineAdapter adapter = new OrderLineAdapter(this,
                (ArrayList<OrderLineDto>) orderDto.getOrderLines());
        recyclerView.setAdapter(adapter);
    }

    public void onClickApprove(){
        new ConfirmationDialog(this,"Approve Order?",i->{
            OrderRequest body = new OrderRequest();
            body.setOrderId(orderDto.getId()).setCustomerId(orderDto.getCustomerId());
            for (OrderLineDto l : orderDto.getOrderLines()) {
                body.addLine(l.getItemId(),l.getQuantity().intValue());
            }
            body.setNote(addNote());
            OrderService.approveOrder(body,this,res->{
                CommonUtil.showToast(getApplicationContext(),"Order Approved",true);
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
            OrderService.cancelOrder(orderDto.getId().toString(),getApplicationContext(),
                    res->{
                        CommonUtil.showToast(getApplicationContext(),"Order Canceled",true);
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
        orderActionPermitted = Objects.equals(orderDto.getCurrentApproverUsername(),
                CommonUtil.loggedInUser.getUsername());
        if(orderActionPermitted) {
            binding.orderAction.setVisibility(View.VISIBLE);
            fetchLastPaymentFromServer();
        }
        else binding.orderAction.setVisibility(View.GONE);
    }

    private void loadStatusFlowDialog(){
        try {
            if(orderDto.getCurrentStatus().toUpperCase().contains("CANCELED")) return;
            ArrayList<StatusFlow> statusFlows = new ArrayList<>();
            statusFlows.add(new StatusFlow(1,false,"Awaiting Market Approval"));
            statusFlows.add(new StatusFlow(2,false,"Awaiting Management Approval"));
            statusFlows.add(new StatusFlow(3,false,"Order Confirmed"));
            for (StatusFlow s:statusFlows){
                s.setPassed(true);
                if(s.getStatus().equals(orderDto.getCurrentStatus())) break;
            }
            binding.statusLayout.setOnClickListener(v->
                    new StatusFlowDialog(statusFlows,OrderDetailActivity.this));
        }catch (Exception e){e.printStackTrace();}
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

    public void showDeliveryList(){
        try {
            if(orderDto.getOracleOrderNumber()==null || orderDto.getOracleOrderNumber().isEmpty() )
                return;
            Intent intent = new Intent(this, DeliveryListActivity.class);
            intent.putExtra("orderNumber",orderDto.getOracleOrderNumber());
            startActivity(intent);
        }catch (Exception e){e.printStackTrace();}
    }

}