package ru.yandex.practicum.filmorate.model;

public record Event(Long eventId, long timestamp, Long userId, String eventType,
                    String operation, Long entityId) {
}
