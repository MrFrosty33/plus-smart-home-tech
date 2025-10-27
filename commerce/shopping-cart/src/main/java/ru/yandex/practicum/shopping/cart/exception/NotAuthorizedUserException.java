package ru.yandex.practicum.shopping.cart.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotAuthorizedUserException extends RuntimeException {

    private final String userMessage;
    private final HttpStatus httpStatus;

    public NotAuthorizedUserException(String message, String userMessage, HttpStatus httpStatus) {
        super(message);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }
}

