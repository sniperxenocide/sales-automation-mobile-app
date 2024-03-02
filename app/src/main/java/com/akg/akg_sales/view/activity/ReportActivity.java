package com.akg.akg_sales.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityReportBinding;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.order.OrderDto;
import com.akg.akg_sales.dto.report.ReportDto;
import com.akg.akg_sales.service.ReportService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.ReportListAdapter;
import com.akg.akg_sales.view.adapter.order.PendingOrderAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReportActivity extends AppCompatActivity {
    ActivityReportBinding binding;
    PageResponse<ReportDto> pageResponse;
    public ArrayList<ReportDto> reports = new ArrayList<>();
    RecyclerView recyclerView ;
    public HashMap<String,String> filter = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonUtil.setFirebaseUserId();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        binding.setActivity(this);
        binding.executePendingBindings();
        initRecycleView();
        initFilter();
        fetchReportsFromServer();
    }

    private void initRecycleView(){
        recyclerView = binding.reportListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        handleLoadMoreData();
    }

    private void loadReportsInRecycleView(){
        reports.addAll(pageResponse.getData());
        binding.orderFetchCount.setText("("+pageResponse.getRecordCount()+")");
        if(reports.isEmpty()) CommonUtil.showToast(this,"No Reports Available",false);
        ReportListAdapter adapter = new ReportListAdapter(this,reports);
        recyclerView.setAdapter(adapter);
        if(reports.size()>pageResponse.getPageSize()){
            recyclerView.scrollToPosition(reports.size()-pageResponse.getData().size());
        }
    }

    private void fetchReportsFromServer(){
        try {
            Map<String,String> filter = new HashMap<>();
            //filter.put("operatingUnitId",CommonUtil.customers.get(0).getOperatingUnitId());
            ReportService.fetchReportList(this,filter,r->{
                this.pageResponse = r;
                loadReportsInRecycleView();
            });
        }catch (Exception e){
            e.printStackTrace();
            CommonUtil.showToast(this,e.getMessage(),false);
        }
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
                            fetchReportsFromServer();
                        }catch (Exception ignored){}

                    }
                }
            }
        });
    }

    public void onClickFilter(){

    }

    private void initFilter() {
        filter.put("page", "1");
    }
}