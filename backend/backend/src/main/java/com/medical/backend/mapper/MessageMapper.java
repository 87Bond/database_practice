package com.medical.backend.mapper;

import com.medical.backend.dto.MessageDtos;
import com.medical.backend.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("""
            INSERT INTO message (message_id, title, content, create_user_id, target_user_id, create_time, is_valid)
            VALUES (#{messageId}, #{title}, #{content}, #{createUserId}, #{targetUserId}, #{createTime}, #{isValid})
            """)
    int insert(Message message);

    @Select("""
            SELECT create_time
            FROM message
            WHERE (create_user_id = #{a} AND target_user_id = #{b})
               OR (create_user_id = #{b} AND target_user_id = #{a})
            ORDER BY create_time DESC
            LIMIT 1
            """)
    java.time.LocalDateTime findLastMessageTime(@Param("a") String a, @Param("b") String b);

    @Select("""
            SELECT message_id, title, content, create_user_id, target_user_id, create_time, is_valid
            FROM message
            WHERE (#{inbox} AND target_user_id = #{userId}
                   OR NOT #{inbox} AND create_user_id = #{userId})
              AND (#{isValid,jdbcType=BOOLEAN} IS NULL OR is_valid = #{isValid,jdbcType=BOOLEAN})
            ORDER BY create_time DESC
            """)
    List<Message> listMessages(@Param("userId") String userId,
                               @Param("inbox") boolean inbox,
                               @Param("isValid") Boolean isValid);

    @Select("""
            SELECT message_id, title, content, create_user_id, target_user_id, create_time, is_valid
            FROM message
            WHERE (create_user_id = #{userId} AND target_user_id = #{targetUserId})
               OR (create_user_id = #{targetUserId} AND target_user_id = #{userId})
            ORDER BY create_time
            """)
    List<Message> listConversation(@Param("userId") String userId,
                                   @Param("targetUserId") String targetUserId);

    /**
     * 会话列表：每个联系人只保留最近一条消息，用于 QQ 式列表
     */
    @Select("""
            SELECT t.contact_user_id   AS contactUserId,
                   u.user_name         AS contactUserName,
                   t.title             AS lastTitle,
                   t.content           AS lastContent,
                   t.create_time       AS lastTime
            FROM (
                     SELECT CASE
                                WHEN create_user_id = #{userId} THEN target_user_id
                                ELSE create_user_id
                            END                               AS contact_user_id,
                            title,
                            content,
                            create_time,
                            ROW_NUMBER() OVER (
                                PARTITION BY CASE
                                                 WHEN create_user_id = #{userId} THEN target_user_id
                                                 ELSE create_user_id
                                             END
                                ORDER BY create_time DESC
                            )                                  AS rn
                     FROM message
                     WHERE create_user_id = #{userId}
                        OR target_user_id = #{userId}
                 ) t
                     JOIN user_directory u ON u.user_id = t.contact_user_id
            WHERE t.rn = 1
            ORDER BY t.create_time DESC
            """)
    List<MessageDtos.ConversationSummary> listConversationSummary(@Param("userId") String userId);

    @Update("""
            UPDATE message
            SET is_valid = FALSE
            WHERE message_id = #{messageId}
            """)
    int invalidate(String messageId);
}
