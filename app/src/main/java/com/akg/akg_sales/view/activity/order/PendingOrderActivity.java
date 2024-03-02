package com.akg.akg_sales.view.activity.order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.databinding.ActivityPendingOrderBinding;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.order.OrderPermission;
import com.akg.akg_sales.service.OrderService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.order.PendingOrderAdapter;
import com.akg.akg_sales.view.dialog.OrderFilterDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingOrderActivity extends AppCompatActivity {
    private ActivityPendingOrderBinding binding;
    public HashMap<String,String> filter = new HashMap<>();
    PageResponse<OrderDto> pageResponse;
    public ArrayList<OrderDto> orders = new ArrayList<>();
    RecyclerView recyclerView ;
    public String userCategory = CommonUtil.loggedInUser.getCategory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFirebaseUserId();
        loadPage();
        initFilter();
        fetchOrderFromServer();
    }

    private void loadPage(){
        binding= DataBindingUtil.setContentView(this,R.layout.activity_pending_order);
        binding.setActivity(this);
        initRecycleView();
        createOrderControl();
    }

    private void createOrderControl(){
        OrderService.fetchOrderPermission(p->{
            CommonUtil.orderPermission = p;
            if(p.getCanCreateOrder()) binding.newOrderBtn.setVisibility(View.VISIBLE);
        });
    }

    private void initRecycleView(){
        recyclerView = binding.pendingOrderListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        handleLoadMoreData();
    }

    private void loadOrdersInRecycleView(){
        orders.addAll(pageResponse.getData());
        binding.orderFetchCount.setText("("+pageResponse.getRecordCount()+")");
        if(orders.isEmpty()) CommonUtil.showToast(this,"No Orders Available",false);
        PendingOrderAdapter adapter = new PendingOrderAdapter(this,orders);
        recyclerView.setAdapter(adapter);
        if(orders.size()>pageResponse.getPageSize()){
            recyclerView.scrollToPosition(orders.size()-pageResponse.getData().size());
        }
    }

    private void initFilter(){
        filter.put("page","1");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        filter.put("endDate",s.format(calendar.getTime()));
        calendar.add(Calendar.DATE,-30);
        filter.put("startDate",s.format(calendar.getTime()));
        filter.put("statusId","%");
        filter.put("sortDir","desc");
        filter.put("customerNumber","%");

        if(this.userCategory.equals("Customer"))
            filter.put("awaitingMyApprovalOnly","false");
        else filter.put("awaitingMyApprovalOnly","true");

    }

    public void fetchOrderFromServer(){
        ProgressDialog progressDialog=CommonUtil.showProgressDialog(this);
        API.getClient().create(OrderApi.class).getAllOrders(filter)
                .enqueue(new Callback<PageResponse<OrderDto>>() {
                @Override
                public void onResponse(Call<PageResponse<OrderDto>> call, Response<PageResponse<OrderDto>> response) {
                    progressDialog.dismiss();
                    try {
                        if(response.code()==200){
                            pageResponse = response.body();
                            loadOrdersInRecycleView();
                            CommonUtil.showToast(getApplicationContext(),"Data Loaded",true);
                        }else throw new Exception(response.code()+"."+response.message());
                    }catch (Exception e){
                        CommonUtil.showToast(getApplicationContext(),e.getMessage(),false);
                    }
                }
                @Override
                public void onFailure(Call<PageResponse<OrderDto>> call, Throwable t) {
                    progressDialog.dismiss();
                    call.cancel();
                }
            });
    }

    private void handleLoadMoreData(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
                assert layoutManager != null;
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstCompletelyVisibleItemPosition();

                if(pastVisibleItems+visibleItemCount > totalItemCount){
                    // End of the list is here.
                    System.out.println("List End");
                    if(pageResponse.getCurrentPage()<pageResponse.getTotalPages()){
                        try {
                            int nextPage = Integer.parseInt(Objects.requireNonNull(filter.get("page")))+1;
                            filter.put("page",Integer.toString(nextPage));
                            fetchOrderFromServer();
                        }catch (Exception ignored){}

                    }
                }
            }
        });
    }

    public void onClickNewOrder(){
        startActivity(new Intent(this, OrderActivity.class));
    }

    public void onClickFilter(){
        new OrderFilterDialog(this);
    }

}