package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@EqualsAndHashCode
public class Film {
    Long id; //уникальный идентификатор,
    String name; //название
    String description; // описание
    LocalDate releaseDate; // дата релиза
    int duration; // продолжительность фильма
    Set<Long> likes = new HashSet<>();
}
