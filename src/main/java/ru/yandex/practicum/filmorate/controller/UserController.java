package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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
    public ResponseEntity<Collection<User>> findAll() {
        log.info("return list users");
        Collection<User> users = userService.getUsers(log);

        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content — если фильмов нет
        }
        return ResponseEntity.ok(users); // 200 OK с объектом Film
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User newUser = userService.create(user, log);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PutMapping
    public ResponseEntity<User> update(@RequestBody User newUser) {
        User updatedUser = userService.update(newUser, log);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findUserById(id, log);
        if (user == null) {
            return ResponseEntity.notFound().build(); // 404, если пользователя нет
        }
        return ResponseEntity.ok(user); // 200 OK с данными пользователя
    }

    // Добавить в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId) {
        userService.addFriend(id, friendId, log);
        return ResponseEntity.ok().build(); // 200 OK — операция выполнена успешно
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
    public ResponseEntity<List<User>> getFriends(@PathVariable Long id) {
        List<User> friends = userService.getFriends(id, log);
        return ResponseEntity.ok(friends);
    }


    // Получить общих друзей с другим пользователем
    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(
            @PathVariable Long id,
            @PathVariable Long otherId) {
        List<User> commonFriends = userService.getCommonFriends(id, otherId, log);
        return ResponseEntity.ok(commonFriends);
    }
}
