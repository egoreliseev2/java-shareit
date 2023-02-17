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

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final BookingService bookingService;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public BookingResponseDto create(@RequestHeader(header) long id,
                                     @Validated(Create.class)
                                     @RequestBody BookingDto bookingDto) {
        return bookingService.create(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto changeStatus(@RequestHeader(header) long userId,
                                           @PathVariable long bookingId,
                                           @RequestParam boolean approved) {
        return bookingService.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getById(@RequestHeader(header) long userId,
                                      @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getByBooker(@RequestHeader(header) long userId,
                                                @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByOwner(@RequestHeader(header) long userId,
                                               @RequestParam(defaultValue = "ALL", required = false) String state) {
        return bookingService.getByOwner(userId, state);
    }
}
