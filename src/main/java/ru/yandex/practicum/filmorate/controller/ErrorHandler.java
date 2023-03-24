package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

import java.util.Map;

@RestControllerAdvice("ru.yandex.practicum.filmorate")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException ex) {
        return new ErrorResponse(ex.getMessage());
    }

//    FIXME: Запрос сюда попадает верно, но в тестах выдает 500 вместо 404
    @ExceptionHandler({UserNotFoundException.class, FilmNotFoundException.class, NullPointerException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException ex) {
        System.out.println(HttpStatus.NOT_FOUND);
        return new ErrorResponse(ex.getMessage());
    }

/*    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleUserNotFoundException(final UserNotFoundException ex) {
        System.out.println(HttpStatus.NOT_FOUND);
//        return new ErrorResponse(ex.getMessage());
        return Map.of("Объект не найден: ", ex.getMessage());
    }*/

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable ex) {
        System.out.println(HttpStatus.INTERNAL_SERVER_ERROR);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}
