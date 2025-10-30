package ru.yandex.practicum.interaction.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class NoProductsInShoppingCartException extends RuntimeException {
    private final String userMessage;
    private final HttpStatus httpStatus;

    public NoProductsInShoppingCartException(String message, String userMessage, HttpStatus httpStatus) {
        super(message);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }
}

