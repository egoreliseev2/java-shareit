package ru.practicum.shareit.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("Пользователь с этой эл. почтой =" + email + " уже существует");
    }
}
