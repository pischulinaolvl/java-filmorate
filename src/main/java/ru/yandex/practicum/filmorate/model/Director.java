package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Director {
    Long id; //уникальный идентификатор,
    String name; // фамилия/имя/псевдоним
}
