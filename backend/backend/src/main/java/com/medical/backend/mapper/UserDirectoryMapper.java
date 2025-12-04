package com.medical.backend.mapper;

import com.medical.backend.model.UserDirectory;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDirectoryMapper {

    @Select("SELECT user_id, user_name, phone, password, create_time, is_active FROM user_directory WHERE phone = #{phone}")
    UserDirectory findByPhone(String phone);

    @Select("SELECT user_id, user_name, phone, password, create_time, is_active FROM user_directory WHERE user_id = #{userId}")
    UserDirectory findById(String userId);

    @Select("SELECT COUNT(1) FROM user_directory WHERE phone = #{phone}")
    int countByPhone(String phone);

    @Insert("""
            INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
            VALUES (#{userId}, #{userName}, #{phone}, #{password}, #{createTime}, #{isActive})
            """)
    int insert(UserDirectory user);

    @Update("UPDATE user_directory SET password = #{newPassword} WHERE user_id = #{userId}")
    int updatePassword(@Param("userId") String userId, @Param("newPassword") String newPassword);

    @Update("UPDATE user_directory SET is_active = #{active} WHERE user_id = #{userId}")
    int updateActive(@Param("userId") String userId, @Param("active") boolean active);

    @Select("""
            SELECT CASE
                       WHEN EXISTS (SELECT 1 FROM patient p WHERE p.user_id = #{userId}) THEN 'patient'
                       WHEN EXISTS (SELECT 1 FROM doctor d WHERE d.user_id = #{userId}) THEN 'doctor'
                       WHEN EXISTS (SELECT 1 FROM department_manager dm WHERE dm.user_id = #{userId}) THEN 'dept_manager'
                       WHEN EXISTS (SELECT 1 FROM "system_user" su WHERE su.user_id = #{userId}) THEN 'admin'
                       ELSE 'unknown'
                   END
            """)
    String findRoleByUserId(String userId);
}
