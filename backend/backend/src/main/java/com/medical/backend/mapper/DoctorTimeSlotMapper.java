package com.medical.backend.mapper;

import com.medical.backend.model.DoctorTimeSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DoctorTimeSlotMapper {

    @Select("""
            SELECT slot_id, doctor_id, department_id, slot_date, time_slot, start_time, end_time,
                   capacity, booked_count, status, note, create_time, update_time
            FROM doctor_time_slot
            WHERE slot_date = #{date}
              AND department_id = #{departmentId}
              AND (#{doctorId} IS NULL OR doctor_id = #{doctorId})
            """)
    List<DoctorTimeSlot> findSlots(@Param("date") LocalDate date,
                                   @Param("departmentId") String departmentId,
                                   @Param("doctorId") String doctorId);

    @Select("""
            SELECT slot_id, doctor_id, department_id, slot_date, time_slot, start_time, end_time,
                   capacity, booked_count, status, note, create_time, update_time
            FROM doctor_time_slot
            WHERE doctor_id = #{doctorId}
              AND slot_date = #{date}
            """)
    List<DoctorTimeSlot> findByDoctorAndDate(@Param("doctorId") String doctorId,
                                             @Param("date") LocalDate date);

    @Update("""
            UPDATE doctor_time_slot
            SET booked_count = booked_count + #{delta}
            WHERE slot_id = #{slotId}
            """)
    int changeBookedCount(@Param("slotId") String slotId, @Param("delta") int delta);
}

