package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.dto.DoctorDtos;
import com.medical.backend.mapper.DoctorMapper;
import com.medical.backend.mapper.DoctorTimeSlotMapper;
import com.medical.backend.model.Doctor;
import com.medical.backend.model.DoctorTimeSlot;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorMapper doctorMapper;
    private final DoctorTimeSlotMapper doctorTimeSlotMapper;

    public DoctorService(DoctorMapper doctorMapper, DoctorTimeSlotMapper doctorTimeSlotMapper) {
        this.doctorMapper = doctorMapper;
        this.doctorTimeSlotMapper = doctorTimeSlotMapper;
    }

    public ApiResponse<List<DoctorDtos.DoctorWithSlots>> listDoctors(String departmentId, LocalDate date) {
        List<Doctor> doctors = doctorMapper.findByDepartment(departmentId);
        List<DoctorDtos.DoctorWithSlots> result = new ArrayList<>();
        for (Doctor d : doctors) {
            DoctorDtos.DoctorWithSlots dto = new DoctorDtos.DoctorWithSlots();
            dto.setUserId(d.getUserId());
            dto.setDoctorName(d.getDoctorName());
            dto.setDepartmentId(d.getDepartmentId());
            dto.setTitle(d.getTitle());
            dto.setSpecialty(d.getSpecialty());
            if (date != null) {
                List<DoctorTimeSlot> slots = doctorTimeSlotMapper.findByDoctorAndDate(d.getUserId(), date);
                List<DoctorDtos.SlotOption> availableSlots = slots.stream()
                        .filter(s -> s.getCapacity() != null
                                && s.getBookedCount() != null
                                && s.getCapacity() > s.getBookedCount())
                        .map(s -> {
                            DoctorDtos.SlotOption option = new DoctorDtos.SlotOption();
                            option.setSlotId(s.getSlotId());
                            String start = s.getStartTime() != null ? s.getStartTime().toString().substring(0, 5) : "";
                            String end = s.getEndTime() != null ? s.getEndTime().toString().substring(0, 5) : "";
                            option.setStartTime(start);
                            option.setEndTime(end);
                            String label = !start.isEmpty() && !end.isEmpty()
                                    ? start + "-" + end
                                    : s.getTimeSlot();
                            option.setLabel(label);
                            option.setRemain(s.getCapacity() - s.getBookedCount());
                            return option;
                        })
                        .collect(Collectors.toList());
                dto.setAvailableSlots(availableSlots);
            }
            result.add(dto);
        }
        return ApiResponse.success(result);
    }
}
