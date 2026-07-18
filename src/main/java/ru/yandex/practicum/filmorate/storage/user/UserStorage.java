package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;

public interface UserStorage {
    // Метод для добавления фильма
    User createUser(User user, Logger log);

    // Метод для удаления фильма по идентификатору
    void removeUser(Long id, Logger log);

    // Метод для обновления фильма
    User updateUser(User user, Logger log);

    // Метод для поиска фильма по идентификатору
    User findUserById(Long id, Logger log);

    Map<Long, User> getUsers(Logger log);
}
