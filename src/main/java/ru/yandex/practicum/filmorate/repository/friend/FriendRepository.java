package ru.yandex.practicum.filmorate.repository.friend;

import java.util.Map;

public interface FriendRepository {
    void createFriendship(long user1Id, long user2Id, String status);

    void updateFriendship(long user1Id, long user2Id, String status);

    Map<Long, String> getFriends(long userId, boolean forSecondFriend);

    void removeFriendship(long user1Id, long user2Id);
}