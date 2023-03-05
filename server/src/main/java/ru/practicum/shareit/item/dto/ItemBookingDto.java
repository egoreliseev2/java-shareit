package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemBookingDto {
    Long id;
    String name;
    String description;
    Boolean available;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDto> comments;
}
