package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.dto.DoctorPortalDtos;
import com.medical.backend.mapper.RegistrationMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DoctorPortalService {

    private final RegistrationMapper registrationMapper;

    public DoctorPortalService(RegistrationMapper registrationMapper) {
        this.registrationMapper = registrationMapper;
    }

    public ApiResponse<List<DoctorPortalDtos.DoctorRegistrationView>> listDoctorRegistrations(
            String doctorId, LocalDate date) {
        List<DoctorPortalDtos.DoctorRegistrationView> list =
                registrationMapper.findDoctorRegistrations(doctorId, date);
        return ApiResponse.success(list);
    }
}

