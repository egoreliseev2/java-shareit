package ru.practicum.shareit.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String entity, long id) {
        super(entity + " с индификатором= " + id + " не найден");
    }
}