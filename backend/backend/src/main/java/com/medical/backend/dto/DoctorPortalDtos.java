package com.medical.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DoctorPortalDtos {

    @Data
    public static class DoctorRegistrationView {
        private String regId;
        private String patientId;
        private String patientName;
        private LocalDate regDate;
        private String regTimeSlot;
        private String regStatus;
        private String payStatus;
    }
}
