package com.medical.backend.controller;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.dto.MessageDtos;
import com.medical.backend.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ApiResponse<MessageDtos.MessageView> sendMessage(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody MessageDtos.SendMessageRequest req) {
        return messageService.sendMessage(userId, req);
    }

    @GetMapping
    public ApiResponse<List<MessageDtos.MessageView>> listMessages(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(value = "box", defaultValue = "inbox") String box,
            @RequestParam(value = "isValid", required = false) Boolean isValid) {
        return messageService.listMessages(userId, box, isValid);
    }

    @GetMapping("/conversations")
    public ApiResponse<List<MessageDtos.ConversationSummary>> listConversationSummary(
            @RequestHeader("X-User-Id") String userId) {
        return messageService.listConversationSummary(userId);
    }

    @GetMapping("/conversation")
    public ApiResponse<List<MessageDtos.MessageView>> listConversation(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam("targetUserId") String targetUserId) {
        return messageService.listConversation(userId, targetUserId);
    }

    @PostMapping("/{messageId}/invalidate")
    public ApiResponse<?> invalidate(@PathVariable("messageId") String messageId) {
        return messageService.invalidateMessage(messageId);
    }
}
