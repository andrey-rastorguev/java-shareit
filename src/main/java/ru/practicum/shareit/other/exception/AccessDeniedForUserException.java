package ru.practicum.shareit.other.exception;

public class AccessDeniedForUserException extends RuntimeException {
    public AccessDeniedForUserException(long userId) {
        super("Access for user '" + userId + "' is denied");
    }
}
