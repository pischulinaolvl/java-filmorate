package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.film.FilmMapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        log.info("Запрос списка всех фильмов");
        Collection<FilmDto> result = filmService.getFilms(log);
        return result;
    }

    @GetMapping("/{id}")
    public FilmDto getFilmById(@PathVariable Long id) {
        log.info("Запрос фильма с ID: {}", id);
        return filmService.getFilmById(id);
    }

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public FilmDto create(@RequestBody NewFilmRequest filmRequest) {
        log.info("Создание нового фильма по запросу: {}", filmRequest);
        return filmService.create(filmRequest, log);
    }

    @PutMapping
    public FilmDto update(@RequestBody FilmDto filmDto) {
        log.info("Изменение фильма по запросу: {}", filmDto);
        return filmService.update(filmDto, log);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void  addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, id);
        filmService.addLike(id, userId, log);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Возвращает 204 No Content
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} убирает лайк у фильма {}", userId, id);
        filmService.removeLike(id, userId, log);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(
            @RequestParam(defaultValue = "10") Integer count) {
        log.info("Запрос популярных фильмов, count={}", count);

        List<Film> popularFilms = filmService.getPopularFilms(count, log);
        return FilmMapper.mapToFilmDtoList(popularFilms);
    }
}
