package ru.yandex.practicum.filmorate.storage.like;

import ru.yandex.practicum.filmorate.model.Like;

public interface LikeStorage {
    void putLike(Long filmId, Long userId);
    void deleteLike(Long filmId, Long userId);
    int getLikesCount(Long filmId);
    boolean isLikedByUser(Long filmId, Long userId); // Опционально, полезно для фронтенда
}