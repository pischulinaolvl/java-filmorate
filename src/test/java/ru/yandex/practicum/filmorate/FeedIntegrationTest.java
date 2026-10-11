package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:feedtest;DB_CLOSE_DELAY=-1")
class FeedIntegrationTest {
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private UserService userService;
    @Autowired
    private FilmService filmService;
    @Autowired
    private ReviewService reviewService;

    @Test
    void recordsSuccessfulActionsInOrder() {
        jdbc.update("INSERT INTO \"user\" (id, email, login) VALUES (9001, 'feed1@example.com', 'feed1')");
        jdbc.update("INSERT INTO \"user\" (id, email, login) VALUES (9002, 'feed2@example.com', 'feed2')");
        jdbc.update("INSERT INTO film (id, name, mpaa_rating_id) VALUES (9001, 'Feed film', 1)");

        var log = LoggerFactory.getLogger(FeedIntegrationTest.class);
        userService.addFriend(9001L, 9002L, log);
        filmService.addLike(9001L, 9001L, log);
        filmService.addLike(9001L, 9001L, log);

        Review review = new Review();
        review.setUserId(9001L);
        review.setFilmId(9001L);
        review.setContent("Good film");
        review.setIsPositive(true);
        review = reviewService.create(review);
        review.setContent("Great film");
        reviewService.update(review);
        reviewService.delete(review.getReviewId());
        filmService.removeLike(9001L, 9001L, log);
        userService.removeFriend(9001L, 9002L, log);

        List<Event> feed = userService.getFeed(9001L);
        assertEquals(List.of("FRIEND:ADD", "LIKE:ADD", "REVIEW:ADD", "REVIEW:UPDATE",
                        "REVIEW:REMOVE", "LIKE:REMOVE", "FRIEND:REMOVE"),
                feed.stream().map(event -> event.eventType() + ":" + event.operation()).toList());
        assertEquals(List.of(9002L, 9001L, review.getReviewId(), review.getReviewId(),
                        review.getReviewId(), 9001L, 9002L),
                feed.stream().map(Event::entityId).toList());
        assertEquals(List.of(), userService.getFeed(9002L));
    }
}
