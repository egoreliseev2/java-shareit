package ru.practicum.shareit.exception;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String entity,long id) {
        super(entity + " with id=" + id + " not found");
    }
}
