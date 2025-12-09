package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.DeptManagerDtos;
import com.medical.backend.mapper.UserDirectoryMapper;
import com.medical.backend.service.DeptManagerService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dept")
public class DeptManagerController {

    private final DeptManagerService deptManagerService;
    private final UserDirectoryMapper userDirectoryMapper;

    public DeptManagerController(DeptManagerService deptManagerService,
                                 UserDirectoryMapper userDirectoryMapper) {
        this.deptManagerService = deptManagerService;
        this.userDirectoryMapper = userDirectoryMapper;
    }

    private boolean ensureDeptManager(String userId) {
        String role = userDirectoryMapper.findRoleByUserId(userId);
        return "dept_manager".equals(role);
    }

    /**
     * 查询本科室下的医生列表
     */
    @GetMapping("/doctors")
    public ApiResponse<List<DeptManagerDtos.DoctorSimple>> listDoctors(
            @RequestHeader("X-User-Id") String userId) {
        if (!ensureDeptManager(userId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可访问该接口");
        }
        return deptManagerService.listDoctors(userId);
    }

    /**
     * 为本科室医生创建号源（起止时间+容量）
     */
    @PostMapping("/slots")
    public ApiResponse<?> createSlot(@RequestHeader("X-User-Id") String userId,
                                     @RequestBody DeptManagerDtos.CreateSlotRequest req) {
        if (!ensureDeptManager(userId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可维护号源");
        }
        return deptManagerService.createSlot(userId, req);
    }

    /**
     * 查看本科室在指定日期的全部号源
     */
    @GetMapping("/slots")
    public ApiResponse<List<DeptManagerDtos.SlotView>> listSlots(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (!ensureDeptManager(userId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可查看号源");
        }
        return deptManagerService.listSlots(userId, date);
    }

    /**
     * 查看本科室下所有医生的挂号记录
     */
    @GetMapping("/registrations")
    public ApiResponse<List<DeptManagerDtos.DeptRegistrationView>> listRegistrations(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (!ensureDeptManager(userId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可查看挂号记录");
        }
        return deptManagerService.listRegistrations(userId, date);
    }

    /**
     * 将已有医生划归到当前科室
     */
    @PostMapping("/doctors/{doctorId}/assign")
    public ApiResponse<?> assignDoctor(@RequestHeader("X-User-Id") String userId,
                                       @PathVariable("doctorId") String doctorId) {
        if (!ensureDeptManager(userId)) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可维护本科室医生");
        }
        return deptManagerService.assignDoctorToDepartment(userId, doctorId);
    }
}
