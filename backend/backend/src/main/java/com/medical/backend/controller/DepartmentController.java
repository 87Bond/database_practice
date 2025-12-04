package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.dto.DepartmentDtos;
import com.medical.backend.service.DepartmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ApiResponse<List<DepartmentDtos.DepartmentSimple>> listDepartments(
            @RequestParam(value = "keyword", required = false) String keyword) {
        return departmentService.listDepartments(keyword);
    }
}

