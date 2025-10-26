package ru.yandex.practicum.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public NoSpecifiedProductInWarehouseException handleNotFound(NoSpecifiedProductInWarehouseException e) {
        // как-то странно.
        // В теле ответа ожидается столько всего, словно действительно нужно возвращать целиком исключение
        // со всеми унаследованными полями
        return e;
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProductInShoppingCartLowQuantityInWarehouseException handleNotFound(ProductInShoppingCartLowQuantityInWarehouseException e) {
        return e;
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SpecifiedProductAlreadyInWarehouseException handleNotFound(SpecifiedProductAlreadyInWarehouseException e) {
        return e;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return e.getMessage();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        return e.getMessage();
    }
}
