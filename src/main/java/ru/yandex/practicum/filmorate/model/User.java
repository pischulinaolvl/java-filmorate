package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode
public class User {
    Long id; //уникальный идентификатор пользователя,
    String email; // электронная почта пользователя,
    String login; // логин пользователя,
    String name; // имя пользователя,
    LocalDate birthday; //дата рождения
}
