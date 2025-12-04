package com.medical.backend.mapper;

import com.medical.backend.model.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    @Select("""
            SELECT department_id, department_name, introduction, location
            FROM department
            WHERE (#{keyword,jdbcType=VARCHAR} IS NULL
                   OR department_name ILIKE '%' || #{keyword,jdbcType=VARCHAR} || '%')
            ORDER BY department_id
            """)
    List<Department> findByKeyword(String keyword);
}
