package ru.practicum.shareit.other.exception;

public class AccessDeniedForUserException extends RuntimeException {
    public AccessDeniedForUserException(int userId) {
        super("Access for user '" + userId + "' is denied");
    }
}
