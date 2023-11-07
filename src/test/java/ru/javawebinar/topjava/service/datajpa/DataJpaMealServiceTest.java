package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.service.MealServiceTest;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.Profiles.DATAJPA;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles(DATAJPA)
public class DataJpaMealServiceTest extends MealServiceTest {

    @Autowired
    private MealService service;

    @Test
    public void getMealWithUser() {
        Meal actual = service.getMealWithUser(ADMIN_MEAL_ID, ADMIN_ID);
        USER_MATCHER.assertMatch(actual.getUser(), UserTestData.admin);
        MEAL_MATCHER.assertMatch(actual, adminMeal1);
    }

    @Test
    public void getMealWithUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.getMealWithUser(MealTestData.NOT_FOUND, USER_ID));
    }

    @Test
    public void getMealWithUserNotOwn() {
        assertThrows(NotFoundException.class, () -> service.getMealWithUser(MEAL1_ID, ADMIN_ID));
    }
}
