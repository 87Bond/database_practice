package com.medical.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalRecord {
    private String recordId;
    private String regId;
    private String diagnosis;
    private String treatmentPlan;
    private String advice;
    private LocalDateTime createTime;
}

