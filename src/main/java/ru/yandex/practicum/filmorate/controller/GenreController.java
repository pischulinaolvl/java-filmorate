package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    /**
     * GET /genres
     * Получить список всех жанров.
     */
    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    /**
     * GET /genres/{id}
     * Получить конкретный жанр по ID.
     * Если не найден -> сработает GlobalExceptionHandler и вернет 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Genre> getGenreById(@PathVariable Long id) {
        return ResponseEntity.ok(genreService.getGenreById(id));
    }

    /**
     * POST /genres
     * Создать новый жанр.
     * Возвращает статус 201 Created.
     */
    @PostMapping
    public ResponseEntity<Genre> createGenre(@RequestBody Genre genre) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.createGenre(genre));
    }

    /**
     * PUT /genres
     * Обновить существующий жанр (полная замена данных).
     * Ожидается, что в теле запроса есть ID.
     */
    @PutMapping
    public ResponseEntity<Genre> updateGenre(@RequestBody Genre genre) {
        return ResponseEntity.ok(genreService.updateGenre(genre));
    }

    /**
     * DELETE /genres/{id}
     * Удалить жанр по ID.
     * Возвращает статус 204 No Content (пустой ответ).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}