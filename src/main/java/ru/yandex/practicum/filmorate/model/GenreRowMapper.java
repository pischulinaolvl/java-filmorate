package ru.yandex.practicum.filmorate.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();

        // Читаем данные из ResultSet и кладем в поля объекта через сеттеры
        genre.setId(rs.getLong("id"));
        genre.setName(rs.getString("name"));

        return genre;
    }
}