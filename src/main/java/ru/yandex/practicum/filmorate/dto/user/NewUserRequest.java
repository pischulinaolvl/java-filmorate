package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    private String email; // электронная почта пользователя,
    private String login; // логин пользователя,
    private String name; // имя пользователя,
    private LocalDate birthday; //дата рождения
}