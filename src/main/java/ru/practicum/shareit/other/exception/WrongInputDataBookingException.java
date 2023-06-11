package ru.practicum.shareit.other.exception;

public class WrongInputDataBookingException extends RuntimeException {
    public WrongInputDataBookingException(String message) {
        super(message);
    }
}
