package ru.javawebinar.topjava.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.NOT_FOUND;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;

    @Test
    public void get() {
        Meal actual = mealService.get(USER_MEAL_ID, USER_ID);
        assertMatch(actual, expectedUserMeal);
    }

    @Test
    public void getNotFound() {
        Assert.assertThrows(NotFoundException.class, () -> mealService.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getWithWrongUserId() {
        Assert.assertThrows(NotFoundException.class, () -> mealService.get(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void delete() {
        mealService.delete(USER_MEAL_ID, USER_ID);
        Assert.assertThrows(NotFoundException.class, () -> mealService.get(USER_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        Assert.assertThrows(NotFoundException.class, () -> mealService.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void deleteWithWrongUserId() {
        Assert.assertThrows(NotFoundException.class, () -> mealService.delete(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusiveFrom() {
        List<Meal> actual = mealService.getBetweenInclusive(LOCAL_DATE.plusDays(2), null, USER_ID);
        List<Meal> expected = Arrays.asList(userMeal5, userMeal4, userMeal3);
        assertMatch(actual, expected);
    }

    @Test
    public void getBetweenInclusiveTo() {
        List<Meal> actual = mealService.getBetweenInclusive(null, LOCAL_DATE, USER_ID);
        List<Meal> expected = Arrays.asList(userMeal2, userMeal1, expectedUserMeal);
        assertMatch(actual, expected);
    }

    @Test
    public void getBetweenInclusiveNotFound() {
        List<Meal> actual = mealService.getBetweenInclusive(LOCAL_DATE, LOCAL_DATE.plusDays(1), NOT_FOUND);
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void getAll() {
        List<Meal> actual = mealService.getAll(USER_ID);
        List<Meal> expected = Arrays.asList(userMeal5, userMeal4, userMeal3, userMeal2, userMeal1, expectedUserMeal);
        assertMatch(actual, expected);
    }

    @Test
    public void getAllNotFound() {
        List<Meal> actual = mealService.getAll(NOT_FOUND);
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void update() {
        Meal updated = MealTestData.getUpdated();
        mealService.update(updated, USER_ID);
        assertMatch(mealService.get(USER_MEAL_ID, USER_ID), MealTestData.getUpdated());
    }

    @Test
    public void updateNotFound() {
        Meal updated = MealTestData.getUpdated();
        updated.setId(NOT_FOUND);
        Assert.assertThrows(NotFoundException.class, () -> mealService.update(updated, USER_ID));
    }

    @Test
    public void updateWithWrongUserId() {
        Meal updated = MealTestData.getUpdated();
        updated.setId(USER_MEAL_ID);
        Assert.assertThrows(NotFoundException.class, () -> mealService.update(updated, ADMIN_ID));
    }

    @Test
    public void create() {
        Meal createdMeal = mealService.create(MealTestData.getNew(), USER_ID);
        Integer newId = createdMeal.getId();
        Meal newMeal = MealTestData.getNew();
        newMeal.setId(newId);
        assertMatch(createdMeal, newMeal);
        assertMatch(mealService.get(newId, USER_ID), newMeal);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThrows(DataAccessException.class, () ->
                mealService.create(new Meal(null, userMeal1.getDateTime(), "Duplicate", 500), USER_ID));
    }
}