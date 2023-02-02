package ru.practicum.shareit.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    @NotNull(groups = Create.class)
    @NotBlank(groups = Create.class)
    @Size(min = 5, max = 100)
    String text;
    String authorName;
    LocalDateTime created;
}
