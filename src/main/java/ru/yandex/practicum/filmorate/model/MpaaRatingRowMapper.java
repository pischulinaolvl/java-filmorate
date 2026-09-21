package ru.yandex.practicum.filmorate.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaaRatingRowMapper implements RowMapper<MpaaRating>{
    @Override
    public MpaaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        MpaaRating mpaaRating = new MpaaRating();

        // Читаем данные из ResultSet и кладем в поля объекта через сеттеры
        mpaaRating.setId(rs.getLong("id"));
        mpaaRating.setName(rs.getString("name"));
        mpaaRating.setDescription(rs.getString("description"));

        return mpaaRating;
    }
}