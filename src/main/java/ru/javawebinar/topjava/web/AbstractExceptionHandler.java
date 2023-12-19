package ru.javawebinar.topjava.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.Map;

public class AbstractExceptionHandler {
    @Autowired
    protected MessageSource messageSource;
    public static final String EXCEPTION_DUPLICATE_EMAIL = "User with this email already exists";
    public static final String EXCEPTION_DUPLICATE_DATETIME = "Meal with this datetime already exists";

    public static final Map<String, String> CONSTRAINTS_VIOLATION_MAP = Map.of(
            "users_unique_email_idx", EXCEPTION_DUPLICATE_EMAIL,
            "meal_unique_user_datetime_idx", EXCEPTION_DUPLICATE_DATETIME
    );

    public static String getMessageCodeFromConstraintKey(String key) {
        return key.replaceAll("_", ".");
    }
}
