package com.medical.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDtos {

    @Data
    public static class SendMessageRequest {
        private String title;
        private String content;
        private String targetUserId;
    }

    @Data
    public static class MessageView {
        private String messageId;
        private String title;
        private String content;
        private String createUserId;
        private String targetUserId;
        private String createUserName;
        private String targetUserName;
        private LocalDateTime createTime;
        private Boolean isValid;
    }

    /**
     * 会话列表视图：每个 target 一行，按最近消息时间排序
     */
    @Data
    public static class ConversationSummary {
        private String contactUserId;
        private String contactUserName;
        private String lastTitle;
        private String lastContent;
        private LocalDateTime lastTime;
    }
}
