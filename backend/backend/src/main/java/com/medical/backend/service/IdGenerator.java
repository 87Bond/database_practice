package com.medical.backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public final class IdGenerator {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private IdGenerator() {
    }

    public static String newPatientId() {
        return "P" + timestampRandom();
    }

    public static String newDoctorId() {
        return "DR" + timestampRandom();
    }

    public static String newDeptManagerId() {
        return "DM" + timestampRandom();
    }

    public static String newAdminId() {
        return "AD" + timestampRandom();
    }

    public static String newRegistrationId() {
        return "R" + timestampRandom();
    }

    public static String newMessageId() {
        return "M" + timestampRandom();
    }

    private static String timestampRandom() {
        int rand = ThreadLocalRandom.current().nextInt(100, 1000);
        return LocalDateTime.now().format(TIME_FORMATTER) + rand;
    }
}
