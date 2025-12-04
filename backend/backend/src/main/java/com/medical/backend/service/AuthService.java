package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.AuthDtos;
import com.medical.backend.mapper.PatientMapper;
import com.medical.backend.mapper.UserDirectoryMapper;
import com.medical.backend.model.Patient;
import com.medical.backend.model.UserDirectory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserDirectoryMapper userDirectoryMapper;
    private final PatientMapper patientMapper;

    public AuthService(UserDirectoryMapper userDirectoryMapper, PatientMapper patientMapper) {
        this.userDirectoryMapper = userDirectoryMapper;
        this.patientMapper = patientMapper;
    }

    @Transactional
    public ApiResponse<AuthDtos.RegisterResponse> registerPatient(AuthDtos.PatientRegisterRequest req) {
        if (userDirectoryMapper.countByPhone(req.getPhone()) > 0) {
            return ApiResponse.error(ErrorCodes.PHONE_EXISTS, "手机号已存在");
        }
        String userId = IdGenerator.newPatientId();
        LocalDateTime now = LocalDateTime.now();

        UserDirectory user = new UserDirectory();
        user.setUserId(userId);
        user.setUserName(req.getPatientName());
        user.setPhone(req.getPhone());
        user.setPassword(req.getPassword());
        user.setCreateTime(now);
        user.setIsActive(true);
        userDirectoryMapper.insert(user);

        Patient patient = new Patient();
        patient.setUserId(userId);
        patient.setGender(req.getGender());
        patient.setAge(req.getAge());
        patient.setIdCard(req.getIdCard());
        patient.setMoney(0);
        patientMapper.insert(patient);

        AuthDtos.RegisterResponse resp = new AuthDtos.RegisterResponse();
        resp.setUserId(userId);
        resp.setCreateTime(now.toString());
        return ApiResponse.success("注册成功", resp);
    }

    public ApiResponse<AuthDtos.LoginResponse> login(AuthDtos.LoginRequest req) {
        UserDirectory user = userDirectoryMapper.findById(req.getId());
        if (user == null) {
            user = userDirectoryMapper.findByPhone(req.getId());
        }
        if (user == null) {
            return ApiResponse.error(ErrorCodes.ACCOUNT_NOT_FOUND, "账号不存在");
        }
        if (!Boolean.TRUE.equals(user.getIsActive())) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "账号已被冻结");
        }
        if (!user.getPassword().equals(req.getPassword())) {
            return ApiResponse.error(ErrorCodes.PASSWORD_WRONG, "密码错误，请重新输入");
        }
        String role = userDirectoryMapper.findRoleByUserId(user.getUserId());

        AuthDtos.LoginResponse resp = new AuthDtos.LoginResponse();
        resp.setUserId(user.getUserId());
        resp.setUsername(user.getUserName());
        resp.setRole(role);
        resp.setDeptId(null);
        resp.setActivityTime("30m");
        // 简化：token 直接使用 userId
        resp.setToken(user.getUserId());
        return ApiResponse.success("登录成功", resp);
    }

    public ApiResponse<Void> changePassword(String userId, AuthDtos.ChangePasswordRequest req) {
        UserDirectory user = userDirectoryMapper.findById(userId);
        if (user == null) {
            return ApiResponse.error(ErrorCodes.ACCOUNT_NOT_FOUND, "账号不存在");
        }
        if (!user.getPassword().equals(req.getOldPassword())) {
            return ApiResponse.error(ErrorCodes.PASSWORD_WRONG, "原密码错误");
        }
        userDirectoryMapper.updatePassword(userId, req.getNewPassword());
        return ApiResponse.success("修改成功", null);
    }
}

