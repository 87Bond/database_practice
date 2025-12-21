# 医院挂号系统（数据库原理实验）

基于 requirements 下的需求说明和 ER 图，实现的一个简单“医院挂号系统”前后端示例：

- 后端：Spring Boot 3.5 + MyBatis + PostgreSQL 16（目录：`backend/backend`）
- 前端：Vue 2（目录：`medical-regist`）
- 数据库：PostgreSQL，使用 `sql/schema.sql` 中的模式
- demo网址：http://database.zhengshibo.top:2000/

> 关于“数据库模式在哪里建”：  
> 本项目 **不自动建表**，推荐你在 DBeaver 里连接 PostgreSQL，手动执行 `sql/schema.sql`（以及可选的 `sql/sample_data.sql`）来创建表结构和初始数据，更符合“数据库原理”实验的要求。

---

## 一、环境要求

- JDK 17（你本机已有）
- Maven 3.9+（你本机已有）
- Node.js 16+/npm 或 pnpm（用于跑 Vue 前端）
- PostgreSQL 16（本地 `localhost:5432`）

---

## 二、数据库准备（建议用 DBeaver）

1. 选择要使用的数据库

   你可以：

   - 直接在默认的 `postgres` 数据库中建表（**当前 `application.properties` 默认就是连的 `postgres`**），或者
   - 新建一个数据库，比如：

     ```sql
     CREATE DATABASE medical_regist
       WITH ENCODING 'UTF8';
     ```

     然后把后端的 `spring.datasource.url` 改成连这个库。

2. 连接选定的数据库，并执行建表脚本

   - 打开 SQL Editor，执行仓库根目录下：`sql/schema.sql`
   - 这会创建：
     - `user_directory`, `patient`, `doctor`, `department`, `department_manager`,
       `system_user`, `doctor_time_slot`, `registration`, `medical_record`,
       `message`, `audit_log` 等表

3. （可选）插入示例数据

   为了方便你直接测试挂号流程，可以执行：

   - `sql/sample_data.sql`

   `sample_data.sql` 会创建一整套比较丰富的测试数据，包括：

   - 10 个科室（`D001`~`D010`）
   - 10 个患者（`P0001`~`P0010`）
   - 10 个医生（`DR0001`~`DR0010`，分别属于不同科室）
   - 10 个科室管理员（`DM0001`~`DM0010`）
   - 10 个系统管理员（`AD0001`~`AD0010`，密码 `admin123`）
   - 多个医生排班号源 `doctor_time_slot`（不同日期、不同时间段）
   - 多条挂号记录 `registration`（Paid / Unpaid / Canceled / Refunded 状态）
   - 若干初始病历 `medical_record`、站内消息 `message` 和操作日志 `audit_log`

4. 患者数据

   患者不需要预先插入，可以通过前端的“注册”页面调用接口 `POST /api/auth/patient/register` 自动写入：

   - `user_directory`（统一用户表）
   - `patient`（患者扩展信息表）

---

## 三、后端（Spring Boot + MyBatis）

后端代码位置：`backend/backend`

### 1. 配置数据库连接

编辑：`backend/backend/src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=zhengshibo
spring.datasource.password=
```

如果你在 PostgreSQL 中创建了独立的 `medical_regist` 数据库，  
可以把 `url` 改成：`jdbc:postgresql://localhost:5432/medical_regist`，  
用户名、密码保持和你在 DBeaver 里连接时一致即可。

### 2. 主要依赖

`backend/backend/pom.xml` 中已配置：

- `spring-boot-starter-web`：REST 接口
- `spring-boot-starter-jdbc`：数据库连接池
- `mybatis-spring-boot-starter`：MyBatis 整合
- `postgresql`：PostgreSQL 驱动
- `lombok`：简化实体类 getter/setter

### 3. 启动后端

在项目根目录执行：

```bash
cd backend/backend
mvn spring-boot:run
```

默认服务地址：`http://localhost:8080`

### 4. 已实现的主要接口（和 requirements 对齐）

- 账号 & 鉴权（`/api/auth`）
  - `POST /api/auth/patient/register`：患者注册（PatientRegister）
  - `POST /api/auth/login`：登录（UserLogin）
  - `POST /api/auth/change-password`：修改密码（ChangePassword）
- 科室 & 医生 & 号源
  - `GET /api/departments`：科室列表（ListDepartments）
  - `GET /api/doctors`：医生 + 可约号源（包含具体起止时间和剩余数量）
  - `GET /api/registrations/slots`：查询号源（QuerySlots）
- 挂号流程（`/api/registrations`）
  - `POST /api/registrations`：创建挂号（CreateRegistration，传 `slotId`，后端会把时段存成“HH:mm-HH:mm”）
  - `POST /api/registrations/{regId}/cancel`：取消挂号（CancelRegistration）
  - `POST /api/registrations/{regId}/pay`：支付挂号费（PayRegistration）
  - `GET /api/registrations`：查询挂号记录（ListRegistrations）
- 患者账户 / 余额（`/api/patient`）
  - `GET /api/patient/me`：查看当前患者信息和余额
  - `POST /api/patient/recharge`：账户充值（金额单位：元）
- 医生工作台（`/api/doctor`）
  - `GET /api/doctor/registrations`：按日期查看本医生的挂号患者列表
- 就诊记录（`/api/medical-records`）
  - `GET /api/medical-records`：患者查看自己的就诊记录
  - `GET /api/medical-records/doctor`：医生查看自己写过的就诊记录
  - `POST /api/medical-records`：医生录入某次挂号的诊疗记录
- 消息（`/api/messages`）
  - `POST /api/messages`：发送消息（SendMessage）
  - `GET /api/messages`：收件箱 / 发件箱（ListMessages）
  - `POST /api/messages/{messageId}/invalidate`：作废消息（InvalidateMessage）
  - `GET /api/messages/conversations`：会话列表（每个联系人最近一条）
  - `GET /api/messages/conversation`：与某个对象的全部聊天记录
- 冻结 / 解冻用户（`/api/users`）
  - `POST /api/users/{userId}/freeze`：冻结用户（FreezeUser）
  - `POST /api/users/{userId}/unfreeze`：解冻用户（UnfreezeUser）

- 科室管理员工作台（`/api/dept`）
  - `GET /api/dept/doctors`：查看本科室医生
  - `POST /api/dept/slots`：为本科室医生创建号源（起止时间 + 容量）
  - `GET /api/dept/slots`：查看本科室某日全部号源
  - `GET /api/dept/registrations`：查看本科室所有医生的挂号记录
  - `POST /api/dept/doctors/{doctorId}/assign`：把已有医生划归当前科室

> 说明：为了简化作业，没有引入 Spring Security / 真正的 JWT。  
> 登录成功后返回的 `token` 实际上就是 `userId`，前端在请求时通过 HTTP 头 `X-User-Id` 传回，后端据此识别当前用户。

---

## 四、前端（Vue 2）

前端代码位置：`medical-regist`

### 1. 安装依赖

```bash
cd medical-regist
npm install
```

### 2. 启动前端开发服务器

```bash
npm run serve
```

默认地址：`http://localhost:8081`（以实际终端输出为准）。

`vue.config.js` 中已配置代理：

```js
devServer: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

所以前端发往 `/api/...` 的请求会自动转发到本地的 Spring Boot 后端，不会有跨域问题。

### 3. 前端与接口的对应关系

- `src/views/LoginRegister.vue`
  - 登录：调用 `POST /api/auth/login`
  - 注册：调用 `POST /api/auth/patient/register`
  - 登录成功后，把后端返回的 `token` 和 `userInfo` 存到 `localStorage`，并跳转到“个人中心”
- `src/views/RegistrationPage.vue`
  - 初始化加载科室列表：`GET /api/departments`
  - 选择科室 + 日期后加载医生及可预约时段：`GET /api/doctors?departmentId=&date=`（返回具体起止时间与剩余号源）
  - 点击“立即挂号”：`POST /api/registrations`（传 `slotId`）
- `src/views/MessagePage.vue`
  - 左侧会话列表：`GET /api/messages/conversations`
  - 中间聊天窗口：`GET /api/messages/conversation?targetUserId=...`
  - 发送消息：`POST /api/messages`
- `src/views/ProfilePage.vue`
  - 显示当前登录用户基本信息（来自 `localStorage`）
  - “我的挂号记录”：
    - 患者：`GET /api/registrations`
    - 医生：`GET /api/doctor/registrations`
  - “我的就诊记录”：
    - 患者：`GET /api/medical-records`
    - 医生：`GET /api/medical-records/doctor`
  - “账户余额 / 充值”（仅患者）：`GET /api/patient/me`、`POST /api/patient/recharge`
  - “修改密码”：`POST /api/auth/change-password`
  - “退出登录”：清空本地 `localStorage` 后跳转到登录页
- `src/views/DoctorPanel.vue`
  - 查看某天的挂号患者：`GET /api/doctor/registrations`
  - 给患者发消息：`POST /api/messages`
  - 给某条挂号写病历：`POST /api/medical-records`
- `src/views/DeptPanel.vue`
  - 查看本科室医生：`GET /api/dept/doctors`
  - 创建号源（起止时间 + 容量）：`POST /api/dept/slots`
  - 查看号源：`GET /api/dept/slots`
  - 查看本科室挂号记录：`GET /api/dept/registrations`
  - 将已有医生划归本科室：`POST /api/dept/doctors/{doctorId}/assign`

`src/api.js` 里对 axios 做了简单封装，在每个请求自动附带 `X-User-Id` 头：

```js
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['X-User-Id'] = token
  }
  return config
})
```

---

## 五、推荐的完整运行步骤（从零开始）

1. **数据库**
   - 在 PostgreSQL 中创建 `medical_regist` 数据库
   - 用 DBeaver 连接 `medical_regist`
   - 执行 `sql/schema.sql` 创建所有表
   - 可选：执行 `sql/sample_data.sql` 插入示例科室、医生和号源

2. **后端**
   - 修改 `backend/backend/src/main/resources/application.properties` 中的数据库用户名和密码
   - 在 `backend/backend` 目录运行：`mvn spring-boot:run`

3. **前端**
   - 在 `medical-regist` 目录执行：`npm install`
   - 开发模式：`npm run serve`（依赖 devServer 代理 `/api -> http://localhost:8080`）
   - 生产打包：`npm run build`，产物在 `medical-regist/dist`
     - 若用本地静态服务器预览 dist，请确保后端地址正确：`src/api.js` 已设为 `http://localhost:8080/api`
     - 如需自定义后端地址，修改 `src/api.js` 的 `baseURL`

4. **体验流程**
   - 浏览器打开前端地址（一般是 `http://localhost:8080` 或 `http://localhost:8081`，以终端输出为准）
   - 在“注册”页注册一个患者账号
   - 使用手机号登录
   - 在“门诊挂号”页选择：科室 D001 + 明天日期 → 选择时段 → 挂号
   - 在“个人中心”查看挂号记录，在“系统消息”页查看消息（如果后续你扩展医生端/管理员端来发送消息）

---

如果你后面希望：

- 改成由 Spring Boot 自动执行 `schema.sql` 来建表，也可以在后端加上：

```properties
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
```

但考虑到实验课程通常要求“手工建模建表”，当前设计仍推荐你在 DBeaver 中执行 SQL 脚本完成建表与初始化。
