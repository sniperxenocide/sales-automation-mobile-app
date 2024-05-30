package com.akg.akg_sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomepagePermission {
    private Boolean orderAccess = true;
    private Boolean deliveryAccess = true;
    private Boolean paymentAccess = true;
    private Boolean reportAccess = true;
    private Boolean resetPasswordAccess = true;
    private Boolean cmsAccess = false;
}
