package ru.yandex.practicum.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(ru.yandex.practicum.logging.Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Entering method: {}", joinPoint.getSignature());

        //todo может как-то красивее выводить стоит
        logger.info("Request Parameters: {}", Arrays.toString(joinPoint.getArgs()));

        Object result = joinPoint.proceed();

        logger.info("Exiting method: {} - Response: {}", joinPoint.getSignature(), result);

        return result;
    }
}