package ru.yandex.practicum.filmorate.model;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();

        // Явно маппим каждую колонку из ResultSet в поле объекта
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        if (rs.getDate("birthday") != null) {
            user.setBirthday(rs.getDate("birthday").toLocalDate()); // Важно: конвертируем SQL Date в LocalDate
        } else {
            user.setBirthday(null);
        }

        return user;
    }
}

