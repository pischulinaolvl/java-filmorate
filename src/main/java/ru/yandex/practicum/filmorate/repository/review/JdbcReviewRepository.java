package ru.yandex.practicum.filmorate.repository.review;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component("ReviewDbStorage")
@Repository
public class JdbcReviewRepository implements ReviewRepository {

    private final JdbcTemplate jdbc;

    private final RowMapper<Review> mapper = (rs, rowNum) -> {
        Review r = new Review();
        r.setReviewId(rs.getLong("id"));
        r.setContent(rs.getString("content"));
        r.setIsPositive(rs.getBoolean("is_positive"));
        r.setUserId(rs.getLong("user_id"));
        r.setFilmId(rs.getLong("film_id"));
        r.setUseful(rs.getInt("useful"));
        return r;
    };

    public JdbcReviewRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Review create(Review review) {
        String sql = """
            INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
            VALUES (?, ?, ?, ?, 0)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(0);
        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = """
            UPDATE reviews
            SET content = ?, is_positive = ?
            WHERE id = ?
            """;
        int updated = jdbc.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        if (updated == 0) {
            throw new NotFoundException("Отзыв с ID " + review.getReviewId() + " не найден");
        }
        return findById(review.getReviewId());
    }

    @Override
    public void delete(Long id) {
        int deleted = jdbc.update("DELETE FROM reviews WHERE id = ?", id);
        if (deleted == 0) {
            throw new NotFoundException("Отзыв с ID " + id + " не найден");
        }
    }

    @Override
    public Review findById(Long id) {
        try {
            return jdbc.queryForObject("SELECT * FROM reviews WHERE id = ?", mapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Отзыв с ID " + id + " не найден");
        }
    }

    @Override
    public List<Review> findAll(Long filmId, int count) {
        if (filmId != null) {
            return jdbc.query("""
                SELECT * FROM reviews
                WHERE film_id = ?
                ORDER BY useful DESC, id ASC
                LIMIT ?
                """, mapper, filmId, count);
        }
        return jdbc.query("""
            SELECT * FROM reviews
            ORDER BY useful DESC, id ASC
            LIMIT ?
            """, mapper, count);
    }

    // ---- голоса ----

    @Override
    public void addLike(Long reviewId, Long userId) {
        setVote(reviewId, userId, true);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        setVote(reviewId, userId, false);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        removeVote(reviewId, userId, true);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        removeVote(reviewId, userId, false);
    }

    /**
     * Ставит/меняет голос. useful пересчитывается относительно предыдущего состояния.
     */
    private void setVote(Long reviewId, Long userId, boolean isLike) {
        findById(reviewId);

        Boolean previous = getExistingVote(reviewId, userId);

        if (previous == null) {
            // новый голос
            jdbc.update(
                    "INSERT INTO review_likes (review_id, user_id, is_like) VALUES (?, ?, ?)",
                    reviewId, userId, isLike);
            changeUseful(reviewId, isLike ? 1 : -1);
        } else if (previous != isLike) {
            // смена like/dislike: useful меняется на +-2
            jdbc.update(
                    "UPDATE review_likes SET is_like = ? WHERE review_id = ? AND user_id = ?",
                    isLike, reviewId, userId);
            changeUseful(reviewId, isLike ? 2 : -2);
        }
        // если тот же голос уже стоит — ничего не делаем
    }

    private void removeVote(Long reviewId, Long userId, boolean expectedLike) {
        findById(reviewId);
        Boolean previous = getExistingVote(reviewId, userId);
        if (previous == null || previous != expectedLike) {
            return;
        }
        int deleted = jdbc.update(
                "DELETE FROM review_likes WHERE review_id = ? AND user_id = ? AND is_like = ?",
                reviewId, userId, expectedLike);
        if (deleted > 0) {
            changeUseful(reviewId, expectedLike ? -1 : 1);
        }
    }

    private Boolean getExistingVote(Long reviewId, Long userId) {
        try {
            return jdbc.queryForObject(
                    "SELECT is_like FROM review_likes WHERE review_id = ? AND user_id = ?",
                    Boolean.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void changeUseful(Long reviewId, int delta) {
        jdbc.update("UPDATE reviews SET useful = useful + ? WHERE id = ?", delta, reviewId);
    }
}