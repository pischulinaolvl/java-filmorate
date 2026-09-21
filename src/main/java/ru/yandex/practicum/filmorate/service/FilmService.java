package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.MpaaRatingStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaaRatingStorage mpaaRatingStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage")FilmStorage filmStorage, @Qualifier("UserDbStorage") UserStorage userStorage, MpaaRatingStorage mpaaRatingStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaaRatingStorage = mpaaRatingStorage;
        this.likeStorage = likeStorage;
    }

    // Добавление лайка фильму от пользователя
    public String addLike(Long filmId, Long userId, Logger log) {
        log.info("Попытка поставить лайк: фильм={}, пользователь={}", filmId, userId);

        // 1. Проверяем существование сущностей (для понятных ошибок)
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        // 2. Делегируем логику добавления в хранилище
        // Внутри likeStorage.putLike() уже есть обработка DuplicateKeyException
        likeStorage.putLike(filmId, userId);

        log.info("Лайк успешно добавлен: фильм={}, пользователь={}", filmId, userId);
        return "Лайк успешно добавлен";
    }

    public void removeLike(Long filmId, Long userId, Logger log) {
        log.info("Попытка убрать лайк: фильм={}, пользователь={}", filmId, userId);

        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        likeStorage.deleteLike(filmId, userId);

        log.info("Лайк успешно удален: фильм={}, пользователь={}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count, Logger log) {
        return filmStorage.getPopularFilms(count);
    }

    public Collection<Film> getFilms(Logger log) {
        return filmStorage.getFilms().values();
    }

    public Film create(Film film, Logger log) {
        if (film.getMpa().getId() > 0) {
            MpaaRating mpaaRating = mpaaRatingStorage.getMpaById(film.getMpa().getId());
            if (mpaaRating == null) {
                throw new NotFoundException("Не заполнен рейтинг MPAA");
            }
        } else {
            throw new NotFoundException("Не заполнен рейтинг MPAA");
        }
        return filmStorage.createFilm(film);
    }

    public Film createFilm(Film film) {
        // 1. Простая валидация полей (null, длина строк) - это ок делать здесь.
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ConditionsNotMetException("Рейтинг MPAA обязателен");
        }

        // 2. Пытаемся сохранить.
        // Если ID не существует, база выбросит DataIntegrityViolationException.
        try {
            return filmStorage.createFilm(film);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // 3. Ловим ошибку БД и превращаем в понятную ошибку для API
            throw new NotFoundException(
                    "Не удалось создать фильм"
            );
        }
    }

    public Film update(Film film, Logger log) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long id) {
        Film film = filmStorage.findFilmById(id);

        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }

        return film;
    }
}
