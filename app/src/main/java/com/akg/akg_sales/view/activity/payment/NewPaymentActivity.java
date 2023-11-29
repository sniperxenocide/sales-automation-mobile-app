package com.akg.akg_sales.view.activity.payment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.akg.akg_sales.R;
import com.akg.akg_sales.databinding.ActivityNewPaymentBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.payment.BankDto;
import com.akg.akg_sales.dto.payment.PaymentAccountDto;
import com.akg.akg_sales.dto.payment.PaymentMasterDto;
import com.akg.akg_sales.dto.payment.PaymentRequestDto;
import com.akg.akg_sales.dto.payment.PaymentTypeDto;
import com.akg.akg_sales.service.PaymentService;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;
import com.akg.akg_sales.view.dialog.ConfirmationDialog;
import com.akg.akg_sales.view.dialog.SearchableTextListDialog;
import com.akg.akg_sales.viewmodel.PaymentViewModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NewPaymentActivity extends AppCompatActivity {
    ActivityNewPaymentBinding binding;
    public PaymentMasterDto masterDto;
    public PaymentAccountDto accountDto;
    private List<CustomerDto> customerList = CommonUtil.customers;
    private CustomerDto selectedCustomer;
    public PaymentViewModel paymentViewModel = new PaymentViewModel();

    String[] customers;
    Long[] customerIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPage();
    }

    private void initPage(){
        accountDto = new PaymentAccountDto();
        loadUI();
        loadCustomerList();
        loadPaymentAccounts();
    }

    private void loadUI(){
        binding = DataBindingUtil.setContentView(this,R.layout.activity_new_payment);
        binding.setActivity(this);
        binding.setVm(paymentViewModel);
        binding.executePendingBindings();
    }

    public void loadCustomerList(){
        AutoCompleteTextView tView=binding.customerList;
        customers = new String[customerList.size()];
        customerIds = new Long[customerList.size()];
        for(int a=0;a<customerList.size();a++){
            customers[a]=customerList.get(a).getCustomerName()+
                    " ("+customerList.get(a).getOracleCustomerCode()+")";
            customerIds[a]=customerList.get(a).getId();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> onClickCustomer(tView,i));
        onClickCustomer(tView,0);
    }

    private void onClickCustomer(AutoCompleteTextView tView,int idx){
        selectedCustomer = customerList.get(idx);
        tView.setText(customers[idx],false);
    }

    private void loadPaymentAccounts(){
        try {
            selectedCustomer = CommonUtil.customers.get(0);
            PaymentService.getPaymentMaster(this,selectedCustomer.getOperatingUnitId(),
                    master-> masterDto = master);
        }catch (Exception e){e.printStackTrace();}
    }

    public void showAccountSelectionDialog(){
        ArrayList<String> accounts = new ArrayList<>();
        for(PaymentAccountDto a:masterDto.getPaymentAccounts()){
            accounts.add(a.getBankName()+" "+a.getBankAccountNumber());
        }
        new SearchableTextListDialog(this,accounts, i->{
            this.accountDto = masterDto.getPaymentAccounts().get(i);
            binding.accInfoContainer.setVisibility(View.VISIBLE);
            binding.accInfo.setText(Html.fromHtml(
                    "<b>Bank: </b>"+accountDto.getBankName()+"<br>"
                            +"<b>Branch: </b>"+accountDto.getBankBranchName()+"<br>"
                            +"<b>AC: </b>"+accountDto.getBankAccountNumber()+"<br>"
                            +"<b>Name: </b>"+accountDto.getAccountHolderName()
                    ));
            paymentViewModel.getPaymentAccountId().set(accountDto.getId().toString());
        });
    }

    public void showCustomerBankDialog(){
        ArrayList<String> banks = new ArrayList<>();
        for (BankDto b:masterDto.getBanks())
            banks.add(b.getBankName()+" "+b.getShortBankName());
        new SearchableTextListDialog(this,banks,idx->{
            binding.custBankName.setText(masterDto.getBanks().get(idx).getBankName());
            paymentViewModel.getCustomerBankId().set(masterDto.getBanks().get(idx).getId().toString());
        });
    }

    public void showPaymentMethodsDialog(){
        ArrayList<String> methods = new ArrayList<>();
        for (PaymentTypeDto t:masterDto.getPaymentTypes())
            methods.add(t.getDescription());
        new SearchableTextListDialog(this,methods,idx->{
            binding.paymentMethod.setText(masterDto.getPaymentTypes().get(idx).getDescription());
            paymentViewModel.getPaymentTypeId().set(masterDto.getPaymentTypes().get(idx).getId().toString());
        });
    }

    public void showPaymentDatePicker(){
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Payment Date").build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat();
            calendar.setTimeInMillis(selection);
            sdf.applyPattern("yyyy-MM-dd");
            paymentViewModel.getPaymentDate().set(sdf.format(calendar.getTime()));
            sdf.applyPattern("dd MMMM yyyy");
            binding.paymentDate.setText(sdf.format(calendar.getTime()));
        });
        datePicker.show(getSupportFragmentManager(),"DATE_PICKER");
    }

    public void onClickAttachment(){
        ImagePicker.with(this).crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(500)			//Final image size will be less than 500 KB(Optional)
                .maxResultSize(800, 600)	//Final image resolution will be less than 800 x 600(Optional)
                .start();
    }

    public void onClickSubmit(){
        new ConfirmationDialog(this,"Payment Confirmation.",i->{
            try {
                PaymentRequestDto paymentRequestDto
                        = new PaymentRequestDto(paymentViewModel,selectedCustomer.getId());
                PaymentService.createPayment(this,paymentRequestDto, res-> {
                    Intent intent = new Intent(this, PaymentListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                });
            }catch (Exception e){
                e.printStackTrace();
                CommonUtil.showToast(this,"Please Fill up Mandatory Fields ",false);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                Uri uri = data.getData();
                System.out.println(uri.getPath());
                paymentViewModel.getAttachment().set(uri.getPath());
                String[] paths = uri.getPath().split("/");
                binding.attachment.setText(paths[paths.length-1]);
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }


}