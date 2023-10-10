package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealRepositoryImpl implements MealRepository {
    public static final AtomicInteger mealCounter = new AtomicInteger(0);
    private final List<Meal> mealList;

    {
        mealList = new CopyOnWriteArrayList<>();
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

    public List<Meal> getAll() {
        return mealList;
    }

    @Override
    public Meal getMealById(int id) {
        return mealList.stream()
                .filter(meal -> meal.getId() == id)
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public void addMeal(Meal meal) {
        mealList.add(meal);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void updateMeal(Meal meal) {
        Meal oldMeal = getMealById(meal.getId());
        Collections.replaceAll(mealList, oldMeal, meal);
    }

    @Override
    public void delete(int id) {
        mealList.removeIf(meal -> meal.getId() == id);
    }
}
