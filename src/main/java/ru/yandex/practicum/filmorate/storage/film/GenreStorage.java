package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    /**
     * Получить жанр по его уникальному идентификатору.
     * @param id ID жанра
     * @return Объект Genre
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если жанр не найден
     */
    Genre getGenreById(Long id);

    /**
     * Получить список всех жанров.
     * @return Список объектов Genre
     */
    List<Genre> getAllGenres();

    /**
     * Создать новый жанр.
     * @param genre Объект Genre с данными для создания (ID обычно игнорируется, так как генерируется БД)
     * @return Созданный объект Genre (с присвоенным ID)
     * @throws ru.yandex.practicum.filmorate.exception.ConditionsNotMetException если данные некорректны
     */
    Genre createGenre(Genre genre);

    /**
     * Обновить существующий жанр.
     * @param genre Объект Genre с новыми данными (обязательно должен содержать корректный ID)
     * @return Обновленный объект Genre
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если жанр не найден
     * @throws ru.yandex.practicum.filmorate.exception.ConditionsNotMetException если данные некорректны
     */
    Genre updateGenre(Genre genre);

    /**
     * Удалить жанр по ID.
     * @param id ID удаляемого жанра
     * @throws ru.yandex.practicum.filmorate.exception.NotFoundException если жанр не найден
     */
    void deleteGenre(Long id);
}