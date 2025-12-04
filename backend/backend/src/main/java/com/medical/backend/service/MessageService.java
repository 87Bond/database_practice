package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.MessageDtos;
import com.medical.backend.mapper.*;
import com.medical.backend.model.DepartmentManager;
import com.medical.backend.model.Doctor;
import com.medical.backend.model.Message;
import com.medical.backend.model.SystemUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    private final MessageMapper messageMapper;

    private final UserDirectoryMapper userDirectoryMapper;
    private final RegistrationMapper registrationMapper;
    private final DoctorMapper doctorMapper;
    private final DepartmentManagerMapper departmentManagerMapper;
    private final SystemUserMapper systemUserMapper;

    public MessageService(MessageMapper messageMapper,
                          UserDirectoryMapper userDirectoryMapper,
                          RegistrationMapper registrationMapper,
                          DoctorMapper doctorMapper,
                          DepartmentManagerMapper departmentManagerMapper,
                          SystemUserMapper systemUserMapper) {
        this.messageMapper = messageMapper;
        this.userDirectoryMapper = userDirectoryMapper;
        this.registrationMapper = registrationMapper;
        this.doctorMapper = doctorMapper;
        this.departmentManagerMapper = departmentManagerMapper;
        this.systemUserMapper = systemUserMapper;
    }

    public ApiResponse<MessageDtos.MessageView> sendMessage(String createUserId, MessageDtos.SendMessageRequest req) {
        String senderRole = userDirectoryMapper.findRoleByUserId(createUserId);
        String targetRole = userDirectoryMapper.findRoleByUserId(req.getTargetUserId());

        // 患者：只能给就诊过的医生发消息
        if ("patient".equals(senderRole)) {
            if (!"doctor".equals(targetRole)) {
                return ApiResponse.error(ErrorCodes.NO_PERMISSION, "患者只能给就诊医生发消息");
            }
            int count = registrationMapper.countByPatientAndDoctor(createUserId, req.getTargetUserId());
            if (count <= 0) {
                return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅可向挂号过的医生发送消息");
            }
        }

        // 医生：可以给就诊患者、科室管理员、本院管理员发消息
        if ("doctor".equals(senderRole)) {
            Doctor doctor = doctorMapper.findById(createUserId);
            if (doctor == null) {
                return ApiResponse.error(ErrorCodes.NO_PERMISSION, "医生信息不存在");
            }
            if ("patient".equals(targetRole)) {
                int count = registrationMapper.countByDoctorAndPatient(createUserId, req.getTargetUserId());
                if (count <= 0) {
                    return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅可向预约了你号的患者发送消息");
                }
            } else if ("dept_manager".equals(targetRole)) {
                DepartmentManager manager = departmentManagerMapper.findByUserId(req.getTargetUserId());
                if (manager == null || !doctor.getDepartmentId().equals(manager.getDepartmentId())) {
                    return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅可向本科室管理员发送消息");
                }
            } else if ("admin".equals(targetRole)) {
                SystemUser admin = systemUserMapper.findByUserId(req.getTargetUserId());
                if (admin == null) {
                    return ApiResponse.error(ErrorCodes.NO_PERMISSION, "目标管理员不存在");
                }
            } else {
                return ApiResponse.error(ErrorCodes.NO_PERMISSION, "医生仅可向患者/科室管理员/系统管理员发送消息");
            }
        }

        // 科室管理员 / 系统管理员：这里简单允许给任何人发消息

        Message message = new Message();
        message.setMessageId(IdGenerator.newMessageId());
        message.setTitle(req.getTitle());
        message.setContent(req.getContent());
        message.setCreateUserId(createUserId);
        message.setTargetUserId(req.getTargetUserId());
        message.setCreateTime(LocalDateTime.now());
        message.setIsValid(true);
        messageMapper.insert(message);

        MessageDtos.MessageView view = new MessageDtos.MessageView();
        view.setMessageId(message.getMessageId());
        view.setTitle(message.getTitle());
        view.setContent(message.getContent());
        view.setCreateUserId(message.getCreateUserId());
        view.setTargetUserId(message.getTargetUserId());
        view.setCreateTime(message.getCreateTime());
        view.setIsValid(message.getIsValid());
        return ApiResponse.success("发送成功", view);
    }

    public ApiResponse<List<MessageDtos.MessageView>> listMessages(String userId, String box, Boolean isValid) {
        boolean inbox = !"outbox".equalsIgnoreCase(box);
        List<Message> messages = messageMapper.listMessages(userId, inbox, isValid);
        List<MessageDtos.MessageView> list = messages.stream().map(m -> {
            MessageDtos.MessageView view = new MessageDtos.MessageView();
            view.setMessageId(m.getMessageId());
            view.setTitle(m.getTitle());
            view.setContent(m.getContent());
            view.setCreateUserId(m.getCreateUserId());
            view.setTargetUserId(m.getTargetUserId());
            view.setCreateUserName(
                    userDirectoryMapper.findById(m.getCreateUserId()) != null
                            ? userDirectoryMapper.findById(m.getCreateUserId()).getUserName()
                            : null);
            view.setTargetUserName(
                    userDirectoryMapper.findById(m.getTargetUserId()) != null
                            ? userDirectoryMapper.findById(m.getTargetUserId()).getUserName()
                            : null);
            view.setCreateTime(m.getCreateTime());
            view.setIsValid(m.getIsValid());
            return view;
        }).collect(Collectors.toList());
        return ApiResponse.success(list);
    }

    public ApiResponse<List<MessageDtos.MessageView>> listConversation(String userId, String targetUserId) {
        List<Message> messages = messageMapper.listConversation(userId, targetUserId);
        List<MessageDtos.MessageView> list = messages.stream().map(m -> {
            MessageDtos.MessageView view = new MessageDtos.MessageView();
            view.setMessageId(m.getMessageId());
            view.setTitle(m.getTitle());
            view.setContent(m.getContent());
            view.setCreateUserId(m.getCreateUserId());
            view.setTargetUserId(m.getTargetUserId());
            view.setCreateUserName(
                    userDirectoryMapper.findById(m.getCreateUserId()) != null
                            ? userDirectoryMapper.findById(m.getCreateUserId()).getUserName()
                            : null);
            view.setTargetUserName(
                    userDirectoryMapper.findById(m.getTargetUserId()) != null
                            ? userDirectoryMapper.findById(m.getTargetUserId()).getUserName()
                            : null);
            view.setCreateTime(m.getCreateTime());
            view.setIsValid(m.getIsValid());
            return view;
        }).collect(Collectors.toList());
        return ApiResponse.success(list);
    }

    public ApiResponse<List<MessageDtos.ConversationSummary>> listConversationSummary(String userId) {
        List<MessageDtos.ConversationSummary> list = messageMapper.listConversationSummary(userId);
        return ApiResponse.success(list);
    }

    public ApiResponse<?> invalidateMessage(String messageId) {
        messageMapper.invalidate(messageId);
        return ApiResponse.success("已作废", null);
    }
}
