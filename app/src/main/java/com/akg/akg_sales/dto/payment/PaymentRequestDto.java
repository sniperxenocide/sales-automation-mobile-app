package com.akg.akg_sales.dto.payment;

import android.content.Context;

import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.viewmodel.PaymentViewModel;

import java.io.File;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor @AllArgsConstructor
public class PaymentRequestDto {
    private Long paymentAccountId;
    private Double paymentAmount;
    private Long paymentTypeId;
    private String paymentDate;
    private String receiptNumber;

    private Long customerBankId;
    private String customerBankBranch;
    private String customerAccountNumber;
    private String comment;

    private File attachment;

    public PaymentRequestDto(PaymentViewModel vm) throws Exception{
        this.paymentAccountId = Long.parseLong(Objects.requireNonNull(vm.getPaymentAccountId().get()));
        this.paymentAmount = Double.parseDouble(Objects.requireNonNull(vm.getPaymentAmount().get()));
        this.paymentTypeId = Long.parseLong(Objects.requireNonNull(vm.getPaymentTypeId().get()));
        this.paymentDate = vm.getPaymentDate().get();
        this.receiptNumber = Objects.requireNonNull(vm.getReceiptNumber().get()).trim();
        if(this.receiptNumber.length()<=0) throw new Exception();

        try {
            this.customerBankId = Long.parseLong(Objects.requireNonNull(vm.getCustomerBankId().get()));
        }catch (Exception e){
            System.out.println("Customer Bank not Selected");
        }
        this.customerBankBranch = vm.getCustomerBankBranch().get();
        this.customerAccountNumber = vm.getCustomerAccountNumber().get();
        this.comment = vm.getComment().get();

        this.attachment = new File("img.txt");
        System.out.println(this);
    }

}
