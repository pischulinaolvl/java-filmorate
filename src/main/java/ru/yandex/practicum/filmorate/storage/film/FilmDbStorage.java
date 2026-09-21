package ru.yandex.practicum.filmorate.storage.film;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("FilmDbStorage")
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма обязательно");
        }
        if (film.getDescription().length() > 100) {
            throw new ConditionsNotMetException("Описание фильма слишком длинное");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата выхода не может быть в будущем");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ConditionsNotMetException("Длительность должна быть положительной");
        }
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ConditionsNotMetException("Рейтинг MPAA обязателен и должен иметь ID");
        }

        String insertFilmSql = "INSERT INTO film (name, description, release_date, duration, mpaa_rating_id) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertFilmSql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        String selectIdSql = "SELECT id FROM film WHERE name = ? AND release_date = ?";
        Long newFilmId = jdbcTemplate.queryForObject(selectIdSql, Long.class, film.getName(), film.getReleaseDate());

        if (newFilmId == null) {
            throw new NotFoundException("Не удалось получить ID созданного фильма");
        }

        film.setId(newFilmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertGenreLinkSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

            // 1. Логирование для отладки (увидишь в консоли при запуске тестов)
            System.out.println("=== Попытка связать фильм ID: " + newFilmId + " с " + film.getGenres().size() + " жанрами ===");

            for (Genre genre : film.getGenres()) {
                // 2. Жесткая проверка на null внутри цикла
                if (genre == null) {
                    System.out.println("Предупреждение: Пропущен null-жанр в списке.");
                    continue;
                }

                Long genreId = genre.getId();
                if (genreId == null) {
                    System.out.println("Предупреждение: Пропущен жанр без ID.");
                    continue;
                }

                try {
                    // 3. Попытка вставки
                    int rowsAffected = jdbcTemplate.update(insertGenreLinkSql, newFilmId, genreId);
                    System.out.println("Успешно добавлено: film_id=" + newFilmId + ", genre_id=" + genreId + " (затронуто строк: " + rowsAffected + ")");

                } catch (org.springframework.dao.DuplicateKeyException e) {
                    // Случай А: Такая связь уже есть (база очищена не полностью)
                    System.out.println("Инфо: Связь уже существует (film_id=" + newFilmId + ", genre_id=" + genreId + "). Пропускаем.");
                    // Мы НЕ выбрасываем ошибку, просто идем дальше. Фильм создан, связь есть.
                }
                catch (org.springframework.dao.DataIntegrityViolationException e) {
                    // Случай Б: Такого жанра вообще нет в таблице genre (ошибка FK)
                    throw new NotFoundException(
                            "Не удалось связать фильм с жанром ID: " + genreId +
                                    ". Возможно, жанр отсутствует в базе данных. Проверьте data.sql."
                    );
                }
                catch (Exception e) {
                    // Случай В: Любая другая непредвиденная ошибка
                    throw new RuntimeException("Ошибка при сохранении связи фильм-жанр: " + e.getMessage(), e);
                }
            }
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ConditionsNotMetException("ID фильма обязателен для обновления");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма обязательно");
        }
        if (film.getDescription() != null && film.getDescription().length() > 100) {
            throw new ConditionsNotMetException("Описание фильма слишком длинное");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата выхода не может быть в будущем");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ConditionsNotMetException("Длительность должна быть положительной");
        }
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new ConditionsNotMetException("Рейтинг MPAA обязателен и должен иметь ID");
        }

        String updateFilmSql = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpaa_rating_id = ? WHERE id = ?";
        int rowsAffected = jdbcTemplate.update(updateFilmSql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (rowsAffected == 0) {
            throw new NotFoundException("Фильм с ID " + film.getId() + " не найден");
        }

        String deleteLinksSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteLinksSql, film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertGenreLinkSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                if (genre != null && genre.getId() != null) {
                    try {
                        jdbcTemplate.update(insertGenreLinkSql, film.getId(), genre.getId());
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        throw new NotFoundException(
                                "Не удалось обновить фильм: жанр с ID " + genre.getId() + " не найден."
                        );
                    }
                }
            }
        }

        return film;
    }

    @Override
    public Film findFilmById(Long id) {
        String sql = "SELECT * FROM film WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new FilmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public Map<Long, Film> getFilms() {
        String sql = "SELECT * FROM film";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());

        return films.stream()
                .collect(Collectors.toMap(Film::getId, f -> f));
    }

    @Override
    public void removeFilm(Long id) {
        String sql = "DELETE FROM film WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        // ВАЖНО: Мы используем LEFT JOIN, чтобы фильмы без лайков тоже попали в выборку (с количеством 0).
        // Если использовать обычный JOIN, фильмы без лайков исчезнут из списка.
        String sql = """
        SELECT f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_rating_id,
               COUNT(l.film_id) AS likes_count
        FROM film f
        LEFT JOIN likes l ON f.id = l.film_id
        GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpaa_rating_id
        ORDER BY likes_count DESC, f.release_date DESC
        LIMIT ?
    """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getLong("duration"));
            film.setMpa(new MpaaRating()); // Заглушка, если нужно, или потом дозагрузить
            film.getMpa().setId(rs.getLong("mpaa_rating_id"));

            // !!! ГЛАВНОЕ: Сохраняем количество лайков в какое-то поле или просто используем для сортировки.
            // Так как в классе Film нет поля likesCount, мы не можем сохранить его туда.
            // Но нам нужно вернуть список фильмов.
            // Хак: мы можем отсортировать их здесь, но вернуть просто список Film.
            // Однако, если нам нужно знать количество лайков для фронтенда, лучше добавить поле в DTO.

            // Для текущего этапа (вернуть просто список фильмов) этого достаточно.
            // Сортировка уже сделана в SQL (ORDER BY likes_count DESC).

            return film;
        }, count);
    }
}