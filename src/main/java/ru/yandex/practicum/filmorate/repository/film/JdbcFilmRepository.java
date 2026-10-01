package ru.yandex.practicum.filmorate.repository.film;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.FilmWithMpaRowMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component("FilmDbStorage")
@Repository
public class JdbcFilmRepository implements FilmRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final LocalDate minDate = LocalDate.of(1895, 12, 28);

    public JdbcFilmRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
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
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new ConditionsNotMetException("Дата релиза не может быть раньше, чем 28 декабря 1895");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO film (name, description, release_date, duration, mpaa_rating_id) " +
                "VALUES (:name, :description, :release_date, :duration, :mpa_id)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", film.getMpa().getId());

        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        Long newFilmId = keyHolder.getKey().longValue();

        if (newFilmId == null) {
            throw new NotFoundException("Не удалось получить ID созданного фильма");
        }

        film.setId(newFilmId);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String insertGenreLinkSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            Set<Long> processedGenreIds = new HashSet<>();

            for (Genre genre : film.getGenres()) {
                if (genre == null || genre.getId() == null || !processedGenreIds.add(genre.getId())) {
                    continue;
                }

                Long genreId = genre.getId();

                try {
                    int rowsAffected = jdbcTemplate.update(insertGenreLinkSql, newFilmId, genreId);
                } catch (org.springframework.dao.DataIntegrityViolationException e) {
                    throw new NotFoundException(
                            "Не удалось связать фильм с жанром ID: " + genreId +
                                    ". Возможно, жанр отсутствует в базе данных. Проверьте data.sql."
                    );
                } catch (Exception e) {
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
        String filmSql = """
        SELECT f.id, f.name, f.description, f.release_date, f.duration,
               m.id as mpa_id, m.name as mpa_name, m.description as mpa_desc
        FROM film f
        JOIN mpaa_rating m ON f.mpaa_rating_id = m.id
        WHERE f.id = ?
    """;

        try {
            Film film = jdbcTemplate.queryForObject(filmSql, new FilmWithMpaRowMapper(), id);

            if (film == null) {
                return null;
            }

            String genresSql = "SELECT g.id, g.name FROM genre g JOIN film_genre fg ON g.id = fg.genre_id WHERE fg.film_id = ?";

            List<Genre> genres = jdbcTemplate.query(genresSql, (rs, rowNum) -> {
                Genre g = new Genre();
                g.setId(rs.getLong("id"));
                g.setName(rs.getString("name"));
                return g;
            }, id);

            film.setGenres(genres);

            return film;
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при маппинге фильма: " + e.getMessage(), e);
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

            return film;
        }, count);
    }
}