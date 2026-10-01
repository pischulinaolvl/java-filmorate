package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id; //уникальный идентификатор пользователя,
    private String email; // электронная почта пользователя,
    private String login; // логин пользователя,
    private String name; // имя пользователя,
    private LocalDate birthday; //дата рождения
}