package ru.yandex.practicum.filmorate.repository.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;

import java.util.List;

@Component("GenreDbStorage")
@Repository
public class JdbcGenreRepository implements GenreRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcGenreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(Long id) {
        String sql = "SELECT * FROM genre WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new GenreRowMapper(), id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с ID " + id + " не найден");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT * FROM genre ORDER BY id";
        return jdbcTemplate.query(sql, new GenreRowMapper());
    }

    @Override
    public Genre createGenre(Genre genre) {
        if (genre.getName() == null || genre.getName().isBlank()) {
            throw new ConditionsNotMetException("Название жанра обязательно");
        }

        String sql = "INSERT INTO genre (name) VALUES (?)";
        jdbcTemplate.update(sql, genre.getName());

        String selectSql = "SELECT * FROM genre WHERE name = ?";
        try {
            return jdbcTemplate.queryForObject(selectSql, new GenreRowMapper(), genre.getName());
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NotFoundException("Не удалось получить созданный жанр");
        }
    }

    @Override
    public Genre updateGenre(Genre genre) {
        if (genre.getId() == null) {
            throw new ConditionsNotMetException("ID жанра обязателен для обновления");
        }
        if (genre.getName() == null || genre.getName().isBlank()) {
            throw new ConditionsNotMetException("Название жанра обязательно");
        }

        String sql = "UPDATE genre SET name = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, genre.getName(), genre.getId());

        if (rows == 0) {
            throw new NotFoundException("Жанр с ID " + genre.getId() + " не найден");
        }

        return getGenreById(genre.getId());
    }

    @Override
    public void deleteGenre(Long id) {
        String sql = "DELETE FROM genre WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);

        if (rows == 0) {
            throw new NotFoundException("Жанр с ID " + id + " не найден");
        }
    }
}