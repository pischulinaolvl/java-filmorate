package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Director {
    Long id; //уникальный идентификатор,
    String name; // фамилия/имя/псевдоним
}
