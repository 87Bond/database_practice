package com.medical.backend.common;

public final class ErrorCodes {
    private ErrorCodes() {
    }

    public static final int OK = 0;
    public static final int ACCOUNT_NOT_FOUND = 1;
    public static final int PASSWORD_WRONG = 2;
    public static final int PHONE_EXISTS = 101;
    public static final int NO_PERMISSION = 201;
    public static final int SLOT_NOT_ENOUGH = 301;
    public static final int DOCTOR_DEPT_MISMATCH = 302;
    public static final int CANCEL_TIME_EXCEEDED = 303;
    public static final int PARAM_ERROR = 400;
    public static final int SERVER_ERROR = 500;
}

