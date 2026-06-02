package ru.village.exception;

/** Адрес нельзя удалить — по нему есть поступления. */
public class AddressInUseException extends RuntimeException {
    public AddressInUseException(String msg) {
        super(msg);
    }
}
