package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.dto.DepartmentDtos;
import com.medical.backend.mapper.DepartmentMapper;
import com.medical.backend.model.Department;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    public ApiResponse<List<DepartmentDtos.DepartmentSimple>> listDepartments(String keyword) {
        List<Department> departments = departmentMapper.findByKeyword(keyword);
        List<DepartmentDtos.DepartmentSimple> list = departments.stream().map(d -> {
            DepartmentDtos.DepartmentSimple dto = new DepartmentDtos.DepartmentSimple();
            dto.setDepartmentId(d.getDepartmentId());
            dto.setDepartmentName(d.getDepartmentName());
            dto.setLocation(d.getLocation());
            return dto;
        }).collect(Collectors.toList());
        return ApiResponse.success(list);
    }
}

