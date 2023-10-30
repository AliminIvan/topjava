package ru.javawebinar.topjava.repository.jpa;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class JpaMealRepositoryTest {

    @Autowired
    private MealRepository mealRepository;

    @Test
    public void saveWhenNew() {
        Meal savedMeal = mealRepository.save(getNew(), USER_ID);
        Integer id = savedMeal.getId();
        Meal newMeal = getNew();
        newMeal.setId(id);
        MEAL_MATCHER.assertMatch(savedMeal, newMeal);
        MEAL_MATCHER.assertMatch(mealRepository.get(id, USER_ID), newMeal);
    }

    @Test
    public void saveWhenUpdating() {
        Meal updatedMeal = getUpdated();
        updatedMeal.setUser(UserTestData.user);
        Integer id = mealRepository.save(updatedMeal, USER_ID).getId();
        MEAL_MATCHER.assertMatch(mealRepository.get(id, USER_ID), getUpdated());
    }

    @Test
    public void saveWhenUpdatingNotOwn() {
        Meal updated = getUpdated();
        updated.setUser(UserTestData.user);
        Assert.assertNull(mealRepository.save(updated, ADMIN_ID));
    }

    @Test
    public void saveWhenDuplicateDateTime() {
        assertThrows(DataAccessException.class, () ->
                mealRepository.save(new Meal(null, meal1.getDateTime(),
                        "Duplicate", 500), USER_ID));
    }

    @Test
    public void delete() {
        Assert.assertTrue(mealRepository.delete(MEAL1_ID, USER_ID));
        Assert.assertNull(mealRepository.get(MEAL1_ID, USER_ID));
    }

    @Test
    public void deleteNotOwn() {
        Assert.assertFalse(mealRepository.delete(MEAL1_ID, ADMIN_ID));
        Assert.assertNotNull(mealRepository.get(MEAL1_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        Assert.assertFalse(mealRepository.delete(NOT_FOUND, USER_ID));
    }

    @Test
    public void get() {
        Meal actual = mealRepository.get(ADMIN_MEAL_ID, ADMIN_ID);
        MEAL_MATCHER.assertMatch(actual, adminMeal1);
    }

    @Test
    public void getNotFound() {
        Assert.assertNull(mealRepository.get(NOT_FOUND, USER_ID));
    }

    @Test
    public void getNotOwn() {
        Assert.assertNull(mealRepository.get(MEAL1_ID, ADMIN_ID));
    }

    @Test
    public void getAll() {
        MEAL_MATCHER.assertMatch(mealRepository.getAll(USER_ID), meals);
    }

    @Test
    public void getAllNotFound() {
        List<Meal> actual = mealRepository.getAll(UserTestData.NOT_FOUND);
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void getBetweenHalfOpen() {
        List<Meal> actual = mealRepository.getBetweenHalfOpen(meal1.getDateTime(), meal4.getDateTime(), USER_ID);
        MEAL_MATCHER.assertMatch(actual, Arrays.asList(meal3, meal2, meal1));
    }

    @Test
    public void getBetweenHalfOpenNotOwn() {
        List<Meal> actual = mealRepository.getBetweenHalfOpen(meal1.getDateTime(), meal4.getDateTime(), ADMIN_ID);
        Assert.assertEquals(0, actual.size());
    }
}