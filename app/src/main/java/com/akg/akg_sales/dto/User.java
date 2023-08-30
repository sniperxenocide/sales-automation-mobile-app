package com.akg.akg_sales.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    private String username;
    private String password;
    private String token;
    private String category;
    private String fcmToken;
    private String lastLoginTime;
    private Long loginCount;
}
