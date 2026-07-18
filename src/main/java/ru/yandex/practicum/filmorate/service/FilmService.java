package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    // Добавление лайка фильму от пользователя
    public String addLike(Long filmId, Long userId, Logger log) {
        Film film = filmStorage.findFilmById(filmId, log);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        User user = userStorage.findUserById(userId, log);
        if (user == null) {
            throw new NotFoundException("Пользователб не найден");
        }

        Set<Long> likes = film.getLikes();
        if (!likes.contains(userId)) {
            likes.add(userId);
            return "Лайк успешно добавлен";
            //filmStorage.updateFilm(film);
        } else {
            throw new ConditionsNotMetException("Пользователь уже поставил лайк этому фильму");
        }
    }

    // Удаление лайка у фильма от пользователя
    public void removeLike(Long filmId, Long userId, Logger log) {
        Film film = filmStorage.findFilmById(filmId, log);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }

        Set<Long> likes = film.getLikes();
        likes.remove(userId);
    }

    public List<Film> getPopularFilms(int count, Logger log) {
        return filmStorage.getFilms(log).values().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Film> getFilms(Logger log) {
        return filmStorage.getFilms(log).values();
    }

    public Film create(Film film, Logger log) {
        return filmStorage.createFilm(film, log);
    }

    public Film update(Film film, Logger log) {
        return filmStorage.updateFilm(film, log);
    }
}
