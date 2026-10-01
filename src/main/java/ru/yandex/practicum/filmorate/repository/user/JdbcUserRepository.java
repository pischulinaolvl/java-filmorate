package ru.yandex.practicum.filmorate.repository.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("UserDbStorage")
@Repository
public class JdbcUserRepository implements UserRepository {

    private final LocalDate minDate = LocalDate.of(1895, 12, 28);
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper mapper = new UserRowMapper();

    public JdbcUserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO \"user\" (email, login, name, birthday) VALUES (?, ?, ?, ?)";

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Электронная почта должна быть указана");
        }
        if (!user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ConditionsNotMetException("Логин должен быть указан");
        }
        if (user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Логин не должен содержать символ пробела");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        String selectSql = "SELECT * FROM \"user\" WHERE email = ?";

        return jdbcTemplate.queryForObject(selectSql, new UserRowMapper(), user.getEmail());
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE \"user\" SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (rowsAffected == 0) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }
        return user;
    }

    @Override
    public User findUserById(Long id) {
        String sql = "SELECT * FROM \"user\" WHERE id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public User findUserByLogin(String login) {
        String sql = "SELECT * FROM \"user\" WHERE login = :login";

        try {
            Map<String, Object> params = Collections.singletonMap("login", login);
            return namedParameterJdbcTemplate.queryForObject(sql, params, new UserRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Map<Long, User> getUsers() {
        String sql = "SELECT * FROM \"user\"";

        List<User> usersList = jdbcTemplate.query(sql, new UserRowMapper());

        return usersList.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
    }

    @Override
    public void removeUser(Long id) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql, id);
    }

    public List<User> findAll() {
        String query = "SELECT * FROM \"user\"";
        return namedParameterJdbcTemplate.query(query, mapper);
    }
}

