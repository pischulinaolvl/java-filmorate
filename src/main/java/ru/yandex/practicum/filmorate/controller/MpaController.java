
package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    /**
     * GET /mpa - получить все рейтинги
     */
    @GetMapping
    public ResponseEntity<List<MpaaRating>> getAllMpa() {
        return ResponseEntity.ok(mpaService.getAllMpa());
    }

    /**
     * GET /mpa/{id} - получить рейтинг по ID
     * Если не найден -> вернет 404 благодаря обработчику исключений
     */
    @GetMapping("/{id}")
    public ResponseEntity<MpaaRating> getMpaById(@PathVariable Long id) {
        return ResponseEntity.ok(mpaService.getMpaById(id));
    }

    /**
     * POST /mpa - создать новый рейтинг
     */
    @PostMapping
    public ResponseEntity<MpaaRating> createMpa(@RequestBody MpaaRating mpa) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mpaService.createMpa(mpa));
    }

    /**
     * PUT /mpa - обновить рейтинг (полная замена)
     */
    @PutMapping
    public ResponseEntity<MpaaRating> updateMpa(@RequestBody MpaaRating mpa) {
        return ResponseEntity.ok(mpaService.updateMpa(mpa));
    }

    /**
     * DELETE /mpa/{id} - удалить рейтинг
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMpa(@PathVariable Long id) {
        mpaService.deleteMpa(id);
        return ResponseEntity.noContent().build(); // Возвращает статус 204 No Content
    }
}