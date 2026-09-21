package ru.yandex.practicum.filmorate.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeRowMapper implements RowMapper<Like> {
    @Override
    public Like mapRow(ResultSet rs, int rowNum) throws SQLException {
        Like like = new Like();

        // Читаем данные из ResultSet и кладем в поля объекта через сеттеры
        like.setUserid(rs.getLong("user_id"));
        like.setFilmid(rs.getLong("film_id"));

        return like;
    }
}