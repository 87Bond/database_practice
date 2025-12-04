package com.medical.backend.mapper;

import com.medical.backend.model.SystemUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemUserMapper {

    @Insert("""
            INSERT INTO "system_user" (user_id, work_phone)
            VALUES (#{userId}, #{workPhone})
            """)
    int insert(SystemUser systemUser);

    @Select("""
            SELECT user_id, work_phone
            FROM "system_user"
            WHERE user_id = #{userId}
            """)
    SystemUser findByUserId(String userId);
}
