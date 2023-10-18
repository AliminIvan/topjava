package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getAllByDateTime(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        log.info("getAll with filter");
        LocalDateTime start = LocalDateTime.of(Optional.ofNullable(startDate).orElse(LocalDate.MIN),
                Optional.ofNullable(startTime).orElse(LocalTime.MIN));
        LocalDateTime end = LocalDateTime.of(Optional.ofNullable(endDate).orElse(LocalDate.MAX),
                Optional.ofNullable(endTime).orElse(LocalTime.MAX));
        return MealsUtil.getTos(service.getAllByDateTime(authUserId(), start, end), authUserCaloriesPerDay());
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, authUserId());
    }

    public Meal create(Meal meal) {
        meal.setUserId(authUserId());
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal, meal.getUserId());
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        meal.setUserId(authUserId());
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, meal.getUserId());
    }
}