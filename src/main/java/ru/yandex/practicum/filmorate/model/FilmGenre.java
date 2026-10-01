package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FilmGenre {
    Long filmid; //уникальный идентификатор,
    Long genreid;
}