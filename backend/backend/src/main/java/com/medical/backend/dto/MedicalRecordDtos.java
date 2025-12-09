package com.medical.backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MedicalRecordDtos {

    @Data
    public static class CreateRecordRequest {
        private String regId;
        private String diagnosis;
        private String treatmentPlan;
        private String advice;
    }

    @Data
    public static class PatientRecordView {
        private String recordId;
        private String regId;
        private String doctorId;
        private String doctorName;
        private String patientId;
        private String patientName;
        private String departmentId;
        private String departmentName;
        private String departmentLocation;
        private LocalDate regDate;
        private String regTimeSlot;
        private String diagnosis;
        private String treatmentPlan;
        private String advice;
        private LocalDateTime createTime;
    }
}
