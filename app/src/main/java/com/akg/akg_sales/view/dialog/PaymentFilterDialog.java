package com.akg.akg_sales.view.dialog;

import android.app.Dialog;
import android.view.LayoutInflater;

import com.akg.akg_sales.databinding.DialogPaymentFilterBinding;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.payment.PaymentListActivity;
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
import java.util.Objects;

public class PaymentFilterDialog {
    DialogPaymentFilterBinding binding;
    PaymentListActivity activity;
    Dialog dialog;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat();
    HashMap<String,String> tempFilter = new HashMap<>();

    public PaymentFilterDialog(PaymentListActivity activity){
        this.activity=activity;
        dialog=new Dialog(activity);
        binding = DialogPaymentFilterBinding.inflate(LayoutInflater.from(activity));
        binding.setVm(this);
        dialog.setContentView(binding.getRoot());
        CommonUtil.setDialogWindowParams(this.activity,this.dialog);
        initFields();
    }

    private void initFields(){
        try {
            tempFilter.putAll(activity.filter);
            tempFilter.put("page","1");

            sdf.applyPattern("yyyy-MM-dd");
            Date sd = sdf.parse(Objects.requireNonNull(tempFilter.get("startDate")));
            Date ed = sdf.parse(Objects.requireNonNull(tempFilter.get("endDate")));
            sdf.applyPattern("dd MMMM yyyy");
            assert sd != null; assert ed != null;
            binding.dateFrom.setText(sdf.format(sd));
            binding.dateTo.setText(sdf.format(ed));
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
        cCal.add(Calendar.DATE,-30);
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
        activity.payments.clear();
        activity.filter.putAll(tempFilter);
        activity.fetchPaymentsFromServer();
    }
}
