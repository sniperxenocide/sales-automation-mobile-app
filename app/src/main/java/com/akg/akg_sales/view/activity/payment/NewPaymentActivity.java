package com.akg.akg_sales.view.activity.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityNewPaymentBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.payment.PaymentAccountDto;
import com.akg.akg_sales.service.PaymentService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.dialog.PaymentAccountDialog;

import java.util.ArrayList;
import java.util.List;

public class NewPaymentActivity extends AppCompatActivity {
    ActivityNewPaymentBinding binding;
    public PaymentAccountDto accountDto;
    private CustomerDto selectedCustomer;
    private List<PaymentAccountDto> paymentAccounts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPage();
    }

    private void initPage(){
        accountDto = new PaymentAccountDto();
        loadUI();
        setCustomer();
        loadPaymentAccounts();
    }

    private void loadUI(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_payment);
        binding.setActivity(this);
        binding.executePendingBindings();
    }

    private void setCustomer(){
        if(CommonUtil.customers.size()>0){
            AutoCompleteTextView tView=binding.customerList;
            String[] customers = new String[CommonUtil.customers.size()];
            for (int i=0;i< CommonUtil.customers.size();i++)
                customers[i]=CommonUtil.customers.get(i).getCustomerName();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);
            tView.setAdapter(adapter);
            tView.setOnItemClickListener((adapterView, view, i, l) -> onCustomerSelect(tView,i));
            onCustomerSelect(tView,0);
        }
    }

    private void onCustomerSelect(AutoCompleteTextView tView,int idx){
        selectedCustomer = CommonUtil.customers.get(idx);
        tView.setText(this.selectedCustomer.getCustomerName(),false);
    }

    private void loadPaymentAccounts(){
        try {
            PaymentService.getPaymentAccounts(this,selectedCustomer.getOperatingUnitId(),
                    list-> paymentAccounts = list);
        }catch (Exception e){e.printStackTrace();}
    }

    public void showAccountSelectionDialog(){
        ArrayList<String> accounts = new ArrayList<>();
        for(PaymentAccountDto a:paymentAccounts){
            accounts.add(a.getBankName()+" "+a.getBankAccountNumber());
        }
        new PaymentAccountDialog(this,accounts,i->{
            this.accountDto = paymentAccounts.get(i);
            binding.setActivity(this);
        });
    }
}