package com.medical.backend.mapper;

import com.medical.backend.model.Doctor;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorMapper {

    @Select("""
            SELECT user_id, doctor_name, department_id, title, specialty, work_phone
            FROM doctor
            WHERE (#{departmentId} IS NULL OR department_id = #{departmentId})
            """)
    List<Doctor> findByDepartment(String departmentId);

    @Insert("""
            INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
            VALUES (#{userId}, #{doctorName}, #{departmentId}, #{title}, #{specialty}, #{workPhone})
            """)
    int insert(Doctor doctor);

    @Select("""
            SELECT user_id, doctor_name, department_id, title, specialty, work_phone
            FROM doctor
            WHERE user_id = #{userId}
            """)
    Doctor findById(String userId);
}
