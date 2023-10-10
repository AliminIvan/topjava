package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealRepository {
    List<Meal> getAll();

    Meal getMealById(int id);

    void addMeal(Meal meal);

    void updateMeal(Meal meal);

    void delete(int id);
}
