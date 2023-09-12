package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        List<UserMealWithExcess> resultList = new ArrayList<>();

        List<UserMealWithExcess> userMealWithExcessList =
                convertUserMealToUserMealWithExcess(meals, caloriesPerDay);

        for (UserMealWithExcess userMealWithExcess : userMealWithExcessList) {
            LocalTime mealWithExcessTime = userMealWithExcess.getDateTime().toLocalTime();
            if (TimeUtil.isBetweenHalfOpen(mealWithExcessTime, startTime, endTime)) {
                resultList.add(userMealWithExcess);
            }
        }

        return resultList;
    }

    private static List<UserMealWithExcess> convertUserMealToUserMealWithExcess(List<UserMeal> meals, int caloriesPerDay) {

        List<UserMealWithExcess> userMealWithExcessList = new ArrayList<>();

        Map<LocalDate, Integer> map = new HashMap<>();

        for (UserMeal meal : meals) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            map.merge(mealDate, meal.getCalories(), Integer::sum);
        }

        for (UserMeal meal : meals) {
            LocalDate mealDate = meal.getDateTime().toLocalDate();
            boolean excess = map.get(mealDate) > caloriesPerDay;
            userMealWithExcessList.add(
                    new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess));
        }

        return userMealWithExcessList;

    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> calculatedCaloriesPerDayMap = meals.stream()
                .collect(Collectors.toMap(
                        userMeal -> userMeal.getDateTime().toLocalDate(),
                        UserMeal::getCalories,
                        Integer::sum));

        return meals.stream()
                .map(userMeal -> {
                    LocalDate mealDate = userMeal.getDateTime().toLocalDate();
                    boolean excess = calculatedCaloriesPerDayMap.get(mealDate) > caloriesPerDay;
                    return new UserMealWithExcess(
                            userMeal.getDateTime(),
                            userMeal.getDescription(),
                            userMeal.getCalories(),
                            excess
                    );
                })
                .filter(userMealWithExcess -> TimeUtil.isBetweenHalfOpen(userMealWithExcess.getDateTime().toLocalTime(),
                        startTime, endTime))
                .collect(Collectors.toList());
    }
}
