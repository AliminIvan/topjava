package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryMealRepository implements MealRepository {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final Map<Integer, Meal> repository;

    {
        repository = new ConcurrentHashMap<>();

        List<Meal> meals = new ArrayList<>();
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 7, 10, 0),
                "Завтрак", 500));
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 7, 14, 0),
                "Обед", 1000));
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 7, 19, 0),
                "Ужин", 900));
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 8, 9, 0),
                "Завтрак", 400));
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 8, 13, 0),
                "Обед", 900));
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 8, 18, 0),
                "Ужин", 600));
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 9, 9, 30),
                "Завтрак", 450));
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 9, 13, 40),
                "Обед", 780));
        meals.add(new Meal(null, LocalDateTime.of(2023, 10, 9, 20, 10),
                "Ужин", 710));
        meals.forEach(this::add);
    }

    public List<Meal> getAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public Meal getById(int id) {
        return repository.get(id);
    }

    @Override
    public Meal add(Meal meal) {
        meal.setId(counter.incrementAndGet());
        repository.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public Meal update(Meal meal) {
        return repository.put(meal.getId(), meal);
    }

    @Override
    public void delete(int id) {
        repository.remove(id);
    }
}
