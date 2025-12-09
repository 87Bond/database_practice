package com.medical.backend.mapper;

import com.medical.backend.dto.DoctorPortalDtos;
import com.medical.backend.model.Registration;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RegistrationMapper {

    @Insert("""
            INSERT INTO registration (reg_id, patient_id, doctor_id, department_id, reg_date, reg_time_slot,
                                      reg_status, reg_fee, pay_status, create_time, paid_at, slot_id)
            VALUES (#{regId}, #{patientId}, #{doctorId}, #{departmentId}, #{regDate}, #{regTimeSlot},
                    #{regStatus}, #{regFee}, #{payStatus}, #{createTime}, #{paidAt}, #{slotId})
            """)
    int insert(Registration registration);

    @Select("""
            SELECT reg_id, patient_id, doctor_id, department_id, reg_date, reg_time_slot,
                   reg_status, reg_fee, pay_status, create_time, paid_at, slot_id
            FROM registration
            WHERE reg_id = #{regId}
            """)
    Registration findById(String regId);

    @Update("""
            UPDATE registration
            SET reg_status = #{regStatus}, pay_status = #{payStatus}, paid_at = #{paidAt}
            WHERE reg_id = #{regId}
            """)
    int updateStatus(@Param("regId") String regId,
                     @Param("regStatus") String regStatus,
                     @Param("payStatus") String payStatus,
                     @Param("paidAt") java.time.LocalDateTime paidAt);

    @Select("""
            SELECT r.reg_id, r.patient_id, r.doctor_id, r.department_id, r.reg_date, r.reg_time_slot,
                   r.reg_status, r.reg_fee, r.pay_status, r.create_time, r.paid_at, r.slot_id,
                   d.doctor_name,
                   dept.department_name,
                   dept.location AS department_location
            FROM registration r
                     LEFT JOIN doctor d ON r.doctor_id = d.user_id
                     LEFT JOIN department dept ON r.department_id = dept.department_id
            WHERE r.patient_id = #{patientId}
              AND (#{from,jdbcType=DATE} IS NULL OR r.reg_date >= #{from,jdbcType=DATE})
              AND (#{to,jdbcType=DATE} IS NULL OR r.reg_date <= #{to,jdbcType=DATE})
              AND (#{departmentId,jdbcType=VARCHAR} IS NULL OR r.department_id = #{departmentId,jdbcType=VARCHAR})
              AND (#{status,jdbcType=VARCHAR} IS NULL OR r.reg_status = #{status,jdbcType=VARCHAR})
            ORDER BY r.reg_date DESC, r.create_time DESC
            """)
    List<Registration> findByConditions(@Param("patientId") String patientId,
                                        @Param("from") LocalDate from,
                                        @Param("to") LocalDate to,
                                        @Param("departmentId") String departmentId,
                                        @Param("status") String status);

    @Select("""
            SELECT r.reg_id       AS regId,
                   r.patient_id   AS patientId,
                   u.user_name    AS patientName,
                   r.reg_date     AS regDate,
                   r.reg_time_slot AS regTimeSlot,
                   r.reg_status   AS regStatus,
                   r.pay_status   AS payStatus
            FROM registration r
                     JOIN user_directory u ON r.patient_id = u.user_id
            WHERE r.doctor_id = #{doctorId}
              AND (#{date,jdbcType=DATE} IS NULL OR r.reg_date = #{date,jdbcType=DATE})
            ORDER BY r.reg_date, r.reg_time_slot
            """)
    java.util.List<DoctorPortalDtos.DoctorRegistrationView> findDoctorRegistrations(
            @Param("doctorId") String doctorId,
            @Param("date") LocalDate date);
    
    @Select("""
            SELECT r.reg_id       AS regId,
                   r.patient_id   AS patientId,
                   u.user_name    AS patientName,
                   r.doctor_id    AS doctorId,
                   r.reg_date     AS regDate,
                   r.reg_time_slot AS regTimeSlot,
                   r.reg_status   AS regStatus,
                   r.pay_status   AS payStatus
            FROM registration r
                     JOIN user_directory u ON r.patient_id = u.user_id
            WHERE r.department_id = #{departmentId}
              AND (#{date,jdbcType=DATE} IS NULL OR r.reg_date = #{date,jdbcType=DATE})
            ORDER BY r.reg_date, r.reg_time_slot
            """)
    java.util.List<DoctorPortalDtos.DoctorRegistrationView> findDoctorRegistrationsForDepartment(
            @Param("departmentId") String departmentId,
            @Param("date") LocalDate date);

    @Select("""
            SELECT COUNT(1)
            FROM registration
            WHERE patient_id = #{patientId}
              AND doctor_id = #{doctorId}
            """)
    int countByPatientAndDoctor(@Param("patientId") String patientId,
                                @Param("doctorId") String doctorId);

    @Select("""
            SELECT COUNT(1)
            FROM registration
            WHERE patient_id = #{patientId}
             AND doctor_id = #{doctorId}
            """)
    int countByDoctorAndPatient(@Param("doctorId") String doctorId,
                                @Param("patientId") String patientId);

    @Select("""
            SELECT COUNT(1)
            FROM registration
            WHERE patient_id = #{patientId}
              AND slot_id = #{slotId}
              AND reg_status <> 'Canceled'
            """)
    int countByPatientAndSlot(@Param("patientId") String patientId,
                              @Param("slotId") String slotId);

    @Select("""
            SELECT COUNT(1)
            FROM registration
            WHERE patient_id = #{patientId}
              AND doctor_id = #{doctorId}
              AND pay_status = 'Paid'
              AND reg_date >= #{cutoff,jdbcType=DATE}
            """)
    int countPaidAfterByPatientAndDoctor(@Param("patientId") String patientId,
                                         @Param("doctorId") String doctorId,
                                         @Param("cutoff") LocalDate cutoff);

    @Select("""
            SELECT COUNT(1)
            FROM registration
            WHERE patient_id = #{patientId}
              AND doctor_id = #{doctorId}
              AND pay_status = 'Paid'
              AND reg_date >= #{cutoff,jdbcType=DATE}
            """)
    int countPaidAfterByDoctorAndPatient(@Param("doctorId") String doctorId,
                                         @Param("patientId") String patientId,
                                         @Param("cutoff") LocalDate cutoff);

    @Select("""
            SELECT COUNT(1)
            FROM registration
            WHERE patient_id = #{patientId}
              AND department_id = #{departmentId}
              AND pay_status = 'Paid'
              AND reg_date >= #{cutoff,jdbcType=DATE}
            """)
    int countPaidAfterByPatientAndDepartment(@Param("patientId") String patientId,
                                             @Param("departmentId") String departmentId,
                                             @Param("cutoff") LocalDate cutoff);
}
