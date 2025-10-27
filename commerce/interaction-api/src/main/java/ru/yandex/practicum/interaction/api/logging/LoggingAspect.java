package ru.yandex.practicum.interaction.api.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    //todo не работает, надо разбираться. Пакеты уже поправил, добавил уровень логгирования для этого модуля
    // всё одно не работает
    @SuppressWarnings("unused")
    @Around("@annotation(ru.yandex.practicum.interaction.api.logging.Loggable)")
    public Object logWithExecutionTimeMeasure(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("{}: Entering method: {}", className, joinPoint.getSignature());

        //todo может как-то красивее выводить стоит
        logger.info("{}: Request Parameters: {}", className, Arrays.toString(joinPoint.getArgs()));

        Instant beforeExecute = Instant.now();
        Object result = joinPoint.proceed();
        Instant afterExecute = Instant.now();

        long executionTimeMs = Duration.between(beforeExecute, afterExecute).toMillis();

        logger.info("{}: Exiting method: {}, Executed in: {} ms, Response: {}",
                className, joinPoint.getSignature(), executionTimeMs, result);

        return result;
    }
}