package ru.javawebinar.topjava.util.exception;

import java.util.Map;

public class ExceptionUtil {
    private ExceptionUtil() {
    }
    public static final String EXCEPTION_DUPLICATE_EMAIL = "User with this email already exists";
    public static final String EXCEPTION_DUPLICATE_DATETIME = "Meal with this datetime already exists";

    public static final Map<String, String> CONSTRAINTS_MAP = Map.of(
            "users_unique_email_idx", EXCEPTION_DUPLICATE_EMAIL,
            "meal_unique_user_datetime_idx", EXCEPTION_DUPLICATE_DATETIME
    );

}
