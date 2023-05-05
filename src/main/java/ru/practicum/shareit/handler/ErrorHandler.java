package ru.practicum.shareit.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.other.ErrorResponse;
import ru.practicum.shareit.other.exception.AccessDeniedForUserException;
import ru.practicum.shareit.other.exception.ObjectNotFoundException;
import ru.practicum.shareit.other.exception.WrongInputDataException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ErrorResponse handlerObjectNotFound(final ObjectNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    ErrorResponse handlerAccessDeniedForUser(final AccessDeniedForUserException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    ErrorResponse handlerWrongInputData(final WrongInputDataException e) {
        return new ErrorResponse(e.getMessage());
    }
}
