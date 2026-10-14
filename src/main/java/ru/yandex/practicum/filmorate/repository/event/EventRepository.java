package ru.yandex.practicum.filmorate.repository.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventRepository {
    void add(Long userId, String eventType, String operation, Long entityId);

    List<Event> findByUserId(Long userId);
}
