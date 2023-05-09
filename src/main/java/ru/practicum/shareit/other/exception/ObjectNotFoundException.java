package ru.practicum.shareit.other.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String user) {
        super("Object '" + user + "' not found");
    }
}
