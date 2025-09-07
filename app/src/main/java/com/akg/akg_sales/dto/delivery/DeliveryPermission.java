package com.akg.akg_sales.dto.delivery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DeliveryPermission {
    private Boolean canViewDeliveryReceiving = false;
    private Boolean canSubmitDeliveryReceiving = false;
}
