package ru.yandex.practicum.filmorate.model; // Или тот пакет, где у тебя лежат мапперы

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmWithMpaRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();

        // --- Заполняем поля самого фильма ---
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));

        // Конвертируем SQL Date в LocalDate
        if (rs.getDate("release_date") != null) {
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        } else {
            film.setReleaseDate(null);
        }

        film.setDuration(rs.getLong("duration"));

        // --- Заполняем объект Mpa (Рейтинг) ---
        MpaaRating mpa = new MpaaRating();

        // Проверяем, есть ли данные о рейтинге (на случай если JOIN вдруг вернет null, хотя FK этого не допустит)
        if (rs.getLong("mpa_id") != 0) {
            mpa.setId(rs.getLong("mpa_id"));
            mpa.setName(rs.getString("mpa_name"));
            mpa.setDescription(rs.getString("mpa_desc"));

            film.setMpa(mpa);
        } else {
            // Если рейтинга нет (теоретически невозможно из-за FK), оставляем поле пустым
            film.setMpa(null);
        }

        return film;
    }
}