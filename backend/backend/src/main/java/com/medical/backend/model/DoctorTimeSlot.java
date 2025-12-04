package com.medical.backend.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class DoctorTimeSlot {
    private String slotId;
    private String doctorId;
    private String departmentId;
    private LocalDate slotDate;
    private String timeSlot;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer capacity;
    private Integer bookedCount;
    private String status;
    private String note;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

