package com.medical.backend.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Registration {
    private String regId;
    private String patientId;
    private String doctorId;
    private String departmentId;
    private LocalDate regDate;
    private String regTimeSlot;
    private String regStatus;
    private Integer regFee;
    private String payStatus;
    private LocalDateTime createTime;
    private LocalDateTime paidAt;
    private String slotId;
}

