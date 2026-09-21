package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserRowMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("UserDbStorage")
@Repository
public class UserDbStorage implements UserStorage {

    private final LocalDate minDate = LocalDate.of(1895, 12, 28);
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        // 1. Вставляем данные в таблицу "user"
        // ID не вставляем, он генерируется базой данных автоматически
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

        // 2. Сразу делаем SELECT, чтобы получить объект с реальным ID, который присвоила база
        String selectSql = "SELECT * FROM \"user\" WHERE email = ?";

        // Используем твой кастомный маппер вместо BeanPropertyRowMapper
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
        // Твой маппер корректно превратит строку БД в объект User

        try {
            // Пытаемся получить объект
            return jdbcTemplate.queryForObject(sql, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            // Если данных нет, ловим ошибку и возвращаем null
            return null;
        }
    }

    @Override
    public Map<Long, User> getUsers() {
        String sql = "SELECT * FROM \"user\"";

        // Получаем список пользователей через маппер
        List<User> usersList = jdbcTemplate.query(sql, new UserRowMapper());

        // Превращаем список в Map<ID, User> для удобного поиска
        return usersList.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
    }

    @Override
    public void removeUser(Long id) {
        String sql = "DELETE FROM \"user\" WHERE id = ?";

        int rowsAffected = jdbcTemplate.update(sql, id);
    }
}

