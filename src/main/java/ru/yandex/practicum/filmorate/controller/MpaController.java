
package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<MpaaRating> getAllMpa() {
        return mpaService.getAllMpa();
    }

    @GetMapping("/{id}")
    public MpaaRating getMpaById(@PathVariable Long id) {
        return mpaService.getMpaById(id);
    }

    @PostMapping
    public MpaaRating createMpa(@RequestBody MpaaRating mpa) {
        return mpaService.createMpa(mpa);
    }

    @PutMapping
    public MpaaRating updateMpa(@RequestBody MpaaRating mpa) {
        return mpaService.updateMpa(mpa);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMpa(@PathVariable Long id) {
        mpaService.deleteMpa(id);
    }
}