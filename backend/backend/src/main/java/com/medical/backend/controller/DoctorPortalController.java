package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.DoctorPortalDtos;
import com.medical.backend.mapper.UserDirectoryMapper;
import com.medical.backend.service.DoctorPortalService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
public class DoctorPortalController {

    private final DoctorPortalService doctorPortalService;
    private final UserDirectoryMapper userDirectoryMapper;

    public DoctorPortalController(DoctorPortalService doctorPortalService,
                                  UserDirectoryMapper userDirectoryMapper) {
        this.doctorPortalService = doctorPortalService;
        this.userDirectoryMapper = userDirectoryMapper;
    }

    @GetMapping("/registrations")
    public ApiResponse<List<DoctorPortalDtos.DoctorRegistrationView>> doctorRegistrations(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String role = userDirectoryMapper.findRoleByUserId(userId);
        if (!"doctor".equals(role)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅医生可查看本人的挂号信息");
        }
        return doctorPortalService.listDoctorRegistrations(userId, date);
    }
}

