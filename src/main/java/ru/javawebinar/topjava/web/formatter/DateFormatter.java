package ru.javawebinar.topjava.web.formatter;

import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatter implements Formatter<LocalDate> {
    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return object.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
