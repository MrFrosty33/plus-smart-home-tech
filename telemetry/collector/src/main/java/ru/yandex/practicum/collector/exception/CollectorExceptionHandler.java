package ru.yandex.practicum.collector.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class CollectorExceptionHandler {
    private final String className = this.getClass().getSimpleName();

    @ExceptionHandler(JsonException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleJsonException(JsonException e) {
        writeLog(e);
        return ApiError.builder()
                .reason("Internal server error.")
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequest(HttpMessageNotReadableException e) {
        writeLog(e);
        return ApiError.builder()
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String message = String.format(
                "Failed to convert value of type '%s' to required type '%s'; %s",
                e.getValue() != null ? e.getValue().getClass().getSimpleName() : "(no value received)",
                e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "(no required type received)",
                e.getCause() != null ? e.getCause().getMessage() : "(no cause received)"
        );
        writeLog(e);

        return ApiError.builder()
                .reason("Incorrectly made request.")
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = "Validation error. ";

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            message = message.concat(error.getField() + ": " + error.getDefaultMessage() + ". ");
        }

        writeLog(e);
        return ApiError.builder()
                .reason("Incorrectly made request.")
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(SerializationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleSerializationException(SerializationException e) {
        writeLog(e);
        return ApiError.builder()
                .reason("Serialization error")
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(KafkaException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleKafkaException(KafkaException e) {
        writeLog(e);
        return ApiError.builder()
                .reason("Kafka error")
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(Exception e) {
        writeLog(new RuntimeException(e));
        return ApiError.builder()
                .reason("Internal server error")
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

    private void writeLog (Exception e){
        log.error("{}: caught {} with message: {}", className, e.getClass().getSimpleName(), e.getMessage());
    }
}
