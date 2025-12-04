package com.medical.backend.dto;

import lombok.Data;

@Data
public class AdminDtos {

    @Data
    public static class CreateDoctorRequest {
        private String doctorName;
        private String phone;
        private String password;
        private String departmentId;
        private String title;
        private String specialty;
        private String workPhone;
    }

    @Data
    public static class CreateDeptManagerRequest {
        private String userName;
        private String phone;
        private String password;
        private String departmentId;
        private String workPhone;
    }

    @Data
    public static class CreateSystemUserRequest {
        private String userName;
        private String phone;
        private String password;
        private String workPhone;
    }
}

