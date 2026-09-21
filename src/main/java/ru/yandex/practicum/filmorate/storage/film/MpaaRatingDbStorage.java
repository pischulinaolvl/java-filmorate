package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaaRating;
import ru.yandex.practicum.filmorate.model.MpaaRatingRowMapper;

import java.util.List;

@Component("MpaDbStorage")
@Repository
public class MpaaRatingDbStorage implements MpaaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MpaaRating getMpaById(Long id) {
        String sql = "SELECT * FROM mpaa_rating WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new MpaaRatingRowMapper(), id);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг MPA с ID " + id + " не найден");
        }
    }

    @Override
    public List<MpaaRating> getAllMpa() {
        String sql = "SELECT * FROM mpaa_rating ORDER BY id";
        return jdbcTemplate.query(sql, new MpaaRatingRowMapper());
    }

    @Override
    public MpaaRating createMpa(MpaaRating mpa) {
        if (mpa.getName() == null || mpa.getName().isBlank()) {
            throw new ConditionsNotMetException("Название рейтинга обязательно");
        }
        if (mpa.getDescription() != null && mpa.getDescription().length() > 1000) {
            throw new ConditionsNotMetException("Описание рейтинга слишком длинное");
        }

        String sql = "INSERT INTO mpaa_rating (code, description) VALUES (?, ?)";
        jdbcTemplate.update(sql, mpa.getName(), mpa.getDescription());

        String selectSql = "SELECT * FROM mpaa_rating WHERE code = ?";
        try {
            return jdbcTemplate.queryForObject(selectSql, new MpaaRatingRowMapper(), mpa.getName());
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            throw new NotFoundException("Не удалось получить созданный рейтинг");
        }
    }

    @Override
    public MpaaRating updateMpa(MpaaRating mpa) {
        if (mpa.getId() == null) {
            throw new ConditionsNotMetException("ID рейтинга обязателен для обновления");
        }
        if (mpa.getName() == null || mpa.getName().isBlank()) {
            throw new ConditionsNotMetException("Название рейтинга обязательно");
        }

        String sql = "UPDATE mpaa_rating SET code = ?, description = ? WHERE id = ?";
        int rows = jdbcTemplate.update(sql, mpa.getName(), mpa.getDescription(), mpa.getId());

        if (rows == 0) {
            throw new NotFoundException("Рейтинг MPA с ID " + mpa.getId() + " не найден");
        }

        return getMpaById(mpa.getId());
    }

    @Override
    public void deleteMpa(Long id) {
        String sql = "DELETE FROM mpaa_rating WHERE id = ?";
        int rows = jdbcTemplate.update(sql, id);
        if (rows == 0) {
            throw new NotFoundException("Рейтинг MPA с ID " + id + " не найден");
        }
    }
}