package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate minDate = LocalDate.of(1895, 12, 28);

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("return list films");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("WARN Film Create Название фильма не может быть пустым");
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("WARN Film Create Описание не может быть длиннее 200 символов");
            throw new ConditionsNotMetException("Описание не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(minDate)) {
            log.warn("WARN Film Create Дата релиза не может быть раньше, чем 28 декабря 1895");
            throw new ConditionsNotMetException("Дата релиза не может быть раньше, чем 28 декабря 1895");
        }
        if (!(film.getDuration() > 0)) {
            log.warn("WARN Film Create Продолжительность фильма должна быть положительныи числом");
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительныи числом");
        }
        // формируем дополнительные данные
        log.trace("update field id (use getNextId)");
        film.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        log.info("create new film");
        films.put(film.getId(), film);
        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            log.warn("WARN Film Update Id должен быть указан");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.warn("WARN Film Update Название фильма не может быть пустым");
                throw new ConditionsNotMetException("Название фильма не может быть пустым");
            }
            if (newFilm.getDescription().length() > 200) {
                log.warn("WARN Film Update Описание не может быть длиннее 200 символов");
                throw new ConditionsNotMetException("Описание не может быть длиннее 200 символов");
            }
            if (newFilm.getReleaseDate().isBefore(minDate)) {
                log.warn("WARN Film Update Дата релиза не может быть раньше, чем 28 декабря 1895");
                throw new ConditionsNotMetException("Дата релиза не может быть раньше, чем 28 декабря 1895");
            }
            if (!(newFilm.getDuration() > 0)) {
                log.warn("WARN Film Update Продолжительность фильма должна быть положительныи числом");
                throw new ConditionsNotMetException("Продолжительность фильма должна быть положительныи числом");
            }

            log.trace("Film Update Поиск фильма по полю id");
            Film oldFilm = films.get(newFilm.getId());

            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            log.debug("Обновление поля name. OldValue {}. NewValue {}", oldFilm.getName(), newFilm.getName());
            oldFilm.setName(newFilm.getName());
            log.debug("Обновление поля description. OldValue {}. NewValue {}", oldFilm.getDescription(), newFilm.getDescription());
            oldFilm.setDescription(newFilm.getDescription());
            log.debug("Обновление поля releaseDate. OldValue {}. NewValue {}", oldFilm.getReleaseDate(), newFilm.getReleaseDate());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.debug("Обновление поля duration. OldValue {}. NewValue {}", oldFilm.getDuration(), newFilm.getDuration());
            oldFilm.setDuration(newFilm.getDuration());

            log.info("update film");
            return oldFilm;
        }

        log.warn("WARN Film Update Фильм с id = {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }
}
