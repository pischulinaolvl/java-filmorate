package ru.yandex.practicum.filmorate.repository.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    User createUser(User user);

    void removeUser(Long id);

    User updateUser(User user);

    User findUserById(Long id);

    User findUserByLogin(String login);

    Map<Long, User> getUsers();

    List<User> findAll();
}
