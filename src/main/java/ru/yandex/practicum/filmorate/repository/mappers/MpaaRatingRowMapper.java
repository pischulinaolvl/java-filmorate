package ru.yandex.practicum.filmorate.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.MpaaRating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MpaaRatingRowMapper implements RowMapper<MpaaRating> {
    @Override
    public MpaaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        MpaaRating mpaaRating = new MpaaRating();

        mpaaRating.setId(rs.getLong("id"));
        mpaaRating.setName(rs.getString("name"));
        mpaaRating.setDescription(rs.getString("description"));

        return mpaaRating;
    }
}