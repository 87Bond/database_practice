package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.DeptManagerDtos;
import com.medical.backend.mapper.*;
import com.medical.backend.model.DepartmentManager;
import com.medical.backend.model.Doctor;
import com.medical.backend.model.DoctorTimeSlot;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptManagerService {

    private final DepartmentManagerMapper departmentManagerMapper;
    private final DoctorMapper doctorMapper;
    private final DoctorTimeSlotMapper doctorTimeSlotMapper;
    private final RegistrationMapper registrationMapper;
    private final UserDirectoryMapper userDirectoryMapper;

    public DeptManagerService(DepartmentManagerMapper departmentManagerMapper,
                              DoctorMapper doctorMapper,
                              DoctorTimeSlotMapper doctorTimeSlotMapper,
                              RegistrationMapper registrationMapper,
                              UserDirectoryMapper userDirectoryMapper) {
        this.departmentManagerMapper = departmentManagerMapper;
        this.doctorMapper = doctorMapper;
        this.doctorTimeSlotMapper = doctorTimeSlotMapper;
        this.registrationMapper = registrationMapper;
        this.userDirectoryMapper = userDirectoryMapper;
    }

    private DepartmentManager requireManager(String userId) {
        return departmentManagerMapper.findByUserId(userId);
    }

    public ApiResponse<List<DeptManagerDtos.DoctorSimple>> listDoctors(String managerId) {
        DepartmentManager manager = requireManager(managerId);
        if (manager == null) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可访问该接口");
        }
        List<Doctor> doctors = doctorMapper.findByDepartment(manager.getDepartmentId());
        List<DeptManagerDtos.DoctorSimple> list = doctors.stream().map(d -> {
            DeptManagerDtos.DoctorSimple dto = new DeptManagerDtos.DoctorSimple();
            dto.setUserId(d.getUserId());
            dto.setDoctorName(d.getDoctorName());
            dto.setTitle(d.getTitle());
            dto.setDepartmentId(d.getDepartmentId());
            return dto;
        }).collect(Collectors.toList());
        return ApiResponse.success(list);
    }

    public ApiResponse<?> createSlot(String managerId, DeptManagerDtos.CreateSlotRequest req) {
        DepartmentManager manager = requireManager(managerId);
        if (manager == null) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可维护号源");
        }
        Doctor doctor = doctorMapper.findById(req.getDoctorId());
        if (doctor == null || !manager.getDepartmentId().equals(doctor.getDepartmentId())) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "只能为本科室医生分配号源");
        }
        if (req.getSlotDate() == null || req.getStartTime() == null || req.getEndTime() == null
                || req.getCapacity() == null || req.getCapacity() <= 0
                || req.getFeeYuan() == null || req.getFeeYuan() <= 0) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "请填写完整的日期、时间段、号源数量和挂号费用");
        }
        if (!req.getStartTime().isBefore(req.getEndTime())) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "开始时间必须早于结束时间");
        }

        // 检查时间重叠
        List<DoctorTimeSlot> exists = doctorTimeSlotMapper.findByDoctorAndDate(doctor.getUserId(), req.getSlotDate());
        boolean overlap = exists.stream().anyMatch(s ->
                // not (end <= existing start or start >= existing end)
                !(req.getEndTime().compareTo(s.getStartTime()) <= 0
                        || req.getStartTime().compareTo(s.getEndTime()) >= 0)
        );
        if (overlap) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "该医生在该日期该时间段已有号源，请调整时间");
        }

        DoctorTimeSlot slot = new DoctorTimeSlot();
        slot.setSlotId(IdGenerator.newSlotId());
        slot.setDoctorId(doctor.getUserId());
        slot.setDepartmentId(manager.getDepartmentId());
        slot.setSlotDate(req.getSlotDate());
        // 将 time_slot 保存为“HH:mm-HH:mm”的直观形式
        String label = String.format("%s-%s",
                req.getStartTime().toString().substring(0, 5),
                req.getEndTime().toString().substring(0, 5));
        slot.setTimeSlot(label);
        slot.setStartTime(req.getStartTime());
        slot.setEndTime(req.getEndTime());
        slot.setCapacity(req.getCapacity());
        slot.setBookedCount(0);
        // 将传入的“元”换算为“分”存储
        slot.setFee(req.getFeeYuan() * 100);
        slot.setStatus("OPEN");
        slot.setNote(req.getNote());
        LocalDateTime now = LocalDateTime.now();
        slot.setCreateTime(now);
        slot.setUpdateTime(now);
        doctorTimeSlotMapper.insert(slot);
        return ApiResponse.success("号源创建成功", null);
    }

    public ApiResponse<List<DeptManagerDtos.SlotView>> listSlots(String managerId, LocalDate date) {
        DepartmentManager manager = requireManager(managerId);
        if (manager == null) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可查看号源");
        }
        if (date == null) {
            date = LocalDate.now();
        }
        List<DoctorTimeSlot> slots = doctorTimeSlotMapper.findSlots(date, manager.getDepartmentId(), null);
        List<DeptManagerDtos.SlotView> list = slots.stream().map(s -> {
            DeptManagerDtos.SlotView view = new DeptManagerDtos.SlotView();
            view.setSlotId(s.getSlotId());
            view.setDoctorId(s.getDoctorId());
            view.setDoctorName(
                    doctorMapper.findById(s.getDoctorId()) != null
                            ? doctorMapper.findById(s.getDoctorId()).getDoctorName()
                            : null);
            view.setSlotDate(s.getSlotDate());
            view.setStartTime(s.getStartTime());
            view.setEndTime(s.getEndTime());
            view.setCapacity(s.getCapacity());
            view.setBookedCount(s.getBookedCount());
            view.setFee(s.getFee());
            view.setStatus(s.getStatus());
            view.setNote(s.getNote());
            return view;
        }).collect(Collectors.toList());
        return ApiResponse.success(list);
    }

    public ApiResponse<List<DeptManagerDtos.DeptRegistrationView>> listRegistrations(String managerId,
                                                                                     LocalDate date) {
        DepartmentManager manager = requireManager(managerId);
        if (manager == null) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可查看挂号记录");
        }
        List<com.medical.backend.dto.DoctorPortalDtos.DoctorRegistrationView> regs =
                registrationMapper.findDoctorRegistrationsForDepartment(manager.getDepartmentId(), date);
        List<DeptManagerDtos.DeptRegistrationView> list = regs.stream().map(r -> {
            DeptManagerDtos.DeptRegistrationView v = new DeptManagerDtos.DeptRegistrationView();
            v.setRegId(r.getRegId());
            v.setPatientId(r.getPatientId());
            v.setPatientName(r.getPatientName());
            v.setDoctorId(r.getDoctorId());
            v.setDoctorName(
                    doctorMapper.findById(r.getDoctorId()) != null
                            ? doctorMapper.findById(r.getDoctorId()).getDoctorName()
                            : null);
            v.setRegDate(r.getRegDate());
            v.setRegTimeSlot(r.getRegTimeSlot());
            v.setRegStatus(r.getRegStatus());
            v.setPayStatus(r.getPayStatus());
            return v;
        }).collect(Collectors.toList());
        return ApiResponse.success(list);
    }

    /**
     * 将一个已有医生划归到当前科室
     */
    public ApiResponse<?> assignDoctorToDepartment(String managerId, String doctorId) {
        DepartmentManager manager = requireManager(managerId);
        if (manager == null) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅科室管理员可维护本科室医生");
        }
        Doctor doctor = doctorMapper.findById(doctorId);
        if (doctor == null) {
            return ApiResponse.error(ErrorCodes.ACCOUNT_NOT_FOUND, "医生不存在");
        }
        if (manager.getDepartmentId().equals(doctor.getDepartmentId())) {
            return ApiResponse.success("医生已在当前科室", null);
        }
        doctorMapper.updateDepartment(doctorId, manager.getDepartmentId());
        return ApiResponse.success("医生已加入当前科室", null);
    }
}
