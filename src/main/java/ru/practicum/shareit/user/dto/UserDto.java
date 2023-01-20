package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    Long id;
    @NotBlank(message = "Имя не должно быть пустым")
    String name;
    @Email(message = "Эл. почта не соответствует требованиям")
    @NotBlank(message = "Эл. почта не должно быть пустым")
    String email;
}
