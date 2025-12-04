package com.medical.backend.model;

import lombok.Data;

@Data
public class Doctor {
    private String userId;
    private String doctorName;
    private String departmentId;
    private String title;
    private String specialty;
    private String workPhone;
}
