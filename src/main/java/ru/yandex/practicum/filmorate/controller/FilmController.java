package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<Collection<Film>> findAll() {
        log.info("return list films");
        Collection<Film> films = filmService.getFilms(log);

        if (films.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content — если фильмов нет
        }
        return ResponseEntity.ok(films); // 200 OK с объектом Film
    }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) {
        Film createdFilm = filmService.create(film, log);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film newFilm) {
        Film updatedFilm = filmService.update(newFilm, log);
        return ResponseEntity.ok(updatedFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<String> addLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.addLike(id, userId, log);
        return ResponseEntity.ok("Лайк добавлен");
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        List<Film> popularFilms = filmService.getPopularFilms(count, log);

        if (popularFilms.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content — если фильмов нет
        }

        return ResponseEntity.ok(popularFilms);   // 200 OK с объектом Film
    }
}
