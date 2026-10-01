package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreMapper {
    public static GenreDto mapToGenreDto(Genre entity) {
        return new GenreDto(entity.getId(), entity.getName());
    }

    public static Genre mapToGenre(GenreDto dto) {
        Genre genre = new Genre();
        genre.setId(dto.getId());      // Можно не ставить при создании, если ID автогенерируется
        genre.setName(dto.getName());
        return genre;
    }

}