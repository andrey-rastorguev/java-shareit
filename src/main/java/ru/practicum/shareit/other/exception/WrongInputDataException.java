package ru.practicum.shareit.other.exception;

public class WrongInputDataException extends RuntimeException {
    public WrongInputDataException(String message) {
        super(message);
    }
}
