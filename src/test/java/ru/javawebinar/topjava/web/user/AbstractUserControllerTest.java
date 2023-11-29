package ru.javawebinar.topjava.web.user;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import ru.javawebinar.topjava.web.AbstractControllerTest;

import static ru.javawebinar.topjava.Profiles.DATAJPA;

public abstract class AbstractUserControllerTest extends AbstractControllerTest {

    @Autowired
    private Environment environment;

    @Test
    void getWithMeals() throws Exception {
        Assumptions.assumeTrue(environment.matchesProfiles(DATAJPA));
    }
}
