package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // Добавление пользователя в друзья
    public void addFriend(Long userId, Long friendId, Logger log) {
        User user = userStorage.findUserById(userId, log);
        User friend = userStorage.findUserById(friendId, log);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }

        Set<Long> friends = user.getFriends();
        if (friends == null) {
            friends = new HashSet<>(); // создаём новый пустой Set
            friends.add(friendId);
            user.setFriends(friends);
        } else if (!friends.contains(friendId)) {
            friends.add(friendId);
            userStorage.updateUser(user, log);
        }

        friends = friend.getFriends();
        if (friends == null) {
            friends = new HashSet<>(); // создаём новый пустой Set
            friends.add(userId);
            friend.setFriends(friends);
        } else if (!friends.contains(userId)) {
            friends.add(userId);
            userStorage.updateUser(friend, log);
        }
    }

    public List<User> getFriends(Long userId, Logger log) {
        // Проверяем валидность ID
        if (userId == null || userId <= 0) {
            throw new ConditionsNotMetException("Пользователь должен быть заполнен");
        }

        User user = userStorage.findUserById(userId, log);

        if (user == null) {
            throw new NotFoundException("Пользователь не найдены");
        }

        // Получаем список друзей — предполагаем, что у сущности User есть метод getFriends()
        Set<Long> userIds = user.getFriends();
        return getUsersByID(userIds, log);
    }

    public List<User> getUsersByID(Set<Long> userIds, Logger log) {
        List<User> users = new ArrayList<>();
        if (userIds != null) {
            for (Long id : userIds) {
                User user = userStorage.findUserById(id, log);
                if (user != null) {
                    users.add(user);
                } else {
                    log.warn("Пользователь с ID {} не найден", id);
                }
            }
        }
        return users;
    }

    // Удаление пользователя из друзей
    public void removeFriend(Long userId, Long friendId, Logger log) {
        User user = userStorage.findUserById(userId, log);
        User friend = userStorage.findUserById(friendId, log);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }

        Set<Long> friends = user.getFriends();
        if (friends == null) {
            log.warn("У пользователя с ID {} нет друзей", userId);
        } else if (friends.contains(friendId)) {
            friends.remove(friendId);
        } else {
            throw new NotFoundException("Пользователь или друг не найдены");
        }

        friends = friend.getFriends();
        if (friends == null) {
            log.warn("У пользователя с ID {} нет друзей", friendId);
        } else if (friends.contains(userId)) {
            friends.remove(userId);
        } else {
            throw new NotFoundException("Пользователь или друг не найдены");
        }
    }

    // Получение списка общих друзей
    public List<User> getCommonFriends(Long userId1, Long userId2, Logger log) {
        User user1 = userStorage.findUserById(userId1, log);
        User user2 = userStorage.findUserById(userId2, log);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("Один из пользователей не найден");
        }

        Set<Long> user1Friends = user1.getFriends();
        Set<Long> user2Friends = user2.getFriends();

        // Находим пересечение множеств — общих друзей
        Set<Long> commonFriends = new HashSet<>(user1Friends);
        commonFriends.retainAll(user2Friends);

        return getUsersByID(commonFriends, log);
    }

    public Collection<User> getUsers(Logger log){
        return userStorage.getUsers(log).values();
    }

    public User create(User user, Logger log) {
        return userStorage.createUser(user, log);
    }

    public User update(User user, Logger log) {
        return userStorage.updateUser(user, log);
    }

    public User findUserById(Long id, Logger log){
        return userStorage.findUserById(id, log);
    }

}
