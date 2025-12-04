package com.medical.backend.mapper;

import com.medical.backend.model.DepartmentManager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DepartmentManagerMapper {

    @Insert("""
            INSERT INTO department_manager (user_id, department_id, work_phone)
            VALUES (#{userId}, #{departmentId}, #{workPhone})
            """)
    int insert(DepartmentManager manager);

    @Select("""
            SELECT user_id, department_id, work_phone
            FROM department_manager
            WHERE user_id = #{userId}
            """)
    DepartmentManager findByUserId(String userId);
}
