package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static java.util.Objects.isNull;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        Set<ConstraintViolation<User>> validate = validator.validate(user);
        if (!validate.isEmpty()) {
            throw new ConstraintViolationException(validate);
        }
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            saveRoles(user.id(), user.getRoles());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password,
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0 & updateRoles(user) == 0) {
            return null;
        }
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        return getWithRoles(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        return getWithRoles(users);
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        Map<Integer, Set<Role>> roles = jdbcTemplate.query("SELECT * FROM user_role", rs -> {
            Map<Integer, Set<Role>> rolesForEachUser = new HashMap<>();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                if (!rolesForEachUser.containsKey(userId)) {
                    rolesForEachUser.put(userId, EnumSet.noneOf(Role.class));
                }
                rolesForEachUser.get(userId).add(Role.valueOf(rs.getString("role")));
            }
            return rolesForEachUser;
        });
        if (!CollectionUtils.isEmpty(roles)) {
            users.forEach(user -> user.setRoles(roles.get(user.id())));
        }
        return users;
    }

    private int saveRoles(int userId, Set<Role> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return 0;
        }
        return jdbcTemplate.batchUpdate(
                "INSERT INTO user_role (user_id, role) VALUES (?,?)", new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, userId);
                        ps.setString(2, List.of(roles.toArray(new Role[0])).get(i).name());
                    }

                    @Override
                    public int getBatchSize() {
                        return roles.size();
                    }
                }).length;
    }

    private int updateRoles(User user) {
        Set<Role> newRoles = user.getRoles();
        if (CollectionUtils.isEmpty(newRoles)) {
            return 0;
        }
        jdbcTemplate.update("DELETE FROM user_role WHERE user_id=?", user.id());
        return saveRoles(user.id(), newRoles);
    }

    private User getWithRoles(List<User> users) {
        User user = DataAccessUtils.singleResult(users);
        if (isNull(user)) {
            return null;
        }
        Set<Role> roles = jdbcTemplate.query("SELECT * FROM user_role WHERE user_id=?", rs -> {
            Set<Role> roleSet = new HashSet<>();
            while (rs.next()) {
                roleSet.add(Role.valueOf(rs.getString("role")));
            }
            return roleSet;
        }, user.id());
        user.setRoles(roles);
        return user;
    }
}
