package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage, FriendStorage friendStorage) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
    }

    // Добавление пользователя в друзья
    public void addFriend(Long userId, Long friendId, Logger log) {
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }

        Map<Long, String> friends = friendStorage.getFriends(userId, false);
        if (!friends.containsKey(friendId)) {
            Map<Long, String> friends2 = friendStorage.getFriends(friendId, false);
            if (friends2.containsKey(userId)) {
                if (!"Confirmed".equals(friends2.get(userId))) {
                    friendStorage.updateFriendship(friendId, userId, "Confirmed");
                }
                friendStorage.createFriendship(userId, friendId, "Confirmed");
            } else {
                friendStorage.createFriendship(userId, friendId, "Pending");
            }
        }
    }

    public List<Map<String, Object>> getFriends(Long userId, Logger log) {
        // Проверяем валидность ID
        if (userId == null || userId <= 0) {
            throw new ConditionsNotMetException("Пользователь должен быть заполнен");
        }

        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        // Получаем список друзей — предполагаем, что у сущности User есть метод getFriends()
        Map<Long, String> friends = friendStorage.getFriends(userId, false);
        return friends.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", entry.getKey());       // Ключ становится полем "id"
                    result.put("status", entry.getValue()); // Значение становится полем "status"
                    return result;
                })
                .collect(Collectors.toList());
    }

    public List<User> getUsersByID(Set<Long> userIds, Logger log) {
        List<User> users = new ArrayList<>();
        if (userIds != null) {
            for (Long id : userIds) {
                User user = userStorage.findUserById(id);
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
        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }

        Map<Long, String> friends = friendStorage.getFriends(userId, false);
        if (friends == null) {
            log.warn("У пользователя с ID {} нет друзей", userId);
        } else if (friends.containsKey(friendId)) {
            friendStorage.removeFriendship(userId, friendId);
        } else {
            return;//throw new NotFoundException("Пользователь или друг не найдены");
        }
    }

    // Получение списка общих друзей
    public List<User> getCommonFriends(Long userId1, Long userId2, Logger log) {
        User user1 = userStorage.findUserById(userId1);
        User user2 = userStorage.findUserById(userId2);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("Один из пользователей не найден");
        }

        Map<Long, String>  user1Friends = friendStorage.getFriends(userId1, false);
        Map<Long, String>  user2Friends = friendStorage.getFriends(userId2, false);

        // Находим пересечение множеств — общих друзей
        Set<Long> commonFriends = new HashSet<>(user1Friends.keySet());
        commonFriends.retainAll(user2Friends.keySet());

        return getUsersByID(commonFriends, log);
    }

    public Collection<User> getUsers(Logger log) {
        return userStorage.getUsers().values();
    }

    public User create(User user, Logger log) {
        return userStorage.createUser(user);
    }

    public User update(User user, Logger log) {
        return userStorage.updateUser(user);
    }

    public User findUserById(Long id, Logger log) {
        return userStorage.findUserById(id);
    }

}
