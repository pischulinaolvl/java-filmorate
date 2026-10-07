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
import ru.yandex.practicum.filmorate.repository.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.FilmWithMpaRowMapper;

import java.time.LocalDate;
import java.util.*;
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

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            String insertGenreLinkSql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
            Set<Long> processedDirectorIds = new HashSet<>();

            for (Director director : film.getDirectors()) {
                if (director == null || director.getId() == null || !processedDirectorIds.add(director.getId())) {
                    continue;
                }

                Long directorId = director.getId();

                try {
                    int rowsAffected = jdbcTemplate.update(insertGenreLinkSql, newFilmId, directorId);
                } catch (org.springframework.dao.DataIntegrityViolationException e) {
                    throw new NotFoundException(
                            "Не удалось связать фильм с режиссёром ID: " + directorId +
                                    ". Возможно, жанр отсутствует в базе данных. Проверьте data.sql."
                    );
                } catch (Exception e) {
                    throw new RuntimeException("Ошибка при сохранении связи фильм-режиссёр: " + e.getMessage(), e);
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

        deleteLinksSql = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(deleteLinksSql, film.getId());

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            String insertGenreLinkSql = "INSERT INTO film_director (film_id, director_id) VALUES (?, ?)";
            for (Director director : film.getDirectors()) {
                if (director != null && director.getId() != null) {
                    try {
                        jdbcTemplate.update(insertGenreLinkSql, film.getId(), director.getId());
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        throw new NotFoundException(
                                "Не удалось обновить фильм: жанр с ID " + director.getId() + " не найден."
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

            String directorSql = "SELECT d.* FROM director d JOIN film_director fd ON d.id = fd.director_id WHERE fd.film_id = ?";

            List<Director> directors = jdbcTemplate.query(directorSql, new DirectorRowMapper(), id);

            film.setDirectors(directors);

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
    public int removeFilm(Long id) {
        String sql = "DELETE FROM film WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getPopularFilms(int count, Long genreId, Integer year) {
        StringBuilder sql = new StringBuilder("""
        SELECT f.id
        FROM film f
        LEFT JOIN likes l ON f.id = l.film_id
        WHERE 1=1
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("count", count);

        if (year != null) {
            sql.append(" AND EXTRACT(YEAR FROM f.release_date) = :year");
            params.addValue("year", year);
        }

        if (genreId != null) {
            sql.append("""
             AND EXISTS (
                SELECT 1 FROM film_genre fg
                WHERE fg.film_id = f.id AND fg.genre_id = :genreId
             )
            """);
            params.addValue("genreId", genreId);
        }

        sql.append("""
         GROUP BY f.id
         ORDER BY COUNT(l.user_id) DESC, f.id ASC
         LIMIT :count
        """);

        List<Long> filmIds = namedParameterJdbcTemplate.query(
                sql.toString(),
                params,
                (rs, rowNum) -> rs.getLong("id")
        );

        return loadFilmsByIds(filmIds);
    }

    private List<Film> loadFilmsByIds(List<Long> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return List.of();
        }

        MapSqlParameterSource idsParams = new MapSqlParameterSource("ids", filmIds);

        String filmsSql = """
            SELECT f.id, f.name, f.description, f.release_date, f.duration,
                   m.id AS mpa_id, m.name AS mpa_name, m.description AS mpa_desc
            FROM film f
            JOIN mpaa_rating m ON f.mpaa_rating_id = m.id
            WHERE f.id IN (:ids)
            """;

        List<Film> loadedFilms = namedParameterJdbcTemplate.query(
                filmsSql, idsParams, new FilmWithMpaRowMapper());

        Map<Long, Film> filmsById = new HashMap<>();
        for (Film film : loadedFilms) {
            film.setGenres(new ArrayList<>());
            filmsById.put(film.getId(), film);
        }

        String genresSql = """
            SELECT fg.film_id, g.id, g.name
            FROM film_genre fg
            JOIN genre g ON g.id = fg.genre_id
            WHERE fg.film_id IN (:ids)
            ORDER BY g.id
            """;

        namedParameterJdbcTemplate.query(genresSql, idsParams, rs -> {
            Long filmId = rs.getLong("film_id");
            Film film = filmsById.get(filmId);
            if (film != null) {
                Genre genre = new Genre();
                genre.setId(rs.getLong("id"));
                genre.setName(rs.getString("name"));
                film.getGenres().add(genre);
            }
        });

        return filmIds.stream()
                .map(filmsById::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        String sql = """
                SELECT l1.film_id
                FROM likes l1
                JOIN likes l2 ON l1.film_id = l2.film_id
                WHERE l1.user_id = ? AND l2.user_id = ?
                ORDER BY (SELECT COUNT(*) FROM likes WHERE film_id = l1.film_id) DESC, l1.film_id ASC
                """;

        return jdbcTemplate.queryForList(sql, Long.class, userId, friendId).stream()
                .map(this::findFilmById)
                .toList();
    }

    @Override
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        StringBuilder sql = new StringBuilder("""
        SELECT f.id
        FROM film f
        JOIN film_director fd ON fd.film_id = f.id
                             and fd.director_id = :directorId
    """);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("directorId", directorId);

        if ("likes".equals(sortBy)) {
            sql.append("""
            LEFT JOIN likes l ON f.id = l.film_id
            GROUP BY f.id
            ORDER BY COUNT(l.user_id) DESC, f.id ASC
        """);
        } else if ("year".equals(sortBy)) {
            sql.append(" ORDER BY f.release_date, f.id ASC");
        }

        return namedParameterJdbcTemplate.queryForList(sql.toString(), params, Long.class).stream()
                .map(this::findFilmById)
                .toList();
    }
}
