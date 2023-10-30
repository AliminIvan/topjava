package ru.javawebinar.topjava.repository.jpa;

import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.MealRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class JpaMealRepository implements MealRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            User ref = entityManager.getReference(User.class, userId);
            meal.setUser(ref);
            entityManager.persist(meal);
            return meal;
        } else {
            Query query = entityManager.createQuery("UPDATE Meal SET dateTime = :dateTime, description = :description, calories = :calories " +
                    "WHERE id = :id AND user.id = :userId");
            query.setParameter("dateTime", meal.getDateTime());
            query.setParameter("description", meal.getDescription());
            query.setParameter("calories", meal.getCalories());
            query.setParameter("id", meal.getId());
            query.setParameter("userId", userId);
            return query.executeUpdate() != 0 ? meal : null;
        }
    }

    @Override
    @Transactional
    public boolean delete(int id, int userId) {
        Query query = entityManager.createQuery("DELETE FROM Meal m WHERE m.id = :id AND m.user.id = :userId");
        query.setParameter("id", id);
        query.setParameter("userId", userId);
        return query.executeUpdate() != 0;
    }

    @Override
    public Meal get(int id, int userId) {
        TypedQuery<Meal> query = entityManager.createQuery("SELECT m FROM Meal m WHERE m.id = :id AND m.user.id = :userId", Meal.class);
        query.setParameter("id", id);
        query.setParameter("userId", userId);
        return DataAccessUtils.singleResult(query.getResultList());
    }

    @Override
    public List<Meal> getAll(int userId) {
        TypedQuery<Meal> query = entityManager.createQuery("SELECT m FROM Meal m WHERE m.user.id = :userId " +
                "ORDER BY m.dateTime DESC", Meal.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<Meal> getBetweenHalfOpen(LocalDateTime startDateTime, LocalDateTime endDateTime, int userId) {
        TypedQuery<Meal> query = entityManager.createQuery(
                "SELECT m FROM Meal m WHERE m.user.id = :userId " +
                        "AND m.dateTime >= :startDateTime " +
                        "AND m.dateTime < :endDateTime " +
                        "ORDER BY m.dateTime DESC", Meal.class);
        query.setParameter("userId", userId);
        query.setParameter("startDateTime", startDateTime);
        query.setParameter("endDateTime", endDateTime);
        return query.getResultList();
    }
}