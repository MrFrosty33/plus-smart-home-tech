package ru.yandex.practicum.shopping.store.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private final String className = this.getClass().getSimpleName();

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProductNotFoundException handleNotFound(ProductNotFoundException e) {
        logError(e);
        return e;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        logError(e);
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        logError(e);
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleOthers(Exception e) {
        logError(e);
        return e.getMessage();
    }

    private void logError(Exception e) {
        log.warn("{}: caught Exception: ", className, e);
    }
}
