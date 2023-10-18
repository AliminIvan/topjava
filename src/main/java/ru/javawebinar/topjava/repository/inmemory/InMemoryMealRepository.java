package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenHalfOpen;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        Integer mealId = meal.getId();
        Meal updatedMeal = get(mealId, userId);
        return nonNull(updatedMeal) ? repository.computeIfPresent(mealId, (id, oldMeal) -> meal) : null;
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal deletedMeal = get(id, userId);
        return nonNull(deletedMeal) && repository.remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(id);
        return nonNull(meal) && Objects.equals(meal.getUserId(), userId) ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.values().stream()
                .filter(meal -> Objects.equals(meal.getUserId(), userId))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAllByDateTime(int userId, LocalDateTime start, LocalDateTime end) {
        return getAll(userId).stream()
                .filter(meal -> isBetweenHalfOpen(meal.getDateTime(), start, end))
                .filter(meal -> isBetweenHalfOpen(meal.getTime(), start.toLocalTime(), end.toLocalTime()))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

