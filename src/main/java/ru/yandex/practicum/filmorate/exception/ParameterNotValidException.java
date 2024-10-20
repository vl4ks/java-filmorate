package ru.yandex.practicum.filmorate.exception;

public class ParameterNotValidException extends RuntimeException {

    public ParameterNotValidException(final String message) {
        super(message);
    }
}