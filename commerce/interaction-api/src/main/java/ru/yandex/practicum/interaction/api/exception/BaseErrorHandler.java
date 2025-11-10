package ru.yandex.practicum.interaction.api.exception;

import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Component
@Slf4j
public class BaseErrorHandler {
    private final String className = this.getClass().getSimpleName();

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public NotAuthorizedUserException
    handleNotAuthorizedUser(NotAuthorizedUserException e) {
        //todo поменял с BAD REQUEST
        // если будут падать тесты прошлого ТЗ, дело в этом
        logError(e);
        return e;
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public NoProductsInShoppingCartException
    handleNoProductsInShoppingCart(NoProductsInShoppingCartException e) {
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

    @ExceptionHandler(StoreProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public StoreProductNotFoundException handleNotFound(StoreProductNotFoundException e) {
        logError(e);
        return e;
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoSpecifiedProductInWarehouseException
    handleNoSpecifiedProductInWarehouse(NoSpecifiedProductInWarehouseException e) {
        logError(e);
        return e;
    }


    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProductInShoppingCartLowQuantityInWarehouseException
    handleProductInShoppingCartLowQuantityInWarehouse(ProductInShoppingCartLowQuantityInWarehouseException e) {
        logError(e);
        return e;
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SpecifiedProductAlreadyInWarehouseException
    handleSpecifiedProductAlreadyInWarehouse(SpecifiedProductAlreadyInWarehouseException e) {
        logError(e);
        return e;
    }

    @ExceptionHandler(NoOrderFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoOrderFoundException
    handleNoOrderFound(NoOrderFoundException e) {
        logError(e);
        return e;
    }

    @ExceptionHandler(NoOrderBookingFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoOrderBookingFoundException
    handleNoOrderBookingFound(NoOrderBookingFoundException e) {
        logError(e);
        return e;
    }

    @ExceptionHandler(NoDeliveryFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoDeliveryFoundException
    handleNoDeliveryFound(NoDeliveryFoundException e) {
        logError(e);
        return e;
    }


    @ExceptionHandler(RetryableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String handleRetryable(RetryableException e) {
        logError(e);
        return e.getMessage();
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServer(InternalServerException e) {
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
        log.warn("{}: caught Exception with name: {}, and stackTrace: ",
                className, e.getClass().getSimpleName(), e);
    }
}