package ru.yandex.practicum.filmorate.repository.like;

import java.util.List;

public interface LikeRepository {
    boolean putLike(Long filmId, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    int getLikesCount(Long filmId);

    boolean isLikedByUser(Long filmId, Long userId); // Опционально, полезно для фронтенда

    List<Long> getRecommendationFilmIds(Long userId);
}
