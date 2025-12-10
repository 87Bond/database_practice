package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.RegistrationDtos;
import com.medical.backend.mapper.DoctorMapper;
import com.medical.backend.mapper.DoctorTimeSlotMapper;
import com.medical.backend.mapper.RegistrationMapper;
import com.medical.backend.mapper.PatientMapper;
import com.medical.backend.model.Doctor;
import com.medical.backend.model.DoctorTimeSlot;
import com.medical.backend.model.Registration;
import com.medical.backend.model.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    private final RegistrationMapper registrationMapper;
    private final DoctorTimeSlotMapper doctorTimeSlotMapper;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;

    // 取消挂号时的违约手续费比例（如 10%）
    private static final double CANCEL_PENALTY_RATE = 0.1;

    public RegistrationService(RegistrationMapper registrationMapper,
                               DoctorTimeSlotMapper doctorTimeSlotMapper,
                               DoctorMapper doctorMapper,
                               PatientMapper patientMapper) {
        this.registrationMapper = registrationMapper;
        this.doctorTimeSlotMapper = doctorTimeSlotMapper;
        this.doctorMapper = doctorMapper;
        this.patientMapper = patientMapper;
    }

    public ApiResponse<Map<String, Object>> querySlots(LocalDate date, String departmentId, String doctorId) {
        List<DoctorTimeSlot> slots = doctorTimeSlotMapper.findSlots(date, departmentId, doctorId);
        List<Map<String, Object>> slotList = slots.stream()
                .map(s -> Map.<String, Object>of(
                        "slotId", s.getSlotId(),
                        "startTime", s.getStartTime() != null ? s.getStartTime().toString().substring(0, 5) : null,
                        "endTime", s.getEndTime() != null ? s.getEndTime().toString().substring(0, 5) : null,
                        "label", s.getStartTime() != null && s.getEndTime() != null
                                ? s.getStartTime().toString().substring(0, 5) + "-" + s.getEndTime().toString().substring(0, 5)
                                : s.getTimeSlot(),
                        "fee", s.getFee(),
                        "remain", s.getCapacity() - s.getBookedCount()))
                .collect(Collectors.toList());
        Map<String, Object> data = Map.of(
                "date", date.toString(),
                "departmentId", departmentId,
                "doctorId", doctorId,
                "slots", slotList
        );
        return ApiResponse.success(data);
    }

    @Transactional
    public ApiResponse<Map<String, Object>> createRegistration(String patientId,
                                                               RegistrationDtos.CreateRegistrationRequest req) {
        // 不允许预约过去的日期
        LocalDate today = LocalDate.now();
        if (req.getRegDate() == null || req.getRegDate().isBefore(today)) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "不能预约过去的日期");
        }

        // 校验医生与科室是否匹配
        List<Doctor> doctors = doctorMapper.findByDepartment(req.getDepartmentId());
        boolean match = doctors.stream().anyMatch(d -> d.getUserId().equals(req.getDoctorId()));
        if (!match) {
            return ApiResponse.error(ErrorCodes.DOCTOR_DEPT_MISMATCH, "医生不属于该科室");
        }

        // 找到对应号源（优先使用 slotId，兼容旧的只传 regTimeSlot 的情况）
        DoctorTimeSlot targetSlot = null;
        if (req.getSlotId() != null && !req.getSlotId().isEmpty()) {
            targetSlot = doctorTimeSlotMapper.findById(req.getSlotId());
            if (targetSlot != null) {
                // 基本一致性校验，防止前端传入不匹配的 slotId
                if (!req.getDoctorId().equals(targetSlot.getDoctorId())
                        || !req.getDepartmentId().equals(targetSlot.getDepartmentId())
                        || !req.getRegDate().equals(targetSlot.getSlotDate())) {
                    return ApiResponse.error(ErrorCodes.PARAM_ERROR, "号源与医生/科室/日期不匹配");
                }
            }
        } else {
            // 兼容旧逻辑，使用 regTimeSlot 作为标识
            List<DoctorTimeSlot> slots = doctorTimeSlotMapper.findSlots(
                    req.getRegDate(), req.getDepartmentId(), req.getDoctorId());
            targetSlot = slots.stream()
                    .filter(s -> s.getTimeSlot().equals(req.getRegTimeSlot()))
                    .findFirst()
                    .orElse(null);
        }

        if (targetSlot == null || targetSlot.getCapacity() <= targetSlot.getBookedCount()) {
            return ApiResponse.error(ErrorCodes.SLOT_NOT_ENOUGH, "该时段号源不足");
        }

        // 限制：同一患者在同一号源（时间段）最多只能预约一次（未取消的）
        int existed = registrationMapper.countByPatientAndSlot(patientId, targetSlot.getSlotId());
        if (existed > 0) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "同一时间段您已预约过该医生");
        }

        String regId = IdGenerator.newRegistrationId();
        LocalDateTime now = LocalDateTime.now();

        Registration registration = new Registration();
        registration.setRegId(regId);
        registration.setPatientId(patientId);
        registration.setDoctorId(req.getDoctorId());
        registration.setDepartmentId(req.getDepartmentId());
        registration.setRegDate(req.getRegDate());
        // 统一将挂号记录里的时间段文字填成“HH:mm-HH:mm”，便于历史查看
        String regTimeLabel;
        if (targetSlot.getStartTime() != null && targetSlot.getEndTime() != null) {
            String start = targetSlot.getStartTime().toString().substring(0, 5);
            String end = targetSlot.getEndTime().toString().substring(0, 5);
            regTimeLabel = start + "-" + end;
        } else if (req.getRegTimeSlot() != null) {
            regTimeLabel = req.getRegTimeSlot();
        } else {
            regTimeLabel = targetSlot.getTimeSlot();
        }
        registration.setRegTimeSlot(regTimeLabel);
        registration.setRegStatus("Booked");
        int fee = targetSlot.getFee() != null ? targetSlot.getFee() : 3000;
        registration.setRegFee(fee);
        registration.setPayStatus("Unpaid");
        registration.setCreateTime(now);
        registration.setSlotId(targetSlot.getSlotId());
        registrationMapper.insert(registration);

        doctorTimeSlotMapper.changeBookedCount(targetSlot.getSlotId(), 1);

        Map<String, Object> data = Map.of(
                "regId", regId,
                "regStatus", "Booked",
                "regFee", fee,
                "payStatus", "Unpaid",
                "createTime", now.toString()
        );
        return ApiResponse.success("挂号成功", data);
    }

    @Transactional
    public ApiResponse<Map<String, Object>> cancelRegistration(String regId) {
        Registration registration = registrationMapper.findById(regId);
        if (registration == null) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "挂号记录不存在");
        }
        if (!"Booked".equals(registration.getRegStatus())) {
            return ApiResponse.error(ErrorCodes.CANCEL_TIME_EXCEEDED, "已过可取消时间");
        }

        // 退还部分费用（违约金）
        if ("Paid".equals(registration.getPayStatus())) {
            Patient patient = patientMapper.findById(registration.getPatientId());
            if (patient != null) {
                int fee = registration.getRegFee() == null ? 0 : registration.getRegFee();
                int refund = (int) Math.round(fee * (1.0 - CANCEL_PENALTY_RATE));
                int balance = patient.getMoney() == null ? 0 : patient.getMoney();
                patientMapper.updateMoney(patient.getUserId(), balance + refund);
            }
        }

        registrationMapper.updateStatus(regId, "Canceled", "Refunded", registration.getPaidAt());
        if (registration.getSlotId() != null) {
            doctorTimeSlotMapper.changeBookedCount(registration.getSlotId(), -1);
        }
        Map<String, Object> data = Map.of(
                "regStatus", "Canceled",
                "payStatus", "Refunded"
        );
        return ApiResponse.success("已取消", data);
    }

    @Transactional
    public ApiResponse<Map<String, Object>> payRegistration(String regId, String payChannel) {
        Registration registration = registrationMapper.findById(regId);
        if (registration == null) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "挂号记录不存在");
        }
        if ("Paid".equals(registration.getPayStatus())) {
            return ApiResponse.success(Map.of(
                    "payStatus", "Paid",
                    "paidAt", registration.getPaidAt() != null ? registration.getPaidAt().toString() : null
            ));
        }

        Patient patient = patientMapper.findById(registration.getPatientId());
        if (patient == null) {
            return ApiResponse.error(ErrorCodes.ACCOUNT_NOT_FOUND, "患者不存在");
        }
        int fee = registration.getRegFee() == null ? 0 : registration.getRegFee();
        int balance = patient.getMoney() == null ? 0 : patient.getMoney();
        if (balance < fee) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "余额不足，请先充值");
        }

        LocalDateTime now = LocalDateTime.now();
        registrationMapper.updateStatus(regId, registration.getRegStatus(), "Paid", now);
        patientMapper.updateMoney(patient.getUserId(), balance - fee);
        Map<String, Object> data = Map.of(
                "payStatus", "Paid",
                "paidAt", now.toString()
        );
        return ApiResponse.success("支付成功", data);
    }

    public ApiResponse<List<RegistrationDtos.RegistrationSummary>> listRegistrations(
            String patientId,
            LocalDate from,
            LocalDate to,
            String departmentId,
            String status) {
        List<Registration> registrations =
                registrationMapper.findByConditions(patientId, from, to, departmentId, status);
        List<RegistrationDtos.RegistrationSummary> list = registrations.stream().map(r -> {
            RegistrationDtos.RegistrationSummary dto = new RegistrationDtos.RegistrationSummary();
            dto.setRegId(r.getRegId());
            dto.setRegDate(r.getRegDate());
            dto.setRegTimeSlot(r.getRegTimeSlot());
            dto.setDepartmentId(r.getDepartmentId());
            dto.setDepartmentName(r.getDepartmentName());
            dto.setDepartmentLocation(r.getDepartmentLocation());
            dto.setDoctorId(r.getDoctorId());
            dto.setDoctorName(r.getDoctorName());
            dto.setRegFee(r.getRegFee());
            dto.setRegStatus(r.getRegStatus());
            dto.setPayStatus(r.getPayStatus());
            return dto;
        }).collect(Collectors.toList());
        return ApiResponse.success(list);
    }
}
