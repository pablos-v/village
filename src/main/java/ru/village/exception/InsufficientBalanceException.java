package ru.village.exception;

/** Расход превышает текущий остаток кассы. */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String msg) {
        super(msg);
    }
}
