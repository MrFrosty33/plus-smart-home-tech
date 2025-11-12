package ru.yandex.practicum.payment.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.interaction.api.exception.BaseErrorHandler;

@RestControllerAdvice
public class ErrorHandler extends BaseErrorHandler {

}
