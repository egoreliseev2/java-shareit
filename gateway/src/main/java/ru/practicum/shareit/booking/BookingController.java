package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingController {

    private final BookingClient bookingClient;
    private final String header = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(header) long id, @Validated(Create.class) @RequestBody BookingDto bookingDto) {
        return bookingClient.create(id, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeStatus(@RequestHeader(header) long userId,
                                               @PathVariable long bookingId,
                                               @RequestParam boolean approved) {
        return bookingClient.changeStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getById(@RequestHeader(header) long userId,
                                          @PathVariable long bookingId) {
        return bookingClient.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader(header) long userId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                              @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingClient.getByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByOwner(@RequestHeader(header) long userId,
                                             @RequestParam(defaultValue = "ALL", required = false) String state,
                                             @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                             @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return bookingClient.getByOwner(userId, state, from, size);
    }
}
