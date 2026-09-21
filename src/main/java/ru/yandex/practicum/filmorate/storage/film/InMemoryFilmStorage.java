package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    // Хранилище фильмов: ключ — ID фильма, значение — объект Film
    private final Map<Long, Film> films = new HashMap<>();

    private final LocalDate minDate = LocalDate.of(1895, 12, 28);
    // Счётчик для генерации уникальных ID
    private long nextId = 1L;

    @Override
    public Film createFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Описание не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше, чем 28 декабря 1895");
        }
        if (!(film.getDuration() > 0)) {
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительныи числом");
        }

        // Если у фильма ещё нет ID, присваиваем новый
        if (film.getId() == null) {
            film.setId(nextId++);
        }

        films.put(film.getId(), film);
        return  film;
    }

    @Override
    public void removeFilm(Long id) {
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        // проверяем необходимые условия
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                throw new ConditionsNotMetException("Название фильма не может быть пустым");
            }
            if (newFilm.getDescription().length() > 200) {
                throw new ConditionsNotMetException("Описание не может быть длиннее 200 символов");
            }
            if (newFilm.getReleaseDate().isBefore(minDate)) {
                throw new ConditionsNotMetException("Дата релиза не может быть раньше, чем 28 декабря 1895");
            }
            if (!(newFilm.getDuration() > 0)) {
                throw new ConditionsNotMetException("Продолжительность фильма должна быть положительныи числом");
            }

            Film oldFilm = films.get(newFilm.getId());

            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            return oldFilm;
        } else {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
    }

    @Override
    public Film findFilmById(Long id) {
        return films.get(id); // Возвращает null, если фильм не найден
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return null;
    }
}
