package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InMemoryMealRepository {
    public static final int CALORIES_PER_DAY = 2000;
    private final List<Meal> mealList;

    {
        mealList = new ArrayList<>();
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 7, 10, 0),
                "Завтрак", 500));
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 7, 14, 0),
                "Обед", 1000));
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 7, 19, 0),
                "Ужин", 900));
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 8, 9, 0),
                "Завтрак", 400));
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 8, 13, 0),
                "Обед", 900));
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 8, 18, 0),
                "Ужин", 600));
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 9, 9, 30),
                "Завтрак", 450));
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 9, 13, 40),
                "Обед", 780));
        mealList.add(new Meal(LocalDateTime.of(2023, 10, 9, 20, 10),
                "Ужин", 710));
    }

    public List<Meal> getAllMeals() {
        return mealList;
    }
}
