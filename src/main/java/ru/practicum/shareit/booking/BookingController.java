package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") long id, @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        return bookingService.create(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto changeStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long bookingId,
                                           @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getByBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingService.getByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state,
                                               @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                               @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingService.getByOwner(userId, state, from, size);
    }
}