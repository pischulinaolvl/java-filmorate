package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MpaaRating {
    Long id; //уникальный идентификатор,
    String name;
    String description; // описание
}

