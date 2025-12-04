package com.medical.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDirectory {
    private String userId;
    private String userName;
    private String phone;
    private String password;
    private LocalDateTime createTime;
    private Boolean isActive;
}

