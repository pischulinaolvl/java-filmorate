package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.film.FilmMapper;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.genre.GenreRepository;
import ru.yandex.practicum.filmorate.repository.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.repository.like.LikeRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final LikeRepository likeRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmRepository filmRepository,
                       @Qualifier("UserDbStorage") UserRepository userRepository,
                       MpaRepository mpaRepository,
                       LikeRepository likeRepository,
                       GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.mpaRepository = mpaRepository;
        this.likeRepository = likeRepository;
        this.genreRepository = genreRepository;
    }

    public String addLike(Long filmId, Long userId, Logger log) {
        log.info("Попытка поставить лайк: фильм={}, пользователь={}", filmId, userId);

        Film film = filmRepository.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        likeRepository.putLike(filmId, userId);

        log.info("Лайк успешно добавлен: фильм={}, пользователь={}", filmId, userId);
        return "Лайк успешно добавлен";
    }

    public void removeLike(Long filmId, Long userId, Logger log) {
        log.info("Попытка убрать лайк: фильм={}, пользователь={}", filmId, userId);

        Film film = filmRepository.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }

        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        likeRepository.deleteLike(filmId, userId);

        log.info("Лайк успешно удален: фильм={}, пользователь={}", filmId, userId);
    }

    public List<Film> getPopularFilms(int count, Long genreId, Integer year, Logger log) {
        log.info("Получение популярных фильмов count={}, genreId={}, year={}", count, genreId, year);

        if (genreId != null) {
            genreRepository.getGenreById(genreId); // бросит NotFoundException, если нет
        }
        return filmRepository.getPopularFilms(count, genreId, year);
    }

    public List<FilmDto> getFilms(Logger log) {
        log.info("Получение всех фильмов");
        Map<Long, Film> filmsFromDb = filmRepository.getFilms();

        List<FilmDto> result = filmsFromDb.values().stream()
                .map(film -> new FilmDto(film))
                .collect(Collectors.toList());

        return result;
    }

    public FilmDto create(NewFilmRequest filmRequest, Logger log) {
        log.info("Создание нового фильма Name = {}", filmRequest.getName());
        if (filmRequest.getMpa().getId() > 0) {
            MpaaRating mpaaRating = mpaRepository.getMpaById(filmRequest.getMpa().getId());
            if (mpaaRating == null) {
                throw new NotFoundException("Не заполнен рейтинг MPAA");
            }
        } else {
            throw new NotFoundException("Не заполнен рейтинг MPAA");
        }
        Film film = FilmMapper.mapToFilm(filmRequest);
        film = filmRepository.createFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(FilmDto filmRequest, Logger log) {
        log.info("Изменение фильма Name = {}", filmRequest.getName());
        Film film = FilmMapper.mapToFilm(filmRequest);
        film = filmRepository.updateFilm(film);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto getFilmById(Long id) {
        Film film = filmRepository.findFilmById(id);

        if (film == null) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }

        return FilmMapper.mapToFilmDto(film);
    }
}
