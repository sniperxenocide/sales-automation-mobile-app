package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.OrderApi;
import com.akg.akg_sales.databinding.DialogOrderFilterBinding;
import com.akg.akg_sales.dto.CustomerDto;
import com.akg.akg_sales.dto.order.OrderStatusDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.order.PendingOrderActivity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFilterDialog {
    DialogOrderFilterBinding binding;
    PendingOrderActivity activity;
    Dialog dialog;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat();
    HashMap<String,String> tempFilter = new HashMap<>();

    public OrderFilterDialog(PendingOrderActivity activity){
        this.activity=activity;
        dialog=new Dialog(activity);
        binding = DialogOrderFilterBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(this.activity,this.dialog);
        initFields();
    }

    private void updateStatusUI(){
        ArrayList<OrderStatusDto> statusList = CommonUtil.statusList;
        if(statusList==null || statusList.isEmpty()) return;
        int arrLen = statusList.size()+1;
        AutoCompleteTextView tView=binding.statusDropdown;
        String[] statusIds = new String[arrLen];
        String[] status = new String[arrLen];
        statusIds[0]="%";status[0]="All";
        for (int i=0;i< statusList.size();i++) {
            statusIds[i+1]=statusList.get(i).getId().toString();
            status[i+1]=statusList.get(i).getStatus();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, status);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            tempFilter.put("statusId",statusIds[i]) ;
        });
        for(int i=0;i<arrLen;i++){
            if(Objects.equals(statusIds[i], tempFilter.get("statusId")))
                tView.setText(status[i],false);
        }
    }

    private void setSortDropdownUI(){
        AutoCompleteTextView tView = binding.sortDirDropdown;
        String[] sortCode = {"desc","asc"};
        String[] sortText = {"Newest Orders First","Oldest Orders First"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, sortText);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            tempFilter.put("sortDir",sortCode[i]) ;
            tView.setText(sortText[i],false);
        });
        for(int i=0;i< sortCode.length;i++){
            if(Objects.equals(sortCode[i], tempFilter.get("sortDir")))
                tView.setText(sortText[i],false);
        }
    }

    private void setCustomerUI(){
        List<CustomerDto> customerList = CommonUtil.customers;
        if(customerList==null || customerList.isEmpty()) {
            binding.customerNumber.setText(tempFilter.get("customerNumber"));
            binding.customerDropdownContainer.setVisibility(View.GONE);
            binding.customerNumberContainer.setVisibility(View.VISIBLE);
            return;
        }

        binding.customerDropdownContainer.setVisibility(View.VISIBLE);
        binding.customerNumberContainer.setVisibility(View.GONE);
        int arrLen = customerList.size()+1;
        AutoCompleteTextView tView=binding.customerDropdown;
        String[] customerNumbers = new String[arrLen];
        String[] customerNames = new String[arrLen];
        customerNumbers[0]="%";customerNames[0]="All";
        for (int i=0;i< customerList.size();i++) {
            CustomerDto c = customerList.get(i);
            customerNumbers[i+1]=c.getOracleCustomerCode();
            customerNames[i+1]=c.getCustomerName()+" "+c.getOracleCustomerCode();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, customerNames);
        tView.setAdapter(adapter);
        tView.setOnItemClickListener((adapterView, view, i, l) -> {
            tempFilter.put("customerNumber",customerNumbers[i]) ;
        });
        for(int i=0;i<arrLen;i++){
            if(Objects.equals(customerNumbers[i], tempFilter.get("customerNumber")))
                tView.setText(customerNames[i],false);
        }
    }

    private void setApprovalToggleBtn(){
        try {
            if(activity.userCategory.equals("Customer"))
                binding.approvalSwitchContainer.setVisibility(View.GONE);
            else {
                binding.approvalSwitch.setChecked(Boolean.parseBoolean(tempFilter.get("awaitingMyApprovalOnly")));
                binding.approvalSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) tempFilter.put("awaitingMyApprovalOnly","true");
                    else tempFilter.put("awaitingMyApprovalOnly","false");
                    handleAdditionalFilter();
                });
            }
        }catch (Exception ignored){}
    }

    private void handleAdditionalFilter(){
        if(tempFilter.get("awaitingMyApprovalOnly").equals("true")){
            binding.additionalFilters.setVisibility(View.GONE);
            binding.approvalSwitchMsg.setVisibility(View.VISIBLE);

            binding.orderNumber.setText("");

            tempFilter.put("statusId","%");
            binding.statusDropdown.setText("All",false);

            if(CommonUtil.customers==null || CommonUtil.customers.isEmpty()) {
                tempFilter.put("customerNumber","");
                binding.customerNumber.setText("");
            }
            else {
                tempFilter.put("customerNumber","%");
                binding.customerDropdown.setText("All",false);
            }
        }
        else {
            binding.additionalFilters.setVisibility(View.VISIBLE);
            binding.approvalSwitchMsg.setVisibility(View.GONE);
        }

    }

    private void initFields(){
        try {
            tempFilter.putAll(activity.filter);
            tempFilter.put("page","1");

            setSortDropdownUI();
            setApprovalToggleBtn();

            sdf.applyPattern("yyyy-MM-dd");
            Date sd = sdf.parse(Objects.requireNonNull(tempFilter.get("startDate")));
            Date ed = sdf.parse(Objects.requireNonNull(tempFilter.get("endDate")));
            sdf.applyPattern("dd MMMM yyyy");
            assert sd != null; assert ed != null;
            binding.dateFrom.setText(sdf.format(sd));
            binding.dateTo.setText(sdf.format(ed));

            setCustomerUI();
            binding.orderNumber.setText(tempFilter.get("orderNumber"));
            updateStatusUI();

            handleAdditionalFilter();

        }catch (Exception e){e.printStackTrace();}

    }

    public void onClickDateFrom(){
        showDatePicker("Date From","startDate", binding.dateFrom);
    }

    public void onClickDateTo(){
        showDatePicker("Date To","endDate", binding.dateTo);
    }


    private void showDatePicker(String title, String filterKey, TextInputEditText textView){
        Calendar cCal = Calendar.getInstance();
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        CalendarConstraints.DateValidator dateValidatorMax = DateValidatorPointBackward.before(cCal.getTimeInMillis());
        cCal.add(Calendar.DATE,-90);
        CalendarConstraints.DateValidator dateValidatorMin = DateValidatorPointForward.from(cCal.getTimeInMillis());
        ArrayList<CalendarConstraints.DateValidator> listValidators = new ArrayList<>();
        listValidators.add(dateValidatorMin);
        listValidators.add(dateValidatorMax);
        CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);
        constraintsBuilderRange.setValidator(validators);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilderRange.build())
                .setTitleText(title).build();
        datePicker.addOnPositiveButtonClickListener(selection -> {
            calendar.setTimeInMillis(selection);
            sdf.applyPattern("yyyy-MM-dd");
            tempFilter.put(filterKey,sdf.format(calendar.getTime()));
            sdf.applyPattern("dd MMMM yyyy");
            textView.setText(sdf.format(calendar.getTime()));
        });
        datePicker.show(activity.getSupportFragmentManager(),"DATE_PICKER");
    }

    public void onClickApplyFilter(){
        dialog.dismiss();
        activity.orders.clear();
        activity.filter.putAll(tempFilter);

        if(binding.orderNumber.getText()!=null && binding.orderNumber.getText().length()>0)
            activity.filter.put("orderNumber",binding.orderNumber.getText().toString());
        else activity.filter.remove("orderNumber");

        if(CommonUtil.customers==null || CommonUtil.customers.isEmpty()) {
            if(binding.customerNumber.getText()!=null && binding.customerNumber.getText().length()>0)
                activity.filter.put("customerNumber",binding.customerNumber.getText().toString());
            else activity.filter.remove("customerNumber");
        }

        activity.fetchOrderFromServer();
    }


}
