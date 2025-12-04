package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.dto.AdminDtos;
import com.medical.backend.service.UserAdminService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @PostMapping("/{userId}/freeze")
    public ApiResponse<?> freeze(@RequestHeader("X-User-Id") String operatorId,
                                 @PathVariable("userId") String userId) {
        return userAdminService.freezeUser(operatorId, userId);
    }

    @PostMapping("/{userId}/unfreeze")
    public ApiResponse<?> unfreeze(@RequestHeader("X-User-Id") String operatorId,
                                   @PathVariable("userId") String userId) {
        return userAdminService.unfreezeUser(operatorId, userId);
    }

    @PostMapping("/doctors")
    public ApiResponse<?> createDoctor(@RequestHeader("X-User-Id") String operatorId,
                                       @RequestBody AdminDtos.CreateDoctorRequest req) {
        return userAdminService.createDoctor(operatorId, req);
    }

    @PostMapping("/dept-managers")
    public ApiResponse<?> createDeptManager(@RequestHeader("X-User-Id") String operatorId,
                                            @RequestBody AdminDtos.CreateDeptManagerRequest req) {
        return userAdminService.createDeptManager(operatorId, req);
    }

    @PostMapping("/system-users")
    public ApiResponse<?> createSystemUser(@RequestHeader("X-User-Id") String operatorId,
                                           @RequestBody AdminDtos.CreateSystemUserRequest req) {
        return userAdminService.createSystemUser(operatorId, req);
    }
}

