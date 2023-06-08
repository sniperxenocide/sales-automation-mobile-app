package com.akg.akg_sales.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PaymentViewModel extends BaseObservable {
    private ObservableField<String> paymentAccountId = new ObservableField<>();
    private ObservableField<String> paymentAmount = new ObservableField<>();
    private ObservableField<String> paymentTypeId = new ObservableField<>();
    private ObservableField<String> paymentDate = new ObservableField<>();
    private ObservableField<String> receiptNumber = new ObservableField<>();

    private ObservableField<String> customerBankId = new ObservableField<>();
    private ObservableField<String> customerBankBranch = new ObservableField<>();
    private ObservableField<String> customerAccountNumber = new ObservableField<>();
    private ObservableField<String> comment = new ObservableField<>();
}
