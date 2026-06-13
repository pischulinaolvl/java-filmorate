package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
@EqualsAndHashCode
//@Builder
public class Film {
    Long id; //уникальный идентификатор,
    String name; //название
    String description; // описание
    LocalDate releaseDate; // дата релиза
    int duration; // продолжительность фильма
}
