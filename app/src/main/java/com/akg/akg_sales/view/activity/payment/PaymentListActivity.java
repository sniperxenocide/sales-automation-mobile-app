package com.akg.akg_sales.view.activity.payment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityPaymentListBinding;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.payment.PaymentDto;
import com.akg.akg_sales.service.PaymentService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        System.out.println("Inside onRestart***********************");
        initFilter();
        payments.clear();
        fetchPayments();
    }

    private void loadPage(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_payment_list);
        binding.setActivity(this);
        binding.executePendingBindings();
    }

    private void fetchPayments(){
        PaymentService.getPayments(this,filter,res->{

        });
    }

    private void initFilter(){
        filter.put("page","1");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        filter.put("endDate",s.format(calendar.getTime()));
        calendar.add(Calendar.DATE,-30);
        filter.put("startDate",s.format(calendar.getTime()));
        filter.put("statusId","%");
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
                            fetchPayments();
                        }catch (Exception ignored){}

                    }
                }
            }
        });
    }

}