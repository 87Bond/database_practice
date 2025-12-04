package com.medical.backend.model;

import lombok.Data;

@Data
public class Patient {
    private String userId;
    private String gender;
    private Integer age;
    private String idCard;
    private Integer money;
}

