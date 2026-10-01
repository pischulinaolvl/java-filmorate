package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.FriendDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.friend.FriendRepository;
import ru.yandex.practicum.filmorate.repository.user.UserMapper;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserRepository userRepository, FriendRepository friendRepository) {
        this.userRepository = userRepository;
        this.friendRepository = friendRepository;
    }

    public void addFriend(Long userId, Long friendId, Logger log) {
        log.info("Пользователь id = {} хочет добавить друга с id = {}", userId, friendId);
        User user = userRepository.findUserById(userId);
        User friend = userRepository.findUserById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }

        Map<Long, String> friends = friendRepository.getFriends(userId, false);
        if (!friends.containsKey(friendId)) {
            Map<Long, String> friends2 = friendRepository.getFriends(friendId, false);
            if (friends2.containsKey(userId)) {
                if (!"Confirmed".equals(friends2.get(userId))) {
                    friendRepository.updateFriendship(friendId, userId, "Confirmed");
                }
                friendRepository.createFriendship(userId, friendId, "Confirmed");
            } else {
                friendRepository.createFriendship(userId, friendId, "Pending");
            }
        }
    }

    public List<FriendDto> getFriends(Long userId, Logger log) {
        log.info("Вывод списка друзей для пользователя с id = {}", userId);
        if (userId == null || userId <= 0) {
            throw new ConditionsNotMetException("Пользователь должен быть заполнен");
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        Map<Long, String> friends = friendRepository.getFriends(userId, false);

        return friends.entrySet().stream()
                .map(entry -> {
                    Long friendId = entry.getKey();
                    String friendName = entry.getValue();
                    return new FriendDto(friendId, friendName);
                })
                .collect(Collectors.toList());
    }

    public List<User> getUsersByID(Set<Long> userIds, Logger log) {
        log.info("Вывод списка друзей для пользователя с id = {}", userIds.toString());
        List<User> users = new ArrayList<>();
        if (userIds != null) {
            for (Long id : userIds) {
                User user = userRepository.findUserById(id);
                if (user != null) {
                    users.add(user);
                } else {
                    log.warn("Пользователь с ID {} не найден", id);
                }
            }
        }
        return users;
    }

    public void removeFriend(Long userId, Long friendId, Logger log) {
        log.info("Пользователь id = {} хочет удалить друга с id = {}", userId, friendId);
        User user = userRepository.findUserById(userId);
        User friend = userRepository.findUserById(friendId);

        if (user == null || friend == null) {
            throw new NotFoundException("Пользователь или друг не найдены");
        }

        Map<Long, String> friends = friendRepository.getFriends(userId, false);
        if (friends == null) {
            log.warn("У пользователя с ID {} нет друзей", userId);
        } else if (friends.containsKey(friendId)) {
            friendRepository.removeFriendship(userId, friendId);
        } else {
            return;
        }
    }

    public List<UserDto>  getCommonFriends(Long userId1, Long userId2, Logger log) {
        log.info("Вывод общих друзей для пользователей id = {} и id = {}", userId1, userId2);
        User user1 = userRepository.findUserById(userId1);
        User user2 = userRepository.findUserById(userId2);

        if (user1 == null || user2 == null) {
            throw new IllegalArgumentException("Один из пользователей не найден");
        }

        Map<Long, String>  user1Friends = friendRepository.getFriends(userId1, false);
        Map<Long, String>  user2Friends = friendRepository.getFriends(userId2, false);

        Set<Long> commonFriends = new HashSet<>(user1Friends.keySet());
        commonFriends.retainAll(user2Friends.keySet());
        List<User> commonUsers = getUsersByID(commonFriends, log);
        return commonUsers.stream()
                .map(u -> new UserDto(u.getId(),u.getEmail(), u.getLogin(), u.getName(), u.getBirthday())) // заполни поля DTO
        .collect(Collectors.toList());
    }

    public Collection<User> getUsers(Logger log) {
        log.info("Вывод всех пользователей");
        return userRepository.getUsers().values();
    }

    public User create(User user, Logger log) {
        log.info("Создание пользователя Name = {}", user.getName());
        return userRepository.createUser(user);
    }

    public User update(User user, Logger log) {
        log.info("Изменение пользователя Name = {}", user.getName());
        return userRepository.updateUser(user);
    }

    public User findUserById(Long id, Logger log) {
        return userRepository.findUserById(id);
    }

    public UserDto createUser(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        User alreadyExistUser = userRepository.findUserByLogin(request.getLogin());
        if (alreadyExistUser != null) {
            throw new ConditionsNotMetException("Данный логин уже используется");
        }

        User user = UserMapper.mapToUser(request);

        user = userRepository.createUser(user);

        return UserMapper.mapToUserDto(user);
    }

    public UserDto getUserById(long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден с ID: " + userId);
        }

        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = userRepository.findUserById(request.getId());
        if (updatedUser == null) {
            throw new NotFoundException("Пользователь не найден с ID: " + request.getId());
        }
        updatedUser = UserMapper.updateUserFields(updatedUser, request);
        updatedUser = userRepository.updateUser(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

}
