package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {
    // Хранилище пользователей: ключ — ID пользователя, значение — объект User
    private final Map<Long, User> users = new HashMap<>();

    // Счётчик для генерации уникальных ID
    private long nextId = 1L;

    @Override
    public User createUser(User user) {
        // Если у пользователя ещё нет ID, присваиваем новый
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
        // формируем дополнительные данные
        user.setId(getNextId());

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void removeUser(Long id) {
        users.remove(id);
    }

    @Override
    public User updateUser(User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                throw new ConditionsNotMetException("Электронная почта должна быть указана");
            }
            if (!newUser.getEmail().contains("@")) {
                throw new ConditionsNotMetException("Электронная почта должна содержать символ @");
            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
                throw new ConditionsNotMetException("Логин должен быть указан");
            }
            if (newUser.getLogin().contains(" ")) {
                throw new ConditionsNotMetException("Логин не должен содержать символ пробела");
            }
            if (newUser.getBirthday().isAfter(LocalDate.now())) {
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            }

            User oldUser = users.get(newUser.getId());

            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            oldUser.setBirthday(newUser.getBirthday());

            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public User findUserById(Long id) {
        return users.get(id); // Возвращает null, если пользователь не найден
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
