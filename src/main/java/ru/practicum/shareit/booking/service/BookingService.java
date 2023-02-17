package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(long id, BookingDto bookingDto);

    BookingResponseDto changeStatus(long userId, long bookingId, boolean approved);

    BookingResponseDto getBookingInfo(long userId, long bookingId);

    List<BookingResponseDto> getByBooker(long userId, String state, int from, int size);

    List<BookingResponseDto> getByOwner(long userId, String state, int from, int size);
}