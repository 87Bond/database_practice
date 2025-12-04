package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.AdminDtos;
import com.medical.backend.mapper.DepartmentManagerMapper;
import com.medical.backend.mapper.DoctorMapper;
import com.medical.backend.mapper.SystemUserMapper;
import com.medical.backend.mapper.UserDirectoryMapper;
import com.medical.backend.model.DepartmentManager;
import com.medical.backend.model.Doctor;
import com.medical.backend.model.SystemUser;
import com.medical.backend.model.UserDirectory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserAdminService {

    private final UserDirectoryMapper userDirectoryMapper;

    private final DoctorMapper doctorMapper;

    private final DepartmentManagerMapper departmentManagerMapper;

    private final SystemUserMapper systemUserMapper;

    public UserAdminService(UserDirectoryMapper userDirectoryMapper,
                            DoctorMapper doctorMapper,
                            DepartmentManagerMapper departmentManagerMapper,
                            SystemUserMapper systemUserMapper) {
        this.userDirectoryMapper = userDirectoryMapper;
        this.doctorMapper = doctorMapper;
        this.departmentManagerMapper = departmentManagerMapper;
        this.systemUserMapper = systemUserMapper;
    }

    private boolean isAdmin(String userId) {
        String role = userDirectoryMapper.findRoleByUserId(userId);
        return "admin".equals(role);
    }

    public ApiResponse<?> freezeUser(String operatorId, String userId) {
        if (!isAdmin(operatorId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "无权限");
        }
        userDirectoryMapper.updateActive(userId, false);
        return ApiResponse.success("已冻结", null);
    }

    public ApiResponse<?> unfreezeUser(String operatorId, String userId) {
        if (!isAdmin(operatorId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "无权限");
        }
        userDirectoryMapper.updateActive(userId, true);
        return ApiResponse.success("已解冻", null);
    }

    @Transactional
    public ApiResponse<?> createDoctor(String operatorId, AdminDtos.CreateDoctorRequest req) {
        if (!isAdmin(operatorId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "无权限");
        }
        if (userDirectoryMapper.countByPhone(req.getPhone()) > 0) {
            return ApiResponse.error(ErrorCodes.PHONE_EXISTS, "手机号已存在");
        }
        String userId = IdGenerator.newDoctorId();
        LocalDateTime now = LocalDateTime.now();

        UserDirectory user = new UserDirectory();
        user.setUserId(userId);
        user.setUserName(req.getDoctorName());
        user.setPhone(req.getPhone());
        user.setPassword(req.getPassword());
        user.setCreateTime(now);
        user.setIsActive(true);
        userDirectoryMapper.insert(user);

        Doctor doctor = new Doctor();
        doctor.setUserId(userId);
        doctor.setDoctorName(req.getDoctorName());
        doctor.setDepartmentId(req.getDepartmentId());
        doctor.setTitle(req.getTitle());
        doctor.setSpecialty(req.getSpecialty());
        doctor.setWorkPhone(req.getWorkPhone());
        doctorMapper.insert(doctor);

        return ApiResponse.success("医生账号创建成功", userId);
    }

    @Transactional
    public ApiResponse<?> createDeptManager(String operatorId, AdminDtos.CreateDeptManagerRequest req) {
        if (!isAdmin(operatorId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "无权限");
        }
        if (userDirectoryMapper.countByPhone(req.getPhone()) > 0) {
            return ApiResponse.error(ErrorCodes.PHONE_EXISTS, "手机号已存在");
        }
        String userId = IdGenerator.newDeptManagerId();
        LocalDateTime now = LocalDateTime.now();

        UserDirectory user = new UserDirectory();
        user.setUserId(userId);
        user.setUserName(req.getUserName());
        user.setPhone(req.getPhone());
        user.setPassword(req.getPassword());
        user.setCreateTime(now);
        user.setIsActive(true);
        userDirectoryMapper.insert(user);

        DepartmentManager manager = new DepartmentManager();
        manager.setUserId(userId);
        manager.setDepartmentId(req.getDepartmentId());
        manager.setWorkPhone(req.getWorkPhone());
        departmentManagerMapper.insert(manager);

        return ApiResponse.success("科室管理员账号创建成功", userId);
    }

    @Transactional
    public ApiResponse<?> createSystemUser(String operatorId, AdminDtos.CreateSystemUserRequest req) {
        if (!isAdmin(operatorId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "无权限");
        }
        if (userDirectoryMapper.countByPhone(req.getPhone()) > 0) {
            return ApiResponse.error(ErrorCodes.PHONE_EXISTS, "手机号已存在");
        }
        String userId = IdGenerator.newAdminId();
        LocalDateTime now = LocalDateTime.now();

        UserDirectory user = new UserDirectory();
        user.setUserId(userId);
        user.setUserName(req.getUserName());
        user.setPhone(req.getPhone());
        user.setPassword(req.getPassword());
        user.setCreateTime(now);
        user.setIsActive(true);
        userDirectoryMapper.insert(user);

        SystemUser systemUser = new SystemUser();
        systemUser.setUserId(userId);
        systemUser.setWorkPhone(req.getWorkPhone());
        systemUserMapper.insert(systemUser);

        return ApiResponse.success("系统管理员账号创建成功", userId);
    }
}

