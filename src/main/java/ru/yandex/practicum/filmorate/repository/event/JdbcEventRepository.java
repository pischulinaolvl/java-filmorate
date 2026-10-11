package ru.yandex.practicum.filmorate.repository.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
public class JdbcEventRepository implements EventRepository {
    private final JdbcTemplate jdbc;

    public JdbcEventRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void add(Long userId, String eventType, String operation, Long entityId) {
        jdbc.update("INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?, ?)",
                System.currentTimeMillis(), userId, eventType, operation, entityId);
    }

    @Override
    public List<Event> findByUserId(Long userId) {
        return jdbc.query("SELECT event_id, timestamp, user_id, event_type, operation, entity_id "
                        + "FROM events WHERE user_id = ? ORDER BY event_id",
                (rs, rowNum) -> new Event(rs.getLong("event_id"), rs.getLong("timestamp"),
                        rs.getLong("user_id"), rs.getString("event_type"), rs.getString("operation"),
                        rs.getLong("entity_id")), userId);
    }
}
