package com.medical.backend.service;

import com.medical.backend.common.ApiResponse;
import com.medical.backend.common.ErrorCodes;
import com.medical.backend.dto.MedicalRecordDtos;
import com.medical.backend.mapper.MedicalRecordMapper;
import com.medical.backend.mapper.RegistrationMapper;
import com.medical.backend.model.MedicalRecord;
import com.medical.backend.model.Registration;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordMapper medicalRecordMapper;
    private final RegistrationMapper registrationMapper;

    public MedicalRecordService(MedicalRecordMapper medicalRecordMapper,
                                RegistrationMapper registrationMapper) {
        this.medicalRecordMapper = medicalRecordMapper;
        this.registrationMapper = registrationMapper;
    }

    public ApiResponse<List<MedicalRecordDtos.PatientRecordView>> listForPatient(String patientId) {
        List<MedicalRecordDtos.PatientRecordView> list =
                medicalRecordMapper.findByPatientId(patientId);
        return ApiResponse.success(list);
    }

    public ApiResponse<List<MedicalRecordDtos.PatientRecordView>> listForDoctor(String doctorId) {
        List<MedicalRecordDtos.PatientRecordView> list =
                medicalRecordMapper.findByDoctorId(doctorId);
        return ApiResponse.success(list);
    }

    public ApiResponse<?> createRecord(String doctorId, MedicalRecordDtos.CreateRecordRequest req) {
        Registration reg = registrationMapper.findById(req.getRegId());
        if (reg == null) {
            return ApiResponse.error(ErrorCodes.PARAM_ERROR, "挂号记录不存在");
        }
        if (!doctorId.equals(reg.getDoctorId())) {
            return ApiResponse.error(ErrorCodes.NO_PERMISSION, "仅该挂号的医生可录入诊疗记录");
        }
        MedicalRecord record = new MedicalRecord();
        record.setRecordId(IdGenerator.newMessageId().replaceFirst("M", "MR"));
        record.setRegId(req.getRegId());
        record.setDiagnosis(req.getDiagnosis());
        record.setTreatmentPlan(req.getTreatmentPlan());
        record.setAdvice(req.getAdvice());
        record.setCreateTime(LocalDateTime.now());
        medicalRecordMapper.insert(record);
        return ApiResponse.success("诊疗记录已保存", null);
    }
}
