package com.medical.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class DeptManagerDtos {

    @Data
    public static class DoctorSimple {
        private String userId;
        private String doctorName;
        private String title;
        private String departmentId;
    }

    @Data
    public static class CreateSlotRequest {
        private String doctorId;
        private LocalDate slotDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer capacity;
        /**
         * 挂号费用（单位：元），后台会换算成分存入数据库
         */
        private Integer feeYuan;
        private String note;
    }

    @Data
    public static class SlotView {
        private String slotId;
        private String doctorId;
        private String doctorName;
        private LocalDate slotDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private Integer capacity;
        private Integer bookedCount;
        // 号源费用（单位：分）
        private Integer fee;
        private String status;
        private String note;
    }

    @Data
    public static class DeptRegistrationView {
        private String regId;
        private String patientId;
        private String patientName;
        private String doctorId;
        private String doctorName;
        private LocalDate regDate;
        private String regTimeSlot;
        private String regStatus;
        private String payStatus;
    }
}
