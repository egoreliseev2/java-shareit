package ru.practicum.shareit.booking.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto create(long bookerId, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> {
            throw new NotFoundException("Item");
        });
        User user = userRepository.findById(bookerId).orElseThrow(() -> {
            throw new NotFoundException("user");
        });
        if (item.getOwner().getId() == bookerId) throw new NotFoundException("item");
        if (!item.getAvailable()) throw new BadRequestException("Item not available");
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new BadRequestException("Wrong time");
        }
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));
        BookingResponseDto bookingDtoResponse = BookingMapper.toBookingDtoResponse(booking);
        log.info("Item created");
        return bookingDtoResponse;
    }

    @Override
    @Transactional
    public BookingResponseDto changeStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking");
        });
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) throw new NotFoundException("This booking");
        if (booking.getStatus() == BookingStatus.APPROVED)
            throw new BadRequestException("You can't change status after it is approved");
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingInfo(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException("Booking");
        });
        Item item = booking.getItem();
        if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
            return BookingMapper.toBookingDtoResponse(booking);
        } else throw new NotFoundException("item");
    }

    @Override
    public List<BookingResponseDto> getByBooker(long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User");
        });
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findAllByBookerIdOrderByStartDesc(userId));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByBookerCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByBookerPast(userId, LocalDateTime.now()));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByBookerFuture(userId, LocalDateTime.now()));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByBookerAndStatus(userId, BookingStatus.WAITING));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByBookerAndStatus(userId, BookingStatus.REJECTED));
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return books.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getByOwner(long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User");
        });
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByItemOwnerCurrent(userId, LocalDateTime.now()));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByItemOwnerPast(userId, LocalDateTime.now()));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByItemOwnerFuture(userId, LocalDateTime.now()));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.WAITING));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.REJECTED));
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return books.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

}
