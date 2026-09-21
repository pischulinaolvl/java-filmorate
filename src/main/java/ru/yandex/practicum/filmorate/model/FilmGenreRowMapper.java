package ru.yandex.practicum.filmorate.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmGenreRowMapper implements RowMapper<FilmGenre>{
    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmGenre filmGenre = new FilmGenre();

        // Читаем данные из ResultSet и кладем в поля объекта через сеттеры
        filmGenre.setFilmid(rs.getLong("film_id"));
        filmGenre.setGenreid(rs.getLong("genre_id"));

        return filmGenre;
    }
}
