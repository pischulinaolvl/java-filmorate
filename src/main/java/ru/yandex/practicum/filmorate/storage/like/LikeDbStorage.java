package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Component("LikeDbStorage")
@Repository
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void putLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new IllegalArgumentException("ID фильма и пользователя обязательны");
        }

        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";

        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (DuplicateKeyException e) {
            // Пользователь уже лайкнул этот фильм, игнорируем
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Не удалось поставить лайк: фильм или пользователь не найдены");
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            throw new IllegalArgumentException("ID фильма и пользователя обязательны");
        }

        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public int getLikesCount(Long filmId) {
        if (filmId == null) {
            return 0;
        }
        String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, filmId);
    }

    @Override
    public boolean isLikedByUser(Long filmId, Long userId) {
        if (filmId == null || userId == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        long count = jdbcTemplate.queryForObject(sql, Long.class, filmId, userId);
        return count > 0;
    }
}
