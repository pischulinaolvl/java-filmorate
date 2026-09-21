package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    // Метод для добавления фильма
    Film createFilm(Film film);

    // Метод для удаления фильма по идентификатору
    void removeFilm(Long id);

    // Метод для обновления фильма
    Film updateFilm(Film film);

    // Метод для поиска фильма по идентификатору
    Film findFilmById(Long id);

    Map<Long, Film> getFilms();

    List<Film> getPopularFilms(int count);
}
