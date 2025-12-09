package com.medical.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class DoctorDtos {

    /**
     * 单个号源信息，用于前端展示和挂号选择
     */
    @Data
    public static class SlotOption {
        private String slotId;
        /**
         * 人类可读的时间段，如 "09:00-10:00"
         */
        private String label;
        private String startTime;
        private String endTime;
        private Integer remain;
    }

    @Data
    public static class DoctorWithSlots {
        private String userId;
        private String doctorName;
        private String departmentId;
        private String title;
        private String specialty;
        /**
         * 可预约的号源列表
         */
        private List<SlotOption> availableSlots;
    }
}
