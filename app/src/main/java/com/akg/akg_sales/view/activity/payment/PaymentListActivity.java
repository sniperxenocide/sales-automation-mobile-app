package com.akg.akg_sales.view.activity.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityPaymentListBinding;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.payment.PaymentDto;
import com.akg.akg_sales.service.PaymentService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.adapter.payment.PaymentListAdapter;
import com.akg.akg_sales.view.dialog.PaymentFilterDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class PaymentListActivity extends AppCompatActivity {
    ActivityPaymentListBinding binding;
    public HashMap<String,String> filter = new HashMap<>();
    PageResponse<PaymentDto> pageResponse;
    public ArrayList<PaymentDto> payments = new ArrayList<>();
    RecyclerView recyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPage();
        initFilter();
        fetchPaymentsFromServer();
    }

    private void loadPage(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_payment_list);
        binding.setActivity(this);
        binding.executePendingBindings();
        initRecycleView();
        newPaymentControl();
    }

    private void newPaymentControl(){
        PaymentService.fetchPaymentPermission(permission->{
            if(permission.getCanCreatePayment()) binding.newPaymentBtn.setVisibility(View.VISIBLE);
        });
    }

    private void initRecycleView(){
        recyclerView = binding.paymentListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new PaymentListAdapter(this,new ArrayList<>()));
        handleLoadMoreData();
    }

    private void loadPaymentsInRecycleView(){
        if(pageResponse!=null) payments.addAll(pageResponse.getData());
        if(payments.isEmpty()) CommonUtil.showToast(this,"No Payments Available",false);
        binding.paymentCountLabel.setText("("+pageResponse.getRecordCount()+")");
        PaymentListAdapter adapter = new PaymentListAdapter(this,payments);
        recyclerView.setAdapter(adapter);
        if(payments.size()>pageResponse.getPageSize()){
            recyclerView.scrollToPosition(payments.size()-pageResponse.getData().size());
        }
    }

    public void fetchPaymentsFromServer(){
        PaymentService.getPayments(this,filter,res->{
            pageResponse = res;
            loadPaymentsInRecycleView();
        });
    }

    private void initFilter(){
        filter.put("page","1");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        filter.put("endDate",s.format(calendar.getTime()));
        calendar.add(Calendar.DATE,-30);
        filter.put("startDate",s.format(calendar.getTime()));

        try {  // Checking for CustomerNumber passed from parent activity
            filter.put("customerNumber",getIntent().getExtras().getString("customerNumber"));
        }catch (Exception ignored){System.out.println("Customer Number not present");}
        System.out.println("Filters "+filter.toString());
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
                            fetchPaymentsFromServer();
                        }catch (Exception ignored){}

                    }
                }
            }
        });
    }

    public void onClickNewPayment(){
        startActivity(new Intent(this, NewPaymentActivity.class));
    }

    public void onClickFilter(){
        new PaymentFilterDialog(this);
    }

}