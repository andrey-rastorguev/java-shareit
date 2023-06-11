package ru.practicum.shareit.other.exception;

public class WrongStatusBookingException extends RuntimeException {
    public WrongStatusBookingException(String message) {
        super(message);
    }
}
