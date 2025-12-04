package com.medical.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class DoctorDtos {

    @Data
    public static class DoctorWithSlots {
        private String userId;
        private String doctorName;
        private String departmentId;
        private String title;
        private String specialty;
        private List<String> availableTimeSlots;
    }
}

