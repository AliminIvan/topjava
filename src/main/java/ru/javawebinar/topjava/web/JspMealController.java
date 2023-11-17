package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserCaloriesPerDay;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
@RequestMapping("/meals")
public class JspMealController {

    private static final Logger log = LoggerFactory.getLogger(JspMealController.class);

    @Autowired
    private MealService mealService;

    @GetMapping
    public String getMeals(Model model) {
        log.info("get all meals for user {}", authUserId());
        model.addAttribute("meals", MealsUtil.getTos(mealService.getAll(authUserId()), authUserCaloriesPerDay()));
        return "meals";
    }

    @GetMapping("/filtered")
    public String getFilteredMeals(Model model, HttpServletRequest request) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        log.info("get filtered meals with startDate:{}, endDate:{}, startTime:{}, endTime:{} for user {}",
                startDate, endDate, startTime, endTime, authUserId());
        List<Meal> mealsDateFiltered = mealService.getBetweenInclusive(startDate, endDate, authUserId());
        model.addAttribute("meals", MealsUtil.getFilteredTos(mealsDateFiltered, authUserCaloriesPerDay(), startTime, endTime));
        return "meals";
    }

    @GetMapping("/add")
    public String add(Model model) {
        log.info("create new meal for user {}", authUserId());
        model.addAttribute("meal", new Meal());
        model.addAttribute("action", "create");
        log.info("forward to mealForm");
        return "mealForm";
    }

    @GetMapping("/edit")
    public String edit(Model model, HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        log.info("get meal with id={} for user {}", id, authUserId());
        Meal meal = mealService.get(id, authUserId());
        model.addAttribute("meal", meal);
        log.info("forward to mealForm");
        return "mealForm";
    }

    @PostMapping
    public String saveOrUpdate(HttpServletRequest request) throws UnsupportedEncodingException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        String id = request.getParameter("id");
        if (StringUtils.hasLength(id)) {
            log.info("update meal with id={} for user {}", id, authUserId());
            meal.setId(Integer.parseInt(id));
            mealService.update(meal, authUserId());
        } else {
            log.info("create new meal for user {}", authUserId());
            mealService.create(meal, authUserId());
        }
        return "redirect:/meals";
    }

    @GetMapping("/delete")
    public String delete(HttpServletRequest request) throws UnsupportedEncodingException {
        int id = Integer.parseInt(request.getParameter("id"));
        log.info("delete meal with id={}", id);
        mealService.delete(id, authUserId());
        return "redirect:/meals";
    }
}
