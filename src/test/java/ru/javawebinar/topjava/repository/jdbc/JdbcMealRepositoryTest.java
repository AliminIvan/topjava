package ru.javawebinar.topjava.repository.jdbc;

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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class JdbcMealRepositoryTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private JdbcMealRepository jdbcMealRepository;

    @Test
    public void saveWhenNewMeal() {
        Meal savedMeal = jdbcMealRepository.save(MealTestData.getNew(), USER_ID);
        Integer id = savedMeal.getId();
        Meal newMeal = MealTestData.getNew();
        newMeal.setId(id);
        assertMatch(savedMeal, newMeal);
        assertMatch(jdbcMealRepository.get(id, USER_ID), newMeal);
    }

    @Test
    public void saveWhenUpdatingMeal() {
        Meal updated = MealTestData.getUpdated();
        jdbcMealRepository.save(updated, USER_ID);
        assertMatch(jdbcMealRepository.get(USER_MEAL_ID, USER_ID), MealTestData.getUpdated());
    }

    @Test
    public void saveWhenUpdatingMealWithWrongUserId() {
        Meal updated = MealTestData.getUpdated();
        Assert.assertNull(jdbcMealRepository.save(updated, ADMIN_ID));
    }

    @Test
    public void saveWhenUpdatingMealNotFound() {
        Meal updated = MealTestData.getUpdated();
        Assert.assertNull(jdbcMealRepository.save(updated, NOT_FOUND));
    }

    @Test
    public void saveWhenDuplicateDateTime() {
        assertThrows(DataAccessException.class, () ->
                jdbcMealRepository.save(new Meal(null, LocalDateTime.of(2023, 10, 21, 10, 0),
                        "Duplicate", 500), USER_ID));
    }

    @Test
    public void delete() {
        Assert.assertTrue(jdbcMealRepository.delete(USER_MEAL_ID, USER_ID));
        Assert.assertNull(jdbcMealRepository.get(USER_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteWithWrongUserId() {
        Assert.assertFalse(jdbcMealRepository.delete(USER_MEAL_ID, ADMIN_ID));
        Assert.assertNotNull(jdbcMealRepository.get(USER_MEAL_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        Assert.assertFalse(jdbcMealRepository.delete(USER_MEAL_ID, NOT_FOUND));
    }

    @Test
    public void get() {
        Meal actual = jdbcMealRepository.get(USER_MEAL_ID, USER_ID);
        assertMatch(actual, expectedUserMeal);
    }

    @Test
    public void getWithWrongUserId() {
        Assert.assertNull(jdbcMealRepository.get(USER_MEAL_ID, ADMIN_ID));
    }

    @Test
    public void getNotFound() {
        Assert.assertNull(jdbcMealRepository.get(USER_MEAL_ID, NOT_FOUND));
    }

    @Test
    public void getAll() {
        List<Meal> actual = jdbcMealRepository.getAll(USER_ID);
        List<Meal> expected = Arrays.asList(userMeal5, userMeal4, userMeal3, userMeal2, userMeal1, expectedUserMeal);
        assertMatch(actual, expected);
    }

    @Test
    public void getAllNotFound() {
        List<Meal> actual = jdbcMealRepository.getAll(NOT_FOUND);
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void getBetweenHalfOpenNotFound() {
        List<Meal> actual = jdbcMealRepository.getBetweenHalfOpen(LOCAL_DATE_TIME, LOCAL_DATE_TIME.plusDays(1), NOT_FOUND);
        Assert.assertEquals(0, actual.size());
    }

    @Test
    public void getBetweenHalfOpenFrom() {
        List<Meal> actual = jdbcMealRepository.getBetweenHalfOpen(LOCAL_DATE_TIME, MAX_DATE, USER_ID);
        List<Meal> expected = Arrays.asList(userMeal5, userMeal4, userMeal3, userMeal2, userMeal1, expectedUserMeal);
        assertMatch(actual, expected);
    }

    @Test
    public void getBetweenHalfOpenTo() {
        List<Meal> actual = jdbcMealRepository.getBetweenHalfOpen(MIN_DATE, LOCAL_DATE_TIME.plusDays(1), USER_ID);
        List<Meal> expected = Arrays.asList(userMeal2, userMeal1, expectedUserMeal);
        assertMatch(actual, expected);
    }
}