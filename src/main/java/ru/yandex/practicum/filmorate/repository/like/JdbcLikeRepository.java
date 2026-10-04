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
        // Каждый общий лайк даёт одну строку после соединения own и other. Группировка
        // считает число совпадений для каждого другого пользователя; первым выбираем того,
        // у кого их больше всего. Если совпадений поровну, выбираем меньший ID пользователя.
        // Когда общих лайков нет ни с кем, похожего пользователя нет и список будет пустым.
        String similarUserSql = """
                SELECT other.user_id
                FROM likes own
                JOIN likes other ON own.film_id = other.film_id
                WHERE own.user_id = ? AND other.user_id <> ?
                GROUP BY other.user_id
                ORDER BY COUNT(*) DESC, other.user_id ASC
                LIMIT 1
                """;
        List<Long> similarUsers = jdbcTemplate.queryForList(similarUserSql, Long.class, userId, userId);
        if (similarUsers.isEmpty()) {
            return List.of();
        }

        // Берём фильмы, лайкнутые выбранным пользователем, и исключаем те, которым
        // исходный пользователь уже поставил лайк: повторно рекомендовать их не нужно.
        String recommendationsSql = """
                SELECT liked.film_id
                FROM likes liked
                WHERE liked.user_id = ?
                  AND NOT EXISTS (
                      SELECT 1 FROM likes own
                      WHERE own.user_id = ? AND own.film_id = liked.film_id
                  )
                ORDER BY liked.film_id
                """;
        return jdbcTemplate.queryForList(recommendationsSql, Long.class, similarUsers.get(0), userId);
    }
}
