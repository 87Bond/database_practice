package com.medical.backend.mapper;

import com.medical.backend.model.Patient;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PatientMapper {

    @Insert("""
            INSERT INTO patient (user_id, gender, age, id_card, money)
            VALUES (#{userId}, #{gender}, #{age}, #{idCard}, #{money})
            """)
    int insert(Patient patient);

    @Select("""
            SELECT user_id, gender, age, id_card, money
            FROM patient
            WHERE user_id = #{userId}
            """)
    Patient findById(String userId);

    @Update("""
            UPDATE patient
            SET money = #{money}
            WHERE user_id = #{userId}
            """)
    int updateMoney(@Param("userId") String userId, @Param("money") int money);
}
