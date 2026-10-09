package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         @Qualifier("FilmDbStorage") FilmRepository filmRepository,
                         @Qualifier("UserDbStorage") UserRepository userRepository,
                         EventRepository eventRepository) {
        this.reviewRepository = reviewRepository;
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Review create(Review review) {
        validate(review);
        ensureUserExists(review.getUserId());
        ensureFilmExists(review.getFilmId());
        Review created = reviewRepository.create(review);
        eventRepository.add(created.getUserId(), "REVIEW", "ADD", created.getReviewId());
        return created;
    }

    @Transactional
    public Review update(Review review) {
        if (review.getReviewId() == null) {
            throw new ConditionsNotMetException("ID отзыва обязателен для обновления");
        }
        Review existing = reviewRepository.findById(review.getReviewId());
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ConditionsNotMetException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ConditionsNotMetException("Тип отзыва (isPositive) обязателен");
        }
        Review updated = reviewRepository.update(review);
        eventRepository.add(existing.getUserId(), "REVIEW", "UPDATE", updated.getReviewId());
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        Review existing = reviewRepository.findById(id);
        reviewRepository.delete(id);
        eventRepository.add(existing.getUserId(), "REVIEW", "REMOVE", id);
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getAll(Long filmId, Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count;
        if (filmId != null) {
            ensureFilmExists(filmId);
        }
        return reviewRepository.findAll(filmId, limit);
    }

    public void addLike(Long reviewId, Long userId) {
        ensureUserExists(userId);
        reviewRepository.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        ensureUserExists(userId);
        reviewRepository.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        ensureUserExists(userId);
        reviewRepository.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        ensureUserExists(userId);
        reviewRepository.removeDislike(reviewId, userId);
    }

    private void validate(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ConditionsNotMetException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new ConditionsNotMetException("Тип отзыва (isPositive) обязателен");
        }
        if (review.getUserId() == null) {
            throw new ConditionsNotMetException("userId обязателен");
        }
        if (review.getFilmId() == null) {
            throw new ConditionsNotMetException("filmId обязателен");
        }
    }

    private void ensureUserExists(Long userId) {
        if (userRepository.findUserById(userId) == null) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }

    private void ensureFilmExists(Long filmId) {
        if (filmRepository.findFilmById(filmId) == null) {
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
    }
}
