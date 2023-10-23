package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final int NOT_FOUND = 10;
    public static final int USER_MEAL_ID = START_SEQ + 3;

    public static final Meal expectedUserMeal = new Meal(USER_MEAL_ID, LocalDateTime.of(2023, 10, 21, 10, 0), "Завтрак User", 500);
    public static final Meal userMeal1 = new Meal(USER_MEAL_ID + 1, LocalDateTime.of(2023, 10, 21, 14, 0), "Обед User", 900);
    public static final Meal userMeal2 = new Meal(USER_MEAL_ID + 2, LocalDateTime.of(2023, 10, 21, 19, 0), "Ужин User", 800);
    public static final Meal userMeal3 = new Meal(USER_MEAL_ID + 6, LocalDateTime.of(2023, 10, 23, 9, 30), "Завтрак User", 350);
    public static final Meal userMeal4 = new Meal(USER_MEAL_ID + 7, LocalDateTime.of(2023, 10, 23, 13, 45), "Обед User", 1000);
    public static final Meal userMeal5 = new Meal(USER_MEAL_ID + 8, LocalDateTime.of(2023, 10, 23, 19, 0), "Ужин User", 600);

    public static final LocalDate LOCAL_DATE = expectedUserMeal.getDate();
    public static final LocalDateTime LOCAL_DATE_TIME = expectedUserMeal.getDateTime();
    public static final LocalDateTime MIN_DATE = LocalDateTime.of(1, 1, 1, 0, 0);
    public static final LocalDateTime MAX_DATE = LocalDateTime.of(3000, 1, 1, 0, 0);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2023, 10, 23, 17, 0), "новый прием пищи", 400);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(expectedUserMeal);
        updated.setDateTime(LocalDateTime.of(2023, 10, 23, 14, 0));
        updated.setDescription("Обед User");
        updated.setCalories(800);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
