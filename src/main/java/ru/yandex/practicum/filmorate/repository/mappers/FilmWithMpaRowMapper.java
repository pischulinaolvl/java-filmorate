package ru.yandex.practicum.filmorate.repository.mappers; // Или тот пакет, где у тебя лежат мапперы

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmWithMpaRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        if (rs.getDate("release_date") != null) {
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        } else {
            film.setReleaseDate(null);
        }

        film.setDuration(rs.getLong("duration"));

        MpaaRating mpa = new MpaaRating();

        if (rs.getLong("mpa_id") != 0) {
            mpa.setId(rs.getLong("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            mpa.setDescription(rs.getString("mpa_desc"));

            film.setMpa(mpa);
        } else {
            film.setMpa(null);
        }

        return film;
    }
}