package com.akg.akg_sales.view.activity.delivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityDeliveryListBinding;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.delivery.MoveOrderConfirmedHeaderDto;
import com.akg.akg_sales.service.DeliveryService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.delivery.DeliveryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class DeliveryListActivity extends AppCompatActivity {
    ActivityDeliveryListBinding binding;
    public HashMap<String,String> filter = new HashMap<>();
    PageResponse<MoveOrderConfirmedHeaderDto> pageResponse;
    public ArrayList<MoveOrderConfirmedHeaderDto> deliveries = new ArrayList<>();
    RecyclerView recyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
        initFilter();
        fetchDeliveryData();
    }

    private void loadPage(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_delivery_list);
        binding.setActivity(this);
        binding.executePendingBindings();
        initRecycleView();
    }

    private void initFilter(){
        filter.put("page","1");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        filter.put("endDate",s.format(calendar.getTime()));
        calendar.add(Calendar.DATE,-30);
        filter.put("startDate",s.format(calendar.getTime()));
        filter.put("customerNumbers","196034,189912");
    }

    private void initRecycleView(){
        recyclerView = binding.deliveryListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        handleLoadMoreData();
    }

    private void loadDeliveryInRecycleView(){
        deliveries.addAll(pageResponse.getData());
        if(deliveries.isEmpty()) CommonUtil.showToast(this,"No Delivery Available",false);
        DeliveryAdapter adapter = new DeliveryAdapter(this,deliveries);
        recyclerView.setAdapter(adapter);
        if(deliveries.size()>pageResponse.getPageSize()){
            recyclerView.scrollToPosition(deliveries.size()-pageResponse.getData().size());
        }
    }


    private void fetchDeliveryData(){
        DeliveryService.fetchDeliveryListFromServer(this,filter,res->{
            pageResponse = res;
            loadDeliveryInRecycleView();
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
                            fetchDeliveryData();
                        }catch (Exception ignored){}

                    }
                }
            }
        });
    }
}