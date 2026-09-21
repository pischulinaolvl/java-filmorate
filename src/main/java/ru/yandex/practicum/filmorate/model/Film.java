package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

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
    Long duration; // продолжительность фильма
    MpaaRating mpa;
    List<Genre> genres;
}
