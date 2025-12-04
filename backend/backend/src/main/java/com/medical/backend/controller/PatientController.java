package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.PatientDtos;
import com.medical.backend.mapper.UserDirectoryMapper;
import com.medical.backend.service.PatientService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final PatientService patientService;
    private final UserDirectoryMapper userDirectoryMapper;

    public PatientController(PatientService patientService,
                             UserDirectoryMapper userDirectoryMapper) {
        this.patientService = patientService;
        this.userDirectoryMapper = userDirectoryMapper;
    }

    @GetMapping("/me")
    public ApiResponse<PatientDtos.PatientProfile> me(@RequestHeader("X-User-Id") String userId) {
        String role = userDirectoryMapper.findRoleByUserId(userId);
        if (!"patient".equals(role)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅患者可以查看个人信息");
        }
        return patientService.getProfile(userId);
    }

    @PostMapping("/recharge")
    public ApiResponse<?> recharge(@RequestHeader("X-User-Id") String userId,
                                   @RequestBody PatientDtos.RechargeRequest req) {
        String role = userDirectoryMapper.findRoleByUserId(userId);
        if (!"patient".equals(role)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅患者可以充值余额");
        }
        return patientService.recharge(userId, req);
    }
}

