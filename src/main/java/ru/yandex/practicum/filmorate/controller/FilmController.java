package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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
    public Collection<Film> findAll() {
        log.info("return list films");
        return filmService.getFilms(log);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        return filmService.create(film, log);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmService.update(newFilm, log);
    }

    /*@PutMapping("/{id}/like/{userId}")
    public ResponseEntity<String> addLike(@PathVariable Long id, @PathVariable Long userId) {
        try {
            filmService.addLike(id, userId, log);
            //return ResponseEntity.badRequest().body("Invalid count value");
            return ResponseEntity.badRequest().body("Лайк добавлен");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body("Ошибка определения объекта");
        } catch (ConditionsNotMetException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
        }
    }*/
    @PutMapping("/{id}/like/{userId}")
    public String addLike(@PathVariable Long id, @PathVariable Long userId) {
        return filmService.addLike(id, userId, log);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.getPopularFilms(count, log);
    }
}
