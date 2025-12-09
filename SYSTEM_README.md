# 医院门诊挂号系统说明文档（系统层面说明）

> 本文档侧重介绍**系统设计、角色权限、业务流程和数据模型**，  
> 环境配置与启动方式请参考当前目录下已有的 `README.md`。

---

## 1. 系统概述

本系统是一个基于 **Spring Boot + MyBatis + PostgreSQL + Vue2** 的简易医院门诊挂号系统，用于课程《数据库原理》实验。

系统实现了从**账号管理、科室和号源查询、在线挂号、就诊记录、消息通知到权限控制**的一整套基本流程，支持多角色协同：

- 患者（Patient）
- 医生（Doctor）
- 科室管理员（DepartmentManager）
- 系统管理员（SystemUser / Admin）

---

## 2. 用户角色与权限

### 2.1 患者 Patient

- 主要操作：
  - 注册 / 登录
  - 查询科室列表、查询医生及可预约时间段
  - 创建挂号、查看个人挂号记录
  - 修改密码
  - 查看系统消息（如挂号成功通知、就诊提醒、系统公告）
- 消息权限：
  - 仅允许向**曾经挂号就诊过的医生**发送消息（系统在发送消息时检查 `registration` 表）。

### 2.2 医生 Doctor

- 主要操作：
  - 在“医生工作台”查看某一天的挂号患者列表（包括患者 ID、姓名、挂号时间段、状态等）
  - 向患者发送就诊提醒、复诊通知等消息
- 消息权限：
  - 可以向**预约过自己号的患者**发送消息；
  - 可以向**本科室的科室管理员**发送消息；
  - 可以向**系统管理员**发送消息；
  - 不允许向其他无关用户发送消息。

### 2.3 科室管理员 DepartmentManager

- 角色定位：
  - 管理本科室医生排班（当前版本主要体现在数据模型中），
  - 接收医生发来的科室类消息。
- 本版本中科室管理员的页面较简单，主要通过消息和数据结构体现权限，可根据需要继续扩展。

### 2.4 系统管理员 SystemUser / Admin

- 主要操作：
  - 创建医生账号：写入 `user_directory` + `doctor`
  - 创建科室管理员账号：写入 `user_directory` + `department_manager`
  - 创建系统管理员账号：写入 `user_directory` + `"system_user"`
  - 冻结 / 解冻用户账号（患者 / 医生 / 科室管理员 / 管理员）：
    - 设置 `user_directory.is_active` 为 `false/true`
- 消息 & 公告：
  - 可以向任意用户发送消息（系统通知、维护公告等），所有人都能在消息页面看到针对自己的消息。
  - 若需要扩展成“系统公告表 Announcement（所有用户可见）”，可在现有基础上增加对应表和接口。

---

## 3. 主要业务模块

### 3.1 账号与鉴权模块

- 患者注册：`POST /api/auth/patient/register`
- 登录：`POST /api/auth/login`（患者 / 医生 / 科室管理员 / 管理员共用）
- 修改密码：`POST /api/auth/change-password`

登录成功后，后端返回：

- `userId`：用户唯一 ID（例如 `P2025...`、`DR0001`、`AD0001`）
- `username`：用户名
- `role`：`patient | doctor | dept_manager | admin`
- `token`：简化为 `userId`（前端每次请求通过 HTTP 头 `X-User-Id` 回传）

> 为了实验简化，没有引入完整的 JWT / Session 管理，但在接口层面实现了必要的角色判断和权限控制。

### 3.2 科室 & 医生 & 号源管理

- 查询科室列表：`GET /api/departments`
- 查询医生及可用时间段：`GET /api/doctors?departmentId=&date=YYYY-MM-DD`
- 查询号源：`GET /api/registrations/slots`（供扩展使用）

号源由表 `doctor_time_slot` 管理，核心字段：

- `doctor_id`、`department_id`
- `slot_date`（日期）+ `time_slot`（默认存成 “HH:mm-HH:mm”，不再强绑定 AM/PM 编码）
- `capacity`（总号源）+ `booked_count`（已预约数量）

当 `booked_count >= capacity` 时，该时间段视为满号，不再作为可预约时段返回给前端。

#### 3.2.1 时间槽与起止时间

新版号源使用 `slot_id` 结合 `start_time` / `end_time`，`time_slot` 仅作为展示标签（默认保存为 “HH:mm-HH:mm”）。前端展示直接使用起止时间，提交挂号时传 `slotId`，后端会把挂号记录中的 `reg_time_slot` 存为 “HH:mm-HH:mm” 便于历史查看。

### 3.3 挂号流程

- 创建挂号：`POST /api/registrations`
  - 参数：`doctorId`, `departmentId`, `regDate`, `slotId`（可选传 `regTimeSlot` 作为展示标签）
  - 步骤：
    1. 校验医生是否属于该科室；
    2. 在 `doctor_time_slot` 中查找指定 `slotId` 的号源并校验日期/医生/科室；
    3. 检查剩余号源（`capacity > booked_count`），不足则报错 `号源不足`；
    4. 生成挂号记录 `registration`，初始状态：
       - `regStatus = "Booked"`
       - `payStatus = "Unpaid"`
        5. 对应号源 `booked_count + 1`。
- 取消挂号：`POST /api/registrations/{regId}/cancel`
  - 将 `regStatus` 置为 `Canceled`，`payStatus` 置为 `Refunded`；
  - 同时将所属号源 `booked_count - 1`；
  - 若挂号已支付，则按一定比例收取违约手续费：
    - 默认挂号费 `reg_fee = 3000`（单位分，即 ¥30.00）；
    - 患者取消：退回 `90%` 费用（10% 作为违约金不退）；
    - 医生端取消（医生原因）：全额退款，不收取违约金。
- 支付挂号费：`POST /api/registrations/{regId}/pay`
  - 使用患者账户余额支付（`patient.money`，单位分）；
  - 若余额不足，返回错误提示“余额不足，请先充值”；
  - 扣除对应挂号费用，修改 `payStatus` 为 `Paid`，记录 `paidAt` 时间。
- 查询个人挂号记录：
  - 患者端：`GET /api/registrations`（默认使用当前登录 `userId`）。
  - 医生端：`GET /api/doctor/registrations?date=...`（查看某天本医生的患者名单）。

### 3.4 消息 & 通知模块

- 发送消息：`POST /api/messages`
- 收件箱：`GET /api/messages?box=inbox`
- 发件箱：`GET /api/messages?box=outbox`
- 作废消息：`POST /api/messages/{messageId}/invalidate`

**消息权限规则概括（含 10 天时效 + 需已支付）：**

- 患者：
  - 仅能给“已支付且 10 天内就诊的医生 / 该科室管理员”发消息。
- 医生：
  - 可给“已支付且 10 天内挂号的患者”发消息；
  - 可给本科室的科室管理员发送消息；
  - 可给系统管理员发送消息。
- 科室管理员：
  - 可给本科室医生；
  - 可给近 10 天在本科室就诊且已支付的患者。
- 系统管理员：
  - 可向任何用户发送系统通知。

> 超过 10 天的历史会话仍可查看，但发送时会被拒绝；患者未支付的挂号不会开启与医生/科室管理员的聊天权限。

系统通过查询 `registration`、`doctor`、`department_manager`、`system_user` 等表，确保消息发送遵守上述约束。

为模拟 QQ/微信 的体验，系统还提供：

- `GET /api/messages/conversations`
  - 返回当前用户与每个联系人的最近一条消息（联系人 ID、姓名、最后一条消息内容和时间），
    由前端在消息页左侧渲染为“会话列表”，按时间倒序排列。
- `GET /api/messages/conversation?targetUserId=...`
  - 返回当前用户与某个具体对象之间的全部消息记录，按时间正序排列，由前端渲染为类似聊天记录的气泡列表。

---

## 4. 数据库模式概览

主要表（简要）：

- `user_directory`：统一用户表
  - `user_id`, `user_name`, `phone`, `password`, `create_time`, `is_active`
- `patient`：患者扩展信息
  - `user_id`, `gender`, `age`, `id_card`, `money`
- `doctor`：医生信息
  - `user_id`, `doctor_name`, `department_id`, `title`, `specialty`, `work_phone`
- `department`：科室信息
  - `department_id`, `department_name`, `introduction`, `location`
- `department_manager`：科室管理员
  - `user_id`, `department_id`, `work_phone`
- `"system_user"`：系统管理员
  - `user_id`, `work_phone`
- `doctor_time_slot`：医生排班与号源
  - `slot_id`, `doctor_id`, `department_id`, `slot_date`, `time_slot`,
    `capacity`, `booked_count`, `status`, `note`
- `registration`：挂号记录
  - `reg_id`, `patient_id`, `doctor_id`, `department_id`,
    `reg_date`, `reg_time_slot`, `reg_status`, `reg_fee`, `pay_status`, `slot_id`
- `medical_record`：就诊记录
  - `record_id`, `reg_id`, `diagnosis`, `treatment_plan`, `advice`, `create_time`
- `message`：站内消息
  - `message_id`, `title`, `content`, `create_user_id`, `target_user_id`, `create_time`, `is_valid`
- `audit_log`：操作日志
  - `log_id`, `user_id`, `action`, `target`, `time`

完整建表 SQL 见：`sql/schema.sql`  
示例数据（覆盖各角色及多条挂号记录）见：`sql/sample_data.sql`。

---

## 5. 前端页面与接口映射

- `LoginRegister.vue`
  - 注册 / 登录 → `/api/auth/patient/register`、`/api/auth/login`
  - 登录成功后保存 `userInfo` 到 `localStorage`，并跳转到“个人中心”（`/profile`）
- `RegistrationPage.vue`
  - 科室选择 → `GET /api/departments`
  - 医生列表 + 可约时段（含起止时间/剩余量） → `GET /api/doctors`
  - 创建挂号 → `POST /api/registrations`（传 `slotId`）
- `MessagePage.vue`
  - 左侧会话列表 → `GET /api/messages/conversations`
  - 右侧聊天记录 → `GET /api/messages/conversation?targetUserId=...`
  - 发送消息 → `POST /api/messages`
  - 系统消息列表 → `GET /api/messages?box=inbox&isValid=true`，并显示发送方姓名 + UID
- `DoctorPanel.vue`
  - 当天挂号患者列表 → `GET /api/doctor/registrations`
  - 向患者发送消息 → `POST /api/messages`
  - 查看发件箱 → `GET /api/messages?box=outbox`
  - 点击“写病历”弹出表单 → `POST /api/medical-records` 为对应挂号写入诊疗记录
- `AdminPanel.vue`
  - 冻结 / 解冻账号 → `POST /api/users/{userId}/freeze` / `unfreeze`
  - 创建医生 / 科室管理员 / 系统管理员账号 →  
    `POST /api/users/doctors`、`/api/users/dept-managers`、`/api/users/system-users`
- `ProfilePage.vue`
  - 个人信息展示（本地缓存 + 登录返回）  
  - 查看和管理挂号记录：
    - 患者：`GET /api/registrations`
    - 医生：`GET /api/doctor/registrations`
    - “支付”：调用 `POST /api/registrations/{regId}/pay`，从余额中扣除费用；
    - “取消”：调用 `POST /api/registrations/{regId}/cancel`，根据是否支付进行部分退款或取消预约。
  - 查看就诊记录：
    - 患者：`GET /api/medical-records`，以表格形式展示诊断和医嘱；
    - 医生：`GET /api/medical-records/doctor`，以表格形式展示自己写过的病历（包含患者姓名）。
  - 余额与充值（仅患者）：
    - `GET /api/patient/me`：查看当前账户余额；
    - `POST /api/patient/recharge`：在个人中心输入金额（元），后端按分累加到 `patient.money`。
  - 修改密码 → `POST /api/auth/change-password`

---

## 6. 典型使用流程示例

1. **管理员初始化**
   - 执行 `schema.sql` 建表，`sample_data.sql` 插入样例数据。
   - 使用系统管理员账号 `AD0001 / admin123` 登录“管理员”界面。
   - 如需新增医生／科室管理员，使用“创建账号”功能。

2. **患者挂号**
   - 患者注册并登录（角色为 `patient`）。
   - 在“门诊挂号”页面：
     - 选择科室 + 就诊日期；
     - 系统加载可预约的医生及其可选时段；
     - 选择具体时段，点击“立即挂号”。
   - 在“个人中心 → 我的挂号记录”查看自己的挂号历史。

3. **医生工作台**
   - 医生使用自己的账号登录（角色为 `doctor`）。
   - 在“医生端”页面：
     - 选择日期查看当天挂号患者列表；
     - 为某位患者发送就诊提醒/复诊通知；
     - 号源由科室管理员维护，医生端只读；
     - 在特殊情况下，可主动取消某个挂号（患者费用全额退回）。

4. **消息与公告**
   - 每个用户可以在“消息”页查看自己的收件箱；
   - 对点消息会按“已支付 + 10 天有效期 + 角色限制”控制；
   - 患者可以选择“已支付且 10 天内就诊的医生 / 科室管理员”发消息并查看历史对话；
   - 医生可以向患者 / 科室管理员 / 系统管理员反馈问题（包括希望拉黑骚扰患者的请求）；
   - 管理员可通过消息机制模拟系统公告功能。

---

本系统主要用于教学场景，重点在于：

- 使用关系模型设计清晰的数据结构；
- 利用主外键和约束实现业务规则（科室、排班、号源与挂号的对应关系）；
- 在应用层实现基本的角色与权限控制；
- 前后端分离，通过 REST API 完成交互。

若后续需要撰写课程报告，可直接引用本说明文档中关于“角色与权限”“数据模型”和“业务流程”的内容，并结合 ER 图和 SQL 脚本进行展开说明。
