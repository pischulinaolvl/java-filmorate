package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private Long id; //уникальный идентификатор пользователя,
    private String email; // электронная почта пользователя,
    private String login; // логин пользователя,
    private String name; // имя пользователя,
    private LocalDate birthday; //дата рождения

    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }
}