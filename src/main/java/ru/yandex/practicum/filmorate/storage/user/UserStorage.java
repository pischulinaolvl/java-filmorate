package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    // Метод для добавления фильма
    User createUser(User user);

    // Метод для удаления фильма по идентификатору
    void removeUser(Long id);

    // Метод для обновления фильма
    User updateUser(User user);

    // Метод для поиска фильма по идентификатору
    User findUserById(Long id);

    Map<Long, User> getUsers();
}
