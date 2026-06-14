package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        log.info("return list users");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("WARN User Create Электронная почта должна быть указана");
            throw new ConditionsNotMetException("Электронная почта должна быть указана");
        }
        if (!user.getEmail().contains("@")) {
            log.warn("WARN User Create Электронная почта должна содержать символ @");
            throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("WARN User Create Логин должен быть указан");
            throw new ConditionsNotMetException("Логин должен быть указан");
        }
        if (user.getLogin().contains(" ")) {
            log.warn("WARN User Create Логин не должен содержать символ пробела");
            throw new ConditionsNotMetException("Логин не должен содержать символ пробела");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("WARN User Create Дата рождения не может быть в будущем");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
        // формируем дополнительные данные
        log.trace("update field id (use getNextId)");
        user.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        log.info("create new user");
        users.put(user.getId(), user);
        return user;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            log.warn("WARN User Update Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                log.warn("WARN User Update Электронная почта должна быть указана");
                throw new ConditionsNotMetException("Электронная почта должна быть указана");
            }
            if (!newUser.getEmail().contains("@")) {
                log.warn("WARN User Update Электронная почта должна содержать символ @");
                throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
                log.warn("WARN User Update Логин должен быть указан");
                throw new ConditionsNotMetException("Логин должен быть указан");
            }
            if (newUser.getLogin().contains(" ")) {
                log.warn("WARN User Update Логин не должен содержать символ пробела");
                throw new ConditionsNotMetException("Логин не должен содержать символ пробела");
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                log.warn("WARN User Update Дата рождения не может быть в будущем");
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            }

            log.trace("Film Update Поиск фильма по полю id");
            User oldUser = users.get(newUser.getId());

            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            log.debug("Обновление поля email. OldValue {}. NewValue {}", oldUser.getEmail(), newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
            log.debug("Обновление поля login. OldValue {}. NewValue {}", oldUser.getLogin(), newUser.getLogin());
            oldUser.setLogin(newUser.getLogin());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                log.debug("Обновление поля name. OldValue {}. NewValue {}", oldUser.getName(), newUser.getLogin());
                oldUser.setName(newUser.getLogin());
            } else {
                log.debug("Обновление поля name. OldValue {}. NewValue {}", oldUser.getName(), newUser.getName());
                oldUser.setName(newUser.getName());
            }
            log.debug("Обновление поля birthday. OldValue {}. NewValue {}", oldUser.getBirthday(), newUser.getBirthday());
            oldUser.setBirthday(newUser.getBirthday());

            log.info("update user");

            return oldUser;
        }
        log.warn("WARN User Update Фильм с id = {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }
}
