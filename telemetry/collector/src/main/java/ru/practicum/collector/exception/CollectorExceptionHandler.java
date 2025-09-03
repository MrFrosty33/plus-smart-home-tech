package ru.practicum.collector.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CollectorExceptionHandler {
    private final String className = this.getClass().getSimpleName();

    @ExceptionHandler(SerializationException.class)
    public ApiError handleSerializationException(SerializationException e) {
        writeLog(e);
        return ApiError.builder()
                .reason("Serialization error")
                .message(e.getMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(java.time.LocalDateTime.now())
                .build();
    }

    private void writeLog (RuntimeException e){
        log.error("{}: caught {} with message: {}", className, e.getClass().getSimpleName(), e.getMessage());
    }
}
