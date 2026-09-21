package ru.yandex.practicum.filmorate.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        if (rs.getDate("release_date") != null) {
            film.setReleaseDate(rs.getDate("release_date").toLocalDate()); // Важно: конвертируем SQL Date в LocalDate
        } else {
            film.setReleaseDate(null);
        }
        film.setDuration(rs.getLong("duration"));
        //film.(rs.getLong("mpaa_rating_id"));

        MpaaRating mpa = new MpaaRating();
        mpa.setId(rs.getLong("mpaa_rating_id"));
        film.setMpa(mpa);
        return film;
    }
}