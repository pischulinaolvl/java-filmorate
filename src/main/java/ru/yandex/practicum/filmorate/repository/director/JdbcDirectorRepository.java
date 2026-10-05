package ru.yandex.practicum.filmorate.repository.director;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.mappers.DirectorRowMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("DirectorDbStorage") // По аналогии с GenreDbStorage
@Repository
public class JdbcDirectorRepository implements DirectorRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcDirectorRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Director createDirector(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ConditionsNotMetException("Название режиссёра обязательно");
        }

        String sql = "INSERT INTO director (name) VALUES (:name)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", director.getName());

        namedParameterJdbcTemplate.update(sql, params);

        String selectSql = "SELECT * FROM director WHERE name = :name";
        try {
            return namedParameterJdbcTemplate.queryForObject(selectSql, params, new DirectorRowMapper());
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NotFoundException("Не удалось получить созданного режиссёра");
        }
    }

    @Override
    public Director updateDirector(Director director) {
        if (director.getId() == null) {
            throw new ConditionsNotMetException("ID режиссёра обязателен для обновления");
        }
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ConditionsNotMetException("Имя режиссёра обязательно");
        }

        String sql = "UPDATE director SET name = :name WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", director.getName());
        params.addValue("id", director.getId());

        int rows = namedParameterJdbcTemplate.update(sql, params);

        if (rows == 0) {
            throw new NotFoundException("Режиссёр с ID " + director.getId() + " не найден");
        }

        return findDirectorById(director.getId());
    }

    @Override
    public void deleteDirector(Long id) {
        String sql = "DELETE FROM director WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        int rows = namedParameterJdbcTemplate.update(sql, params);

        if (rows == 0) {
            throw new NotFoundException("Режиссёр с ID " + id + " не найден");
        }
    }

    @Override
    public Director findDirectorById(Long id) {
        String sql = "SELECT * FROM director WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, new DirectorRowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Режиссёр с ID " + id + " не найден");
        }
    }

    @Override
    public List<Director> getDirectors() {
        String sql = "SELECT * FROM director ORDER BY id";
        return namedParameterJdbcTemplate.query(sql, new DirectorRowMapper());
    }
}