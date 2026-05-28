package ru.village.exception;

/** Сущность по ID не найдена. */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String msg) {
        super(msg);
    }
}
