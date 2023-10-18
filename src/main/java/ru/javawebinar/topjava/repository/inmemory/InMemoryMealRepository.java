package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.javawebinar.topjava.util.DateTimeUtil.isBetweenHalfOpen;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            Map<Integer, Meal> userMeals = repository.get(userId);
            if (isNull(userMeals)) {
                userMeals = new ConcurrentHashMap<>();
            }
            userMeals.put(meal.getId(), meal);
            repository.put(userId, userMeals);
            return meal;
        }
        // handle case: update, but not present in storage
        Integer mealId = meal.getId();
        Meal mealFromRepo = get(mealId, userId);
        if (nonNull(mealFromRepo)) {
            meal.setUserId(userId);
        }
        return repository.get(userId).computeIfPresent(mealId, (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal mealFromRepo = get(id, userId);
        return nonNull(mealFromRepo) && repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(userId).get(id);
        return nonNull(meal) ? meal : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return filterByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getAllByDateTime(int userId, LocalDateTime start, LocalDateTime end) {
        return filterByPredicate(userId, meal -> isBetweenHalfOpen(meal.getDateTime(), start, end)
                && isBetweenHalfOpen(meal.getTime(), start.toLocalTime(), end.toLocalTime()));
    }

    private List<Meal> filterByPredicate(int userId, Predicate<Meal> filter) {
        return repository.get(userId).values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

