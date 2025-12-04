package com.medical.backend.dto;

import lombok.Data;

@Data
public class PatientDtos {

    @Data
    public static class PatientProfile {
        private String userId;
        private String userName;
        private String phone;
        private String gender;
        private Integer age;
        private String idCard;
        // 余额（分）
        private Integer balance;
    }

    @Data
    public static class RechargeRequest {
        // 金额（元），前端传小数，后端转换为分
        private Double amount;
    }
}

