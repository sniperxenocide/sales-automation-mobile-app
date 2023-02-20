package com.akg.akg_sales.viewmodel.notification;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akg.akg_sales.BR;
import com.akg.akg_sales.api.API;
import com.akg.akg_sales.api.NotificationApi;
import com.akg.akg_sales.dto.PageResponse;
import com.akg.akg_sales.dto.notification.DeliveryConfirmedHeaderDto;
import com.akg.akg_sales.dto.notification.OrderBookedHeaderDto;
import com.akg.akg_sales.dto.notification.PaymentDto;
import com.akg.akg_sales.util.CommonUtil;
import com.akg.akg_sales.view.activity.notification.NotificationActivity;
import com.akg.akg_sales.view.adapter.DeliveryConfirmedAdapter;
import com.akg.akg_sales.view.adapter.OrderBookedAdapter;
import com.akg.akg_sales.view.adapter.PaymentClearedAdapter;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationViewModel extends BaseObservable {
    public NotificationActivity activity;
    private final RecyclerView recyclerView;
    private PageResponse<OrderBookedHeaderDto> orderBookedPageResponse = null;
    private PageResponse<DeliveryConfirmedHeaderDto> deliveryConfirmedPageResponse = null;
    private PageResponse<PaymentDto> paymentClearedPageResponse = null;

    @Bindable
    public boolean[] selectedTab = {false,false,false};
    @Bindable
    public String tabTitle = "";

    public NotificationViewModel(NotificationActivity activity){
        this.activity = activity;
        recyclerView = initNotificationList();
        selectTabOrderBooked();
    }

    private RecyclerView initNotificationList(){
        RecyclerView recyclerView = activity.notificationBinding.notificationListview;
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(
                activity, layoutManager.getOrientation()));
        return recyclerView;
    }

    //    208753,203267
    public void selectTabOrderBooked(){
        updateUiOnTabChange(0,"Order Booked");
        if(this.orderBookedPageResponse==null){
            NotificationApi api = API.getClient().create(NotificationApi.class);
            Call<PageResponse<OrderBookedHeaderDto>> call
                    = api.getOrderBooked(CommonUtil.loggedInUser.getUsername());
            call.enqueue(new Callback<PageResponse<OrderBookedHeaderDto>>() {
                @Override
                public void onResponse(Call<PageResponse<OrderBookedHeaderDto>> call, Response<PageResponse<OrderBookedHeaderDto>> response) {
                    orderBookedPageResponse = response.body();
                    recyclerView.setAdapter(new OrderBookedAdapter(activity,
                            (ArrayList<OrderBookedHeaderDto>) orderBookedPageResponse.getData()));
                }
                @Override
                public void onFailure(Call<PageResponse<OrderBookedHeaderDto>> call, Throwable t) {
                    call.cancel();
                    CommonUtil.showToast(activity,t.getMessage(),false);
                }
            });
        }
        else {
            recyclerView.setAdapter(new OrderBookedAdapter(activity,
                    (ArrayList<OrderBookedHeaderDto>) orderBookedPageResponse.getData()));
        }
    }

    public void selectTabDeliveryConfirmed(){
        updateUiOnTabChange(1,"Delivery Confirmed");
        if(this.deliveryConfirmedPageResponse==null){
            NotificationApi api = API.getClient().create(NotificationApi.class);
            Call<PageResponse<DeliveryConfirmedHeaderDto>> call = api.getDeliveryConfirmed();
            call.enqueue(new Callback<PageResponse<DeliveryConfirmedHeaderDto>>() {
                @Override
                public void onResponse(Call<PageResponse<DeliveryConfirmedHeaderDto>> call, Response<PageResponse<DeliveryConfirmedHeaderDto>> response) {
                    deliveryConfirmedPageResponse = response.body();
                    recyclerView.setAdapter(new DeliveryConfirmedAdapter(activity,
                            (ArrayList<DeliveryConfirmedHeaderDto>) deliveryConfirmedPageResponse.getData()));
                }
                @Override
                public void onFailure(Call<PageResponse<DeliveryConfirmedHeaderDto>> call, Throwable t) {
                    call.cancel();
                    CommonUtil.showToast(activity,t.getMessage(),false);
                }
            });
        }
        else {
            recyclerView.setAdapter(new DeliveryConfirmedAdapter(activity,
                    (ArrayList<DeliveryConfirmedHeaderDto>) deliveryConfirmedPageResponse.getData()));
        }
    }

    public void selectTabPayment(){
        updateUiOnTabChange(2,"Payment");
        if(this.paymentClearedPageResponse==null){
            NotificationApi api = API.getClient().create(NotificationApi.class);
            Call<PageResponse<PaymentDto>> call = api.getAllPaymentCleared();
            call.enqueue(new Callback<PageResponse<PaymentDto>>() {
                @Override
                public void onResponse(Call<PageResponse<PaymentDto>> call, Response<PageResponse<PaymentDto>> response) {
                    paymentClearedPageResponse = response.body();
                    recyclerView.setAdapter(new PaymentClearedAdapter(activity,
                            (ArrayList<PaymentDto>) paymentClearedPageResponse.getData()));
                }

                @Override
                public void onFailure(Call<PageResponse<PaymentDto>> call, Throwable t) {
                    call.cancel();
                    CommonUtil.showToast(activity,t.getMessage(),false);
                }
            });
        }
        else {
            recyclerView.setAdapter(new PaymentClearedAdapter(activity,
                    (ArrayList<PaymentDto>) paymentClearedPageResponse.getData()));
        }
    }

    public void updateUiOnTabChange(int pos,String title){
        Arrays.fill(selectedTab, false);
        selectedTab[pos] = true;
        notifyPropertyChanged(BR.selectedTab);
        this.tabTitle = "("+title+")";
        notifyPropertyChanged(BR.tabTitle);
    }

}
