package com.medical.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RegistrationDtos {

    @Data
    public static class CreateRegistrationRequest {
        private String doctorId;
        private String departmentId;
        private LocalDate regDate;
        /**
         * 号源主键，用于精确定位一个时间段
         */
        private String slotId;
        /**
         * 可选：前端传过来的展示用时间段文字，未传则后端根据号源的开始结束时间填充
         */
        private String regTimeSlot;
    }

    @Data
    public static class RegistrationSummary {
        private String regId;
        private LocalDate regDate;
        private String regTimeSlot;
        private String departmentId;
        private String departmentName;
        private String departmentLocation;
        private String doctorId;
        private String doctorName;
        /**
         * 挂号费用（单位：分）
         */
        private Integer regFee;
        private String regStatus;
        private String payStatus;
    }
}
