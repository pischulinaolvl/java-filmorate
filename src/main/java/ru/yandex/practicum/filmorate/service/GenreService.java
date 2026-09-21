package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    /**
     * Получить жанр по ID.
     */
    public Genre getGenreById(Long id) {
        return genreStorage.getGenreById(id);
    }

    /**
     * Получить все жанры.
     */
    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    /**
     * Создать новый жанр.
     */
    public Genre createGenre(Genre genre) {
        return genreStorage.createGenre(genre);
    }

    /**
     * Обновить существующий жанр.
     */
    public Genre updateGenre(Genre genre) {
        return genreStorage.updateGenre(genre);
    }

    /**
     * Удалить жанр.
     */
    public void deleteGenre(Long id) {
        genreStorage.deleteGenre(id);
    }
}