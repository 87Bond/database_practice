package com.medical.backend.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private String messageId;
    private String title;
    private String content;
    private String createUserId;
    private String targetUserId;
    private LocalDateTime createTime;
    private Boolean isValid;
}

