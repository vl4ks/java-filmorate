package ru.yandex.practicum.filmorate.exception;

public class InvalidDataAccessException extends RuntimeException {
    public InvalidDataAccessException(String message) {
        super(message);
    }
}
