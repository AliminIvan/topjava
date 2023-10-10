package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.InMemoryMealRepositoryImpl;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.javawebinar.topjava.util.Constants.*;

public class MealServlet extends HttpServlet {
    public static final Logger log = getLogger(MealServlet.class);
    private final MealRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MealServlet() {
        super();
        repository = new InMemoryMealRepositoryImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (nonNull(action) && action.equalsIgnoreCase("delete")) {
            int mealId = Integer.parseInt(req.getParameter("mealId"));
            log.debug("deleting meal with id: {}", mealId);
            repository.delete(mealId);
        } else if (nonNull(action) && action.equalsIgnoreCase("update")) {
            int mealId = Integer.parseInt(req.getParameter("mealId"));
            log.debug("getting meal with id: {} from mealRepository", mealId);
            Meal meal = repository.getMealById(mealId);
            log.debug("setting request attributes for update");
            req.setAttribute("mealId", meal.getId());
            req.setAttribute("mealDateTime", meal.getDateTime());
            req.setAttribute("mealDescription", meal.getDescription());
            req.setAttribute("mealCalories", meal.getCalories());
            log.debug("request forward to {}", UPDATE_MEAL_JSP);
            req.getRequestDispatcher(UPDATE_MEAL_JSP).forward(req, resp);
        } else if (nonNull(action) && action.equalsIgnoreCase("insert")) {
            log.debug("request forward to {}", ADD_MEAL_JSP);
            req.getRequestDispatcher(ADD_MEAL_JSP).forward(req, resp);
        }
        showAllMeals(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        log.debug("getting request parameters from form");
        String id = req.getParameter("id");
        LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"));
        String description = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));
        if (isNull(id)) {
            Meal meal = new Meal(dateTime, description, calories);
            log.debug("adding new Meal to mealRepository");
            repository.addMeal(meal);
        } else {
            Meal meal = repository.getMealById(Integer.parseInt(id));
            meal.setDateTime(dateTime);
            meal.setDescription(description);
            meal.setCalories(calories);
            log.debug("updating meal with id: {} in mealRepository", id);
            repository.updateMeal(meal);
        }
        showAllMeals(req, resp);
    }

    private void showAllMeals(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("getting all meals from mealRepository");
        List<Meal> allMeals = repository.getAll();
        List<MealTo> mealToList =
                MealsUtil.filteredByStreams(
                        allMeals, LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY
                );
        log.debug("setting request attributes for {}", MEALS_JSP);
        req.setAttribute("formatter", formatter);
        req.setAttribute("meals", mealToList);
        log.debug("request forward to {}", MEALS_JSP);
        req.getRequestDispatcher(MEALS_JSP).forward(req, resp);
    }
}
