package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.PatientDtos;
import com.medical.backend.mapper.PatientMapper;
import com.medical.backend.mapper.UserDirectoryMapper;
import com.medical.backend.model.Patient;
import com.medical.backend.model.UserDirectory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final UserDirectoryMapper userDirectoryMapper;
    private final PatientMapper patientMapper;

    public PatientService(UserDirectoryMapper userDirectoryMapper, PatientMapper patientMapper) {
        this.userDirectoryMapper = userDirectoryMapper;
        this.patientMapper = patientMapper;
    }

    public ApiResponse<PatientDtos.PatientProfile> getProfile(String userId) {
        UserDirectory user = userDirectoryMapper.findById(userId);
        Patient patient = patientMapper.findById(userId);
        if (user == null || patient == null) {
            return ApiResponse.error(ErrorCodes.ACCOUNT_NOT_FOUND, "患者不存在");
        }
        PatientDtos.PatientProfile p = new PatientDtos.PatientProfile();
        p.setUserId(user.getUserId());
        p.setUserName(user.getUserName());
        p.setPhone(user.getPhone());
        p.setGender(patient.getGender());
        p.setAge(patient.getAge());
        p.setIdCard(patient.getIdCard());
        p.setBalance(patient.getMoney());
        return ApiResponse.success(p);
    }

    @Transactional
    public ApiResponse<?> recharge(String userId, PatientDtos.RechargeRequest req) {
        if (req.getAmount() == null || req.getAmount() <= 0) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "充值金额必须大于 0");
        }
        int delta = (int) Math.round(req.getAmount() * 100); // 元 -> 分
        Patient patient = patientMapper.findById(userId);
        if (patient == null) {
            return ApiResponse.error(ErrorCodes.ACCOUNT_NOT_FOUND, "患者不存在");
        }
        int newBalance = (patient.getMoney() == null ? 0 : patient.getMoney()) + delta;
        patientMapper.updateMoney(userId, newBalance);
        return ApiResponse.success("充值成功", null);
    }
}

