package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    Long id;
    @NotBlank(message = "Имя не должно быть пустым")
    String name;
    @Email(message = "Эл. почта не соответствует требованиям")
    @NotBlank(message = "Эл. почта не должно быть пустым")
    String email;
}
