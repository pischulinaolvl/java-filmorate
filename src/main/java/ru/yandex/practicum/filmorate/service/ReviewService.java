package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.event.EventRepository;
import ru.yandex.practicum.filmorate.repository.film.FilmRepository;
import ru.yandex.practicum.filmorate.repository.review.ReviewMapper;
import ru.yandex.practicum.filmorate.repository.review.ReviewRepository;
import ru.yandex.practicum.filmorate.repository.user.UserRepository;

import java.util.List;

@Slf4j
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
    public ReviewDto create(ReviewDto reviewDto) {
        log.info("Создание отзыва: userId={}, filmId={}", reviewDto.getUserId(), reviewDto.getFilmId());
        Review review = ReviewMapper.mapToReview(reviewDto);
        validate(review);
        ensureUserExists(review.getUserId());
        ensureFilmExists(review.getFilmId());

        Review created = reviewRepository.create(review);
        eventRepository.add(created.getUserId(), "REVIEW", "ADD", created.getReviewId());

        log.info("Отзыв создан: reviewId={}", created.getReviewId());
        return ReviewMapper.mapToReviewDto(created);
    }

    @Transactional
    public ReviewDto update(ReviewDto reviewDto) {
        log.info("Обновление отзыва: reviewId={}", reviewDto.getReviewId());
        if (reviewDto.getReviewId() == null) {
            throw new ConditionsNotMetException("ID отзыва обязателен для обновления");
        }

        Review existing = reviewRepository.findById(reviewDto.getReviewId());
        if (reviewDto.getContent() == null || reviewDto.getContent().isBlank()) {
            throw new ConditionsNotMetException("Содержание отзыва не может быть пустым");
        }
        if (reviewDto.getIsPositive() == null) {
            throw new ConditionsNotMetException("Тип отзыва (isPositive) обязателен");
        }

        Review updated = reviewRepository.update(ReviewMapper.mapToReview(reviewDto));
        eventRepository.add(existing.getUserId(), "REVIEW", "UPDATE", updated.getReviewId());

        log.info("Отзыв обновлён: reviewId={}", updated.getReviewId());
        return ReviewMapper.mapToReviewDto(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Удаление отзыва: reviewId={}", id);
        Review existing = reviewRepository.findById(id);
        reviewRepository.delete(id);
        eventRepository.add(existing.getUserId(), "REVIEW", "REMOVE", id);
        log.info("Отзыв удалён: reviewId={}", id);
    }

    public ReviewDto getById(Long id) {
        log.info("Получение отзыва: reviewId={}", id);
        return ReviewMapper.mapToReviewDto(reviewRepository.findById(id));
    }

    public List<ReviewDto> getAll(Long filmId, Integer count) {
        int limit = (count == null || count <= 0) ? 10 : count;
        log.info("Список отзывов: filmId={}, count={}", filmId, limit);
        if (filmId != null) {
            ensureFilmExists(filmId);
        }
        return ReviewMapper.mapToReviewDtoList(reviewRepository.findAll(filmId, limit));
    }

    public void addLike(Long reviewId, Long userId) {
        log.info("Лайк отзыву {} от пользователя {}", reviewId, userId);
        ensureUserExists(userId);
        reviewRepository.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        log.info("Дизлайк отзыву {} от пользователя {}", reviewId, userId);
        ensureUserExists(userId);
        reviewRepository.addDislike(reviewId, userId);
    }

    public void removeLike(Long reviewId, Long userId) {
        log.info("Удаление лайка отзыва {} пользователем {}", reviewId, userId);
        ensureUserExists(userId);
        reviewRepository.removeLike(reviewId, userId);
    }

    public void removeDislike(Long reviewId, Long userId) {
        log.info("Удаление дизлайка отзыва {} пользователем {}", reviewId, userId);
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