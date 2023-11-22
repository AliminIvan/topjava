package ru.javawebinar.topjava.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.javawebinar.topjava.repository.JpaUtil;

public abstract class Abstract2ndLvlCacheUserServiceTest extends AbstractUserServiceTest {

    @Autowired
    private JpaUtil jpaUtil;

    @Override
    public void setup() {
        super.setup();
        jpaUtil.clear2ndLevelHibernateCache();
    }
}
