package ru.yandex.practicum.filmorate.repository.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.DataIntegrityViolationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.List;

@Component("LikeDbStorage")
@Repository
public class JdbcLikeRepository implements LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLikeRepository(JdbcTemplate jdbcTemplate) {
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

    @Override
    public List<Long> getRecommendationFilmIds(Long userId) {
        // Для каждого другого пользователя считаем число фильмов, лайкнутых им и текущим
        // пользователем. Берём всех с максимальным числом совпадений: при равенстве никто
        // не теряется. Их фильмы объединяем без повторов и исключаем уже лайкнутые текущим
        // пользователем. Если общих лайков ни с кем нет, запрос вернёт пустой список.
        String recommendationsSql = """
                WITH similarities AS (
                    SELECT other.user_id, COUNT(*) AS common_likes
                    FROM likes own
                    JOIN likes other ON own.film_id = other.film_id
                    WHERE own.user_id = ? AND other.user_id <> ?
                    GROUP BY other.user_id
                )
                SELECT DISTINCT liked.film_id
                FROM likes liked
                JOIN similarities similar ON liked.user_id = similar.user_id
                WHERE similar.common_likes = (SELECT MAX(common_likes) FROM similarities)
                  AND NOT EXISTS (
                      SELECT 1 FROM likes own
                      WHERE own.user_id = ? AND own.film_id = liked.film_id
                  )
                ORDER BY liked.film_id
                """;
        return jdbcTemplate.queryForList(recommendationsSql, Long.class, userId, userId, userId);
    }
}
