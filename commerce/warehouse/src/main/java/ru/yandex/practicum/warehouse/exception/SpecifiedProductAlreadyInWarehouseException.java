package ru.yandex.practicum.warehouse.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class SpecifiedProductAlreadyInWarehouseException extends RuntimeException {

    private final String userMessage;
    private final HttpStatus httpStatus;

    public SpecifiedProductAlreadyInWarehouseException(String message, String userMessage, HttpStatus httpStatus) {
        super(message);
        this.userMessage = userMessage;
        this.httpStatus = httpStatus;
    }
}

