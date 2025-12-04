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
        private String regTimeSlot;
    }

    @Data
    public static class RegistrationSummary {
        private String regId;
        private LocalDate regDate;
        private String regTimeSlot;
        private String departmentId;
        private String doctorId;
        private String regStatus;
        private String payStatus;
    }
}

