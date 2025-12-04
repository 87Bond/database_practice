package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.MedicalRecordDtos;
import com.medical.backend.mapper.UserDirectoryMapper;
import com.medical.backend.service.MedicalRecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final UserDirectoryMapper userDirectoryMapper;

    public MedicalRecordController(MedicalRecordService medicalRecordService,
                                   UserDirectoryMapper userDirectoryMapper) {
        this.medicalRecordService = medicalRecordService;
        this.userDirectoryMapper = userDirectoryMapper;
    }

    @GetMapping
    public ApiResponse<List<MedicalRecordDtos.PatientRecordView>> listForPatient(
            @RequestHeader("X-User-Id") String userId) {
        String role = userDirectoryMapper.findRoleByUserId(userId);
        if (!"patient".equals(role)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅患者可查看自己的就诊记录");
        }
        return medicalRecordService.listForPatient(userId);
    }

    @GetMapping("/doctor")
    public ApiResponse<List<MedicalRecordDtos.PatientRecordView>> listForDoctor(
            @RequestHeader("X-User-Id") String userId) {
        String role = userDirectoryMapper.findRoleByUserId(userId);
        if (!"doctor".equals(role)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅医生可查看自己的诊疗记录");
        }
        return medicalRecordService.listForDoctor(userId);
    }

    @PostMapping
    public ApiResponse<?> createRecord(@RequestHeader("X-User-Id") String userId,
                                       @RequestBody MedicalRecordDtos.CreateRecordRequest req) {
        String role = userDirectoryMapper.findRoleByUserId(userId);
        if (!"doctor".equals(role)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅医生可录入诊疗记录");
        }
        return medicalRecordService.createRecord(userId, req);
    }
}
