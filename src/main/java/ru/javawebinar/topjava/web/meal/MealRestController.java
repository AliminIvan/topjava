package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;
@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getAll(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        log.info("getAll with filter");
        Optional<LocalDate> optionalStartDate = Optional.ofNullable(startDate);
        Optional<LocalDate> optionalEndDate = Optional.ofNullable(endDate);
        Optional<LocalTime> optionalStartTime = Optional.ofNullable(startTime);
        Optional<LocalTime> optionalEndTime = Optional.ofNullable(endTime);
        LocalDateTime start = LocalDateTime.of(optionalStartDate.orElse(LocalDate.MIN), optionalStartTime.orElse(LocalTime.MIN));
        LocalDateTime end = LocalDateTime.of(optionalEndDate.orElse(LocalDate.MAX), optionalEndTime.orElse(LocalTime.MAX));
        return MealsUtil.getFilteredTos(service.getAll(authUserId()), authUserCaloriesPerDay(), start, end).stream()
                .filter(mealTo -> DateTimeUtil.isBetweenHalfOpen(mealTo.getDateTime().toLocalTime(),
                        start.toLocalTime(), end.toLocalTime()))
                .collect(Collectors.toList());
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal);
    }

}