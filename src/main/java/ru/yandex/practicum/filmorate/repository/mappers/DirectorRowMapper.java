package ru.yandex.practicum.filmorate.repository.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorRowMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet currentRow, int rowNum) throws SQLException {
        return Director.builder()
                .id(currentRow.getLong("id"))
                .name(currentRow.getString("name"))
                .build();
    }
}