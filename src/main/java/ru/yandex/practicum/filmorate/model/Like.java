package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Like {
    Long userid; //уникальный идентификатор,
    Long filmid;
}