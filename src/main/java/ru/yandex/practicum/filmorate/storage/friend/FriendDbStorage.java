package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class FriendDbStorage implements FriendStorage{
    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Получаем список ID друзей для конкретного пользователя
    public Map<Long, String> getFriends(long userId, boolean forSecondFriend) {
        String sql;
        if (!forSecondFriend) {
            sql = "SELECT user2_id as friendId, status FROM friends WHERE user1_id = ?";
        } else {
            sql = "SELECT user1_id as friendId, status FROM friends WHERE user2_id = ?";
        }

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
                    Long friendId = rs.getLong("friendId");
                    String status = rs.getString("status");
                    return new HashMap.SimpleEntry<>(friendId, status);
                }, userId)
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        java.util.AbstractMap.SimpleEntry::getKey,
                        java.util.AbstractMap.SimpleEntry::getValue
                ));
    }

    // Метод для добавления дружбы (INSERT в таблицу friends)
    public void createFriendship(long user1Id, long user2Id, String status) {
        String sql = "INSERT INTO friends (user1_id, user2_id, status) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user1Id, user2Id, status);
    }

    public void updateFriendship(long user1Id, long user2Id, String status) {
        String sql = "UPDATE friends SET status = ? WHERE user1_id = ? AND user2_id = ?";
        jdbcTemplate.update(sql, status, user1Id, user2Id);
    }

    @Override
    public void removeFriendship(long user1Id, long user2Id) {
        String sql = "DELETE FROM friends WHERE user1_id = ? AND user2_id = ?";

        int rowsAffected = jdbcTemplate.update(sql, user1Id, user2Id);
    }
}
