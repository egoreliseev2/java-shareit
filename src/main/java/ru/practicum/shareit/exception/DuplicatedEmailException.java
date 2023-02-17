package ru.practicum.shareit.exception;

public class DuplicatedEmailException extends RuntimeException {
    public DuplicatedEmailException(String text) {
        super(text);
    }
}
