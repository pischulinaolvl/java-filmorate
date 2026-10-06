package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;

public interface FilmRepository {
    Film createFilm(Film film);

    int removeFilm(Long id);

    Film updateFilm(Film film);

    Film findFilmById(Long id);

    Map<Long, Film> getFilms();

    List<Film> getPopularFilms(int count);

    List<Film> getCommonFilms(Long userId, Long friendId);
}
