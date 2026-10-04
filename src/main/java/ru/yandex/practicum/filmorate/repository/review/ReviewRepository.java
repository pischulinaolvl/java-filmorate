package ru.yandex.practicum.filmorate.repository.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewRepository {
    Review create(Review review);
    Review update(Review review);
    void delete(Long id);
    Review findById(Long id);
    List<Review> findAll(Long filmId, int count);

    void addLike(Long reviewId, Long userId);
    void addDislike(Long reviewId, Long userId);
    void removeLike(Long reviewId, Long userId);
    void removeDislike(Long reviewId, Long userId);
}