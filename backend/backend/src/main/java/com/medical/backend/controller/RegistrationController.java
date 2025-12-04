package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.dto.RegistrationDtos;
import com.medical.backend.service.RegistrationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping("/slots")
    public ApiResponse<Map<String, Object>> querySlots(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("departmentId") String departmentId,
            @RequestParam(value = "doctorId", required = false) String doctorId) {
        return registrationService.querySlots(date, departmentId, doctorId);
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> createRegistration(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody RegistrationDtos.CreateRegistrationRequest req) {
        return registrationService.createRegistration(userId, req);
    }

    @PostMapping("/{regId}/cancel")
    public ApiResponse<Map<String, Object>> cancelRegistration(@PathVariable("regId") String regId) {
        return registrationService.cancelRegistration(regId);
    }

    @PostMapping("/{regId}/pay")
    public ApiResponse<Map<String, Object>> payRegistration(
            @PathVariable("regId") String regId,
            @RequestParam("payChannel") String payChannel) {
        return registrationService.payRegistration(regId, payChannel);
    }

    @GetMapping
    public ApiResponse<List<RegistrationDtos.RegistrationSummary>> listRegistrations(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "status", required = false) String status) {
        return registrationService.listRegistrations(userId, from, to, departmentId, status);
    }
}

