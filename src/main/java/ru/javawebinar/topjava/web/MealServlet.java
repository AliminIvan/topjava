package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.InMemoryMealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {

    public static final Logger log = getLogger(MealServlet.class);

    private final InMemoryMealRepository mealRepository = new InMemoryMealRepository();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.debug("getting all meals from mealRepository");
        List<Meal> allMeals = mealRepository.getAllMeals();
        log.debug("transform meals to MealTo objects");
        List<MealTo> mealToList =
                MealsUtil.filteredByStreams(
                        allMeals, LocalTime.MIN, LocalTime.MAX, InMemoryMealRepository.CALORIES_PER_DAY
                );
        req.setAttribute("formatter", formatter);
        req.setAttribute("meals", mealToList);
        log.debug("forward to /meals.jsp");
        req.getRequestDispatcher("/meals.jsp").forward(req, resp);
    }
}
