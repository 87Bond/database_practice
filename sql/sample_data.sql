-- Sample initial data for quick testing
-- 尽量覆盖所有表：department / user_directory / patient / doctor /
-- department_manager / system_user / doctor_time_slot /
-- registration / medical_record / message / audit_log

-- =========================
-- 1. Departments
-- =========================

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D001', '内科', '内科门诊', '门诊楼3F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D002', '外科', '外科门诊', '门诊楼4F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D003', '儿科', '儿童门诊', '门诊楼2F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D004', '妇产科', '妇产科门诊', '门诊楼5F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D005', '眼科', '眼科门诊', '门诊楼6F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D006', '耳鼻喉科', '耳鼻喉门诊', '门诊楼6F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D007', '皮肤科', '皮肤病门诊', '门诊楼7F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D008', '口腔科', '口腔门诊', '门诊楼2F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D009', '骨科', '骨科门诊', '门诊楼4F')
ON CONFLICT (department_id) DO NOTHING;

INSERT INTO department (department_id, department_name, introduction, location)
VALUES ('D010', '心内科', '心血管内科门诊', '门诊楼3F')
ON CONFLICT (department_id) DO NOTHING;

-- =========================
-- 2. Users (UserDirectory)
-- =========================

-- 10 个患者
INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0001', '张三', '13800000002', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0002', '李四', '13800000003', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0003', '王五', '13800000006', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0004', '赵六', '13800000007', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0005', '孙七', '13800000008', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0006', '周八', '13800000009', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0007', '吴九', '13800000010', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0008', '郑十', '13800000011', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0009', '钱一', '13800000012', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('P0010', '冯二', '13800000013', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

-- 10 个医生
INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0001', '李医生', '13800000001', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0002', '王医生', '13800000004', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0003', '赵医生', '13800000014', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0004', '钱医生', '13800000015', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0005', '孙医生', '13800000016', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0006', '周医生', '13800000017', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0007', '吴医生', '13800000018', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0008', '郑医生', '13800000019', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0009', '王主任', '13800000020', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DR0010', '李主任', '13800000021', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

-- 10 个科室管理员
INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0001', '内科主任', '13800000005', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0002', '外科主任', '13800000022', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0003', '儿科主任', '13800000023', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0004', '妇产科主任', '13800000024', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0005', '眼科主任', '13800000025', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0006', '耳鼻喉主任', '13800000026', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0007', '皮肤科主任', '13800000027', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0008', '口腔科主任', '13800000028', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0009', '骨科主任', '13800000029', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('DM0010', '心内科主任', '13800000030', '123456', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

-- 10 个系统管理员
INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0001', '系统管理员', '13800000099', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0002', '系统管理员2', '13800000100', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0003', '系统管理员3', '13800000101', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0004', '系统管理员4', '13800000102', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0005', '系统管理员5', '13800000103', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0006', '系统管理员6', '13800000104', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0007', '系统管理员7', '13800000105', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0008', '系统管理员8', '13800000106', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0009', '系统管理员9', '13800000107', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO user_directory (user_id, user_name, phone, password, create_time, is_active)
VALUES ('AD0010', '系统管理员10', '13800000108', 'admin123', NOW(), TRUE)
ON CONFLICT (user_id) DO NOTHING;

-- =========================
-- 3. Patients
-- =========================

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0001', '男', 30, '110101199001010011', 10000)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0002', '女', 28, '110101199301012222', 8000)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0003', '男', 25, '110101199501013333', 6000)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0004', '女', 35, '110101198501014444', 9000)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0005', '男', 40, '110101198001015555', 12000)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0006', '女', 32, '110101198901016666', 7000)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0007', '男', 22, '110101199801017777', 5000)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0008', '女', 29, '110101199201018888', 6500)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0009', '男', 27, '110101199301019999', 6100)
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO patient (user_id, gender, age, id_card, money)
VALUES ('P0010', '女', 31, '110101198901011010', 8900)
ON CONFLICT (user_id) DO NOTHING;

-- =========================
-- 4. Doctors
-- =========================

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0001', '李医生', 'D001', '主任医师', '呼吸内科', '010-88886666')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0002', '王医生', 'D002', '副主任医师', '普通外科', '010-88887777')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0003', '赵医生', 'D003', '主治医师', '小儿呼吸', '010-88883333')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0004', '钱医生', 'D004', '主治医师', '高危妊娠', '010-88882222')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0005', '孙医生', 'D005', '副主任医师', '白内障', '010-88881111')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0006', '周医生', 'D006', '主任医师', '鼻炎、中耳炎', '010-88880000')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0007', '吴医生', 'D007', '主治医师', '湿疹、荨麻疹', '010-87776666')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0008', '郑医生', 'D008', '主治医师', '龋齿治疗', '010-86665555')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0009', '王主任', 'D009', '主任医师', '骨折、关节置换', '010-85554444')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO doctor (user_id, doctor_name, department_id, title, specialty, work_phone)
VALUES ('DR0010', '李主任', 'D010', '主任医师', '心律失常', '010-84443333')
ON CONFLICT (user_id) DO NOTHING;

-- =========================
-- 5. Department Manager
-- =========================

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0001', 'D001', '010-66668888')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0002', 'D002', '010-66668889')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0003', 'D003', '010-66668890')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0004', 'D004', '010-66668891')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0005', 'D005', '010-66668892')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0006', 'D006', '010-66668893')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0007', 'D007', '010-66668894')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0008', 'D008', '010-66668895')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0009', 'D009', '010-66668896')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO department_manager (user_id, department_id, work_phone)
VALUES ('DM0010', 'D010', '010-66668897')
ON CONFLICT (user_id) DO NOTHING;

-- =========================
-- 6. System User (管理员)
-- =========================

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0001', '010-99998888')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0002', '010-99998889')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0003', '010-99998890')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0004', '010-99998891')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0005', '010-99998892')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0006', '010-99998893')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0007', '010-99998894')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0008', '010-99998895')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0009', '010-99998896')
ON CONFLICT (user_id) DO NOTHING;

INSERT INTO "system_user" (user_id, work_phone)
VALUES ('AD0010', '010-99998897')
ON CONFLICT (user_id) DO NOTHING;

-- =========================
-- 7. DoctorTimeSlot（号源）
-- =========================

-- DR0001 明天的三个号源时段（内科）
INSERT INTO doctor_time_slot (slot_id, doctor_id, department_id, slot_date, time_slot,
                              start_time, end_time, capacity, booked_count, status, note)
VALUES ('SLOT001', 'DR0001', 'D001', CURRENT_DATE + 1, 'AM-1',
        TIME '09:00', TIME '10:00', 5, 0, 'OPEN', '上午第一段'),
       ('SLOT002', 'DR0001', 'D001', CURRENT_DATE + 1, 'AM-2',
        TIME '10:00', TIME '11:00', 5, 0, 'OPEN', '上午第二段'),
       ('SLOT003', 'DR0001', 'D001', CURRENT_DATE + 1, 'PM-1',
        TIME '14:00', TIME '15:00', 5, 0, 'OPEN', '下午第一段')
ON CONFLICT (slot_id) DO NOTHING;

-- DR0002 后天的两个号源时段（外科）
INSERT INTO doctor_time_slot (slot_id, doctor_id, department_id, slot_date, time_slot,
                              start_time, end_time, capacity, booked_count, status, note)
VALUES ('SLOT004', 'DR0002', 'D002', CURRENT_DATE + 2, 'AM-1',
        TIME '09:00', TIME '10:00', 3, 0, 'OPEN', '外科上午一段'),
       ('SLOT005', 'DR0002', 'D002', CURRENT_DATE + 2, 'PM-1',
        TIME '14:00', TIME '15:00', 4, 0, 'OPEN', '外科下午一段')
ON CONFLICT (slot_id) DO NOTHING;

-- 额外号源，保证数量更多
INSERT INTO doctor_time_slot (slot_id, doctor_id, department_id, slot_date, time_slot,
                              start_time, end_time, capacity, booked_count, status, note)
VALUES ('SLOT006', 'DR0003', 'D003', CURRENT_DATE + 1, 'AM-1',
        TIME '09:00', TIME '10:00', 6, 0, 'OPEN', '儿科上午一段'),
       ('SLOT007', 'DR0004', 'D004', CURRENT_DATE + 1, 'AM-1',
        TIME '09:00', TIME '10:00', 6, 0, 'OPEN', '妇产科上午一段'),
       ('SLOT008', 'DR0005', 'D005', CURRENT_DATE + 3, 'AM-1',
        TIME '09:00', TIME '10:00', 4, 0, 'OPEN', '眼科上午一段'),
       ('SLOT009', 'DR0006', 'D006', CURRENT_DATE + 3, 'PM-1',
        TIME '14:00', TIME '15:00', 4, 0, 'OPEN', '耳鼻喉下午一段'),
       ('SLOT010', 'DR0007', 'D007', CURRENT_DATE + 4, 'AM-1',
        TIME '09:00', TIME '10:00', 5, 0, 'OPEN', '皮肤科上午一段')
ON CONFLICT (slot_id) DO NOTHING;

-- =========================
-- 8. Registration（挂号记录）
-- =========================

-- 张三（P0001）明天挂 DR0001 上午一段，已支付
INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512050001', 'P0001', 'DR0001', 'D001',
        CURRENT_DATE + 1, 'AM-1', 'Booked',
        3000, 'Paid', NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day', 'SLOT001')
ON CONFLICT (reg_id) DO NOTHING;

-- 李四（P0002）后天挂 DR0002 下午一段，未支付
INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512060001', 'P0002', 'DR0002', 'D002',
        CURRENT_DATE + 2, 'PM-1', 'Booked',
        2500, 'Unpaid', NOW(), NULL, 'SLOT005')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512050002', 'P0003', 'DR0001', 'D001',
        CURRENT_DATE + 1, 'AM-2', 'Booked',
        3000, 'Unpaid', NOW(), NULL, 'SLOT002')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512050003', 'P0004', 'DR0001', 'D001',
        CURRENT_DATE + 1, 'PM-1', 'Booked',
        3000, 'Paid', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '1 hours', 'SLOT003')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512060002', 'P0005', 'DR0002', 'D002',
        CURRENT_DATE + 2, 'AM-1', 'Booked',
        2500, 'Paid', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '1 hours', 'SLOT004')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512060003', 'P0006', 'DR0002', 'D002',
        CURRENT_DATE + 2, 'PM-1', 'Canceled',
        2500, 'Refunded', NOW() - INTERVAL '3 hours', NOW() - INTERVAL '2 hours', 'SLOT005')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512050004', 'P0007', 'DR0003', 'D003',
        CURRENT_DATE + 1, 'AM-1', 'Booked',
        2000, 'Paid', NOW(), NOW(), 'SLOT006')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512050005', 'P0008', 'DR0004', 'D004',
        CURRENT_DATE + 1, 'AM-1', 'Booked',
        2600, 'Unpaid', NOW(), NULL, 'SLOT007')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512070001', 'P0009', 'DR0005', 'D005',
        CURRENT_DATE + 3, 'AM-1', 'Booked',
        2800, 'Unpaid', NOW(), NULL, 'SLOT008')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512070002', 'P0010', 'DR0006', 'D006',
        CURRENT_DATE + 3, 'PM-1', 'Booked',
        2800, 'Paid', NOW(), NOW(), 'SLOT009')
ON CONFLICT (reg_id) DO NOTHING;

INSERT INTO registration (reg_id, patient_id, doctor_id, department_id,
                          reg_date, reg_time_slot, reg_status,
                          reg_fee, pay_status, create_time, paid_at, slot_id)
VALUES ('R202512080001', 'P0001', 'DR0007', 'D007',
        CURRENT_DATE + 4, 'AM-1', 'Booked',
        2400, 'Unpaid', NOW(), NULL, 'SLOT010')
ON CONFLICT (reg_id) DO NOTHING;

-- =========================
-- 9. MedicalRecord（诊疗记录）
-- =========================

INSERT INTO medical_record (record_id, reg_id, diagnosis, treatment_plan, advice, create_time)
VALUES ('MR202512050001', 'R202512050001',
        '上呼吸道感染', '口服消炎药 3 天', '注意休息，多喝水', NOW())
ON CONFLICT (record_id) DO NOTHING;

INSERT INTO medical_record (record_id, reg_id, diagnosis, treatment_plan, advice, create_time)
VALUES ('MR202512050002', 'R202512050004',
        '小儿发热', '物理降温 + 对症处理', '多喝水，随访', NOW())
ON CONFLICT (record_id) DO NOTHING;

INSERT INTO medical_record (record_id, reg_id, diagnosis, treatment_plan, advice, create_time)
VALUES ('MR202512060001', 'R202512060002',
        '胆囊结石', '择期手术', '注意饮食清淡', NOW())
ON CONFLICT (record_id) DO NOTHING;

-- =========================
-- 10. Message（消息）
-- =========================

-- DR0001 发给 P0001 的就诊提醒
INSERT INTO message (message_id, title, content,
                     create_user_id, target_user_id, create_time, is_valid)
VALUES ('M202512040001', '就诊提醒',
        '您已预约明天上午 9 点内科门诊，请提前 30 分钟到达医院。',
        'DR0001', 'P0001', NOW() - INTERVAL '1 day', TRUE)
ON CONFLICT (message_id) DO NOTHING;

-- 管理员发给 P0002 的系统通知
INSERT INTO message (message_id, title, content,
                     create_user_id, target_user_id, create_time, is_valid)
VALUES ('M202512040002', '系统维护通知',
        '本系统将于本周日 23:00-24:00 进行维护，期间暂停挂号服务。',
        'AD0001', 'P0002', NOW() - INTERVAL '2 days', TRUE)
ON CONFLICT (message_id) DO NOTHING;

INSERT INTO message (message_id, title, content,
                     create_user_id, target_user_id, create_time, is_valid)
VALUES ('M202512050003', '复诊提醒',
        '您的复诊时间为下周一上午，请合理安排时间。',
        'DR0001', 'P0003', NOW(), TRUE)
ON CONFLICT (message_id) DO NOTHING;

INSERT INTO message (message_id, title, content,
                     create_user_id, target_user_id, create_time, is_valid)
VALUES ('M202512050004', '检查结果通知',
        '您的检查结果已出，请登录系统查看或到窗口咨询。',
        'DR0002', 'P0004', NOW(), TRUE)
ON CONFLICT (message_id) DO NOTHING;

-- =========================
-- 11. AuditLog（操作日志）
-- =========================

INSERT INTO audit_log (log_id, user_id, action, target, time)
VALUES ('L202512040001', 'P0001', 'CREATE_REGISTRATION', 'R202512050001', NOW() - INTERVAL '1 day')
ON CONFLICT (log_id) DO NOTHING;

INSERT INTO audit_log (log_id, user_id, action, target, time)
VALUES ('L202512040002', 'DR0001', 'CREATE_MEDICAL_RECORD', 'MR202512050001', NOW() - INTERVAL '1 day')
ON CONFLICT (log_id) DO NOTHING;

INSERT INTO audit_log (log_id, user_id, action, target, time)
VALUES ('L202512050003', 'DR0001', 'SEND_MESSAGE', 'M202512050003', NOW())
ON CONFLICT (log_id) DO NOTHING;

INSERT INTO audit_log (log_id, user_id, action, target, time)
VALUES ('L202512050004', 'AD0001', 'FREEZE_USER', 'P0006', NOW())
ON CONFLICT (log_id) DO NOTHING;
