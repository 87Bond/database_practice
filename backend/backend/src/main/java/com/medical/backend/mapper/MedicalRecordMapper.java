package com.medical.backend.mapper;

import com.medical.backend.dto.MedicalRecordDtos;
import com.medical.backend.model.MedicalRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MedicalRecordMapper {

    @Insert("""
            INSERT INTO medical_record (record_id, reg_id, diagnosis, treatment_plan, advice, create_time)
            VALUES (#{recordId}, #{regId}, #{diagnosis}, #{treatmentPlan}, #{advice}, #{createTime})
            """)
    int insert(MedicalRecord record);

    @Select("""
            SELECT mr.record_id      AS recordId,
                   mr.reg_id         AS regId,
                   r.doctor_id       AS doctorId,
                    d.doctor_name     AS doctorName,
                   r.patient_id      AS patientId,
                   u.user_name       AS patientName,
                   r.department_id   AS departmentId,
                   dept.department_name AS departmentName,
                   dept.location     AS departmentLocation,
                   r.reg_date        AS regDate,
                   r.reg_time_slot   AS regTimeSlot,
                   mr.diagnosis      AS diagnosis,
                   mr.treatment_plan AS treatmentPlan,
                   mr.advice         AS advice,
                    mr.create_time    AS createTime
            FROM medical_record mr
                     JOIN registration r ON mr.reg_id = r.reg_id
                     JOIN doctor d ON r.doctor_id = d.user_id
                     JOIN user_directory u ON r.patient_id = u.user_id
                     LEFT JOIN department dept ON r.department_id = dept.department_id
            WHERE r.patient_id = #{patientId}
            ORDER BY mr.create_time DESC
            """)
    List<MedicalRecordDtos.PatientRecordView> findByPatientId(@Param("patientId") String patientId);

    @Select("""
            SELECT mr.record_id      AS recordId,
                   mr.reg_id         AS regId,
                   r.doctor_id       AS doctorId,
                   d.doctor_name     AS doctorName,
                   r.patient_id      AS patientId,
                   u.user_name       AS patientName,
                   r.department_id   AS departmentId,
                   dept.department_name AS departmentName,
                   dept.location     AS departmentLocation,
                   r.reg_date        AS regDate,
                   r.reg_time_slot   AS regTimeSlot,
                   mr.diagnosis      AS diagnosis,
                   mr.treatment_plan AS treatmentPlan,
                   mr.advice         AS advice,
                   mr.create_time    AS createTime
            FROM medical_record mr
                     JOIN registration r ON mr.reg_id = r.reg_id
                     JOIN doctor d ON r.doctor_id = d.user_id
                     JOIN user_directory u ON r.patient_id = u.user_id
                     LEFT JOIN department dept ON r.department_id = dept.department_id
            WHERE r.doctor_id = #{doctorId}
            ORDER BY mr.create_time DESC
            """)
    List<MedicalRecordDtos.PatientRecordView> findByDoctorId(@Param("doctorId") String doctorId);
}
