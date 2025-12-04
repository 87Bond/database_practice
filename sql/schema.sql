-- PostgreSQL schema for hospital registration system
-- Based on ER diagram in requirements

CREATE TABLE IF NOT EXISTS user_directory (
    user_id      VARCHAR(32) PRIMARY KEY,
    user_name    VARCHAR(100) NOT NULL,
    phone        VARCHAR(20) UNIQUE,
    password     VARCHAR(255) NOT NULL,
    create_time  TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active    BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS patient (
    user_id   VARCHAR(32) PRIMARY KEY REFERENCES user_directory(user_id),
    gender    VARCHAR(10),
    age       INTEGER,
    id_card   VARCHAR(32),
    money     INTEGER DEFAULT 0
);

CREATE TABLE IF NOT EXISTS department (
    department_id   VARCHAR(32) PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL,
    introduction    TEXT,
    location        VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS doctor (
    user_id        VARCHAR(32) PRIMARY KEY REFERENCES user_directory(user_id),
    doctor_name    VARCHAR(100) NOT NULL,
    department_id  VARCHAR(32) NOT NULL REFERENCES department(department_id),
    title          VARCHAR(50),
    specialty      VARCHAR(255),
    work_phone     VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS department_manager (
    user_id        VARCHAR(32) PRIMARY KEY REFERENCES user_directory(user_id),
    department_id  VARCHAR(32) NOT NULL REFERENCES department(department_id),
    work_phone     VARCHAR(20)
);

-- system_user 是 SQL 关键字，这里加引号避免语法冲突
CREATE TABLE IF NOT EXISTS "system_user" (
    user_id    VARCHAR(32) PRIMARY KEY REFERENCES user_directory(user_id),
    work_phone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS doctor_time_slot (
    slot_id        VARCHAR(32) PRIMARY KEY,
    doctor_id      VARCHAR(32) NOT NULL REFERENCES doctor(user_id),
    department_id  VARCHAR(32) NOT NULL REFERENCES department(department_id),
    slot_date      DATE NOT NULL,
    time_slot      VARCHAR(20) NOT NULL,
    start_time     TIME NOT NULL,
    end_time       TIME NOT NULL,
    capacity       INTEGER NOT NULL,
    booked_count   INTEGER NOT NULL DEFAULT 0,
    status         VARCHAR(20) NOT NULL,
    note           TEXT,
    create_time    TIMESTAMP NOT NULL DEFAULT NOW(),
    update_time    TIMESTAMP
);

CREATE TABLE IF NOT EXISTS registration (
    reg_id         VARCHAR(32) PRIMARY KEY,
    patient_id     VARCHAR(32) NOT NULL REFERENCES patient(user_id),
    doctor_id      VARCHAR(32) NOT NULL REFERENCES doctor(user_id),
    department_id  VARCHAR(32) NOT NULL REFERENCES department(department_id),
    reg_date       DATE NOT NULL,
    reg_time_slot  VARCHAR(20) NOT NULL,
    reg_status     VARCHAR(20) NOT NULL,
    reg_fee        INTEGER NOT NULL,
    pay_status     VARCHAR(20) NOT NULL,
    create_time    TIMESTAMP NOT NULL DEFAULT NOW(),
    paid_at        TIMESTAMP,
    slot_id        VARCHAR(32) REFERENCES doctor_time_slot(slot_id)
);

CREATE TABLE IF NOT EXISTS medical_record (
    record_id      VARCHAR(32) PRIMARY KEY,
    reg_id         VARCHAR(32) NOT NULL REFERENCES registration(reg_id),
    diagnosis      TEXT,
    treatment_plan TEXT,
    advice         TEXT,
    create_time    TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS message (
    message_id     VARCHAR(32) PRIMARY KEY,
    title          VARCHAR(200) NOT NULL,
    content        TEXT NOT NULL,
    create_user_id VARCHAR(32) NOT NULL REFERENCES user_directory(user_id),
    target_user_id VARCHAR(32) NOT NULL REFERENCES user_directory(user_id),
    create_time    TIMESTAMP NOT NULL DEFAULT NOW(),
    is_valid       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS audit_log (
    log_id   VARCHAR(32) PRIMARY KEY,
    user_id  VARCHAR(32),
    action   VARCHAR(100),
    target   VARCHAR(100),
    time     TIMESTAMP NOT NULL DEFAULT NOW()
);
