package com.medical.backend.dto;

import lombok.Data;

@Data
public class AuthDtos {
    @Data
    public static class PatientRegisterRequest {
        private String patientName;
        private String gender;
        private Integer age;
        private String phone;
        private String password;
        private String idCard;
    }

    @Data
    public static class RegisterResponse {
        private String userId;
        private String createTime;
    }

    @Data
    public static class LoginRequest {
        private String id;
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String userId;
        private String username;
        private String role;
        private String deptId;
        private String activityTime;
        private String token;
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }
}

