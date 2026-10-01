package ru.yandex.practicum.filmorate.repository.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreRepository {

    Genre getGenreById(Long id);

    List<Genre> getAllGenres();

    Genre createGenre(Genre genre);

    Genre updateGenre(Genre genre);

    void deleteGenre(Long id);
}