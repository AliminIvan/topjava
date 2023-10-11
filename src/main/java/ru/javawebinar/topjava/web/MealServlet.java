package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.InMemoryMealRepository;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;
import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    private static final String MEALS_JSP = "/meals.jsp";
    private static final String EDIT_JSP = "/editMeal.jsp";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int CALORIES_PER_DAY = 2000;
    private MealRepository repository;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        repository = new InMemoryMealRepository();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path;
        log.debug("getting action parameter from request");
        String action = String.valueOf(req.getParameter("action"));
        log.debug("action parameter: {}", action);
        switch (action) {
            case "delete":
                log.debug("getting id parameter from request");
                int deleteId = Integer.parseInt(req.getParameter("id"));
                log.debug("deleting meal object with id: {} from repository", deleteId);
                repository.delete(deleteId);
                path = req.getRequestURI();
                log.debug("redirecting to: {}", path);
                resp.sendRedirect(path);
                break;
            case "edit":
                log.debug("getting id parameter from request");
                int editId = Integer.parseInt(req.getParameter("id"));
                log.debug("getting meal object with id: {} from repository for update", editId);
                Meal updatableMeal = repository.getById(editId);
                log.debug("setting updatable meal with id: {} as a request attribute for {}", updatableMeal.getId(), EDIT_JSP);
                req.setAttribute("meal", updatableMeal);
                path = EDIT_JSP;
                log.debug("request forward to {}", EDIT_JSP);
                forward(req, resp, path);
                break;
            case "insert":
                Meal newMeal = new Meal();
                log.debug("setting new empty meal object as a request attribute for {}", EDIT_JSP);
                req.setAttribute("meal", newMeal);
                path = EDIT_JSP;
                log.debug("request forward to {}", EDIT_JSP);
                forward(req, resp, path);
                break;
            default:
                path = MEALS_JSP;
                log.debug("setting request attributes for {}", MEALS_JSP);
                req.setAttribute("formatter", FORMATTER);
                req.setAttribute("meals", MealsUtil.filteredByStreams(repository.getAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY));
                log.debug("request forward to {}", MEALS_JSP);
                forward(req, resp, path);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        Meal meal = new Meal();
        log.debug("getting request parameters from form for add/update");
        String id = req.getParameter("id");
        meal.setDateTime(LocalDateTime.parse(req.getParameter("dateTime")));
        meal.setDescription(req.getParameter("description"));
        meal.setCalories(Integer.parseInt(req.getParameter("calories")));
        if (isNull(id) || id.isEmpty()) {
            log.debug("adding new meal object to repository");
            Meal addedMeal = repository.add(meal);
            log.debug("new meal with id: {} added to repository", addedMeal.getId());
        } else {
            meal.setId(Integer.parseInt(id));
            log.debug("updating meal object with id: {}", meal.getId());
            repository.update(meal);
        }
        resp.sendRedirect(req.getRequestURI());
    }

    private static void forward(HttpServletRequest req, HttpServletResponse resp, String path) throws ServletException, IOException {
        RequestDispatcher view = req.getRequestDispatcher(path);
        view.forward(req, resp);
    }
}
