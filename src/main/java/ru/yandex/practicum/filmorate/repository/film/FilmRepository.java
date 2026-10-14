package ru.yandex.practicum.filmorate.repository.film;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Map;

public interface FilmRepository {
    Film createFilm(Film film);

    int removeFilm(Long id);

    Film updateFilm(Film film);

    Film findFilmById(Long id);

    List<Film> getFilmsByIds(List<Long> filmIds);

    Map<Long, Film> getFilms();

    List<Film> getPopularFilms(int count, Long genreId, Integer year);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

    List<Film> search(String query, boolean byTitle, boolean byDirector);
}
