package ru.yandex.practicum.filmorate.repository.like;

public interface LikeRepository {
    void putLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);

    int getLikesCount(Long filmId);

    boolean isLikedByUser(Long filmId, Long userId); // Опционально, полезно для фронтенда
}