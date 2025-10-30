package ru.yandex.practicum.interaction.api.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @SuppressWarnings("unused")
    @Around("@annotation(ru.yandex.practicum.interaction.api.logging.Loggable)")
    public Object logWithExecutionTimeMeasure(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.info("{}: Entering method: {}", className, joinPoint.getSignature());

        log.info("{}: Request Parameters: {}", className, Arrays.toString(joinPoint.getArgs()));

        Instant beforeExecute = Instant.now();
        Object result = joinPoint.proceed();
        Instant afterExecute = Instant.now();

        long executionTimeMs = Duration.between(beforeExecute, afterExecute).toMillis();

        log.info("{}: Exiting method: {}, Executed in: {} ms, Response: {}",
                className, joinPoint.getSignature(), executionTimeMs, result);

        return result;
    }
}