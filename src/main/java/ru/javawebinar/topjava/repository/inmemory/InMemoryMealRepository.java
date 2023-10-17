package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
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
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        Integer mealId = meal.getId();
        Integer userId = meal.getUserId();
        Meal updatedMeal = get(mealId, userId);
        return nonNull(updatedMeal) ? repository.computeIfPresent(mealId, (id, oldMeal) -> meal) : null;
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal deletedMeal = get(id, userId);
        boolean result = false;
        if (nonNull(deletedMeal)) {
            result = repository.remove(id) != null;
        }
        return result;
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
                .sorted((m1, m2) -> m2.getDateTime().compareTo(m1.getDateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int userId, LocalDateTime start, LocalDateTime end) {
        return getAll(userId).stream()
                .filter(meal -> isBetweenHalfOpen(meal.getDateTime(), start, end))
                .collect(Collectors.toList());
    }
}

