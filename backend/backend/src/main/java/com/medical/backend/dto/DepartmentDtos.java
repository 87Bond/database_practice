package com.medical.backend.dto;

import lombok.Data;

@Data
public class DepartmentDtos {

    @Data
    public static class DepartmentSimple {
        private String departmentId;
        private String departmentName;
        private String location;
    }
}

