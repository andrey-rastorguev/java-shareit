package ru.practicum.shareit.other.exception;

public class AccessDeniedForItemException extends RuntimeException {
    public AccessDeniedForItemException(long itemId) {
        super("Access for user '" + itemId + "' is denied");
    }
}
