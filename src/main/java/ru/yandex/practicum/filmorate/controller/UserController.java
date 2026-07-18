package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("return list users");
        return userService.getUsers(log);
    }


    @PostMapping
    public User create(@RequestBody User user) {
        return userService.create(user, log);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return userService.update(newUser, log);
    }

    // Получить пользователя по ID
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findUserById(id, log);
    }

    // Добавить в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {
            userService.addFriend(id, friendId, log);
            //return ResponseEntity.ok().build(); // 200
    }

    // Удалить из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<?> removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {

            try {
                userService.removeFriend(id, friendId, log);
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                log.error("XXX", e);
                throw e; // или вернуть кастомный ответ об ошибке
            }
    }

    // Получить список друзей пользователя
    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
            return userService.getFriends(id, log);
    }

    // Получить общих друзей с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
            return userService.getCommonFriends(id, otherId, log);
    }
}
