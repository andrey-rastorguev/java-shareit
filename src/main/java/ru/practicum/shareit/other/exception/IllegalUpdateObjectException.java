package ru.practicum.shareit.other.exception;

public class IllegalUpdateObjectException extends RuntimeException {
    public IllegalUpdateObjectException(String message) {
        super("Illegal update '" + message + "'");
    }
}
