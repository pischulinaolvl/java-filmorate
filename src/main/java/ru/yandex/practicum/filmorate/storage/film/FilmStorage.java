package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    // Метод для добавления фильма
    Film createFilm(Film film, Logger log);

    // Метод для удаления фильма по идентификатору
    void removeFilm(Long id, Logger log);

    // Метод для обновления фильма
    Film updateFilm(Film film, Logger log);

    // Метод для поиска фильма по идентификатору
    Film findFilmById(Long id, Logger log);

    Map<Long, Film> getFilms(Logger log);
}
