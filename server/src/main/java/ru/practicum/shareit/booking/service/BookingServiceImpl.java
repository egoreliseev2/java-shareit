package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
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
            throw new ObjectNotFoundException("item",bookerId);
        });
        User user = userRepository.findById(bookerId).orElseThrow(() -> {
            throw new ObjectNotFoundException("user",bookerId);
        });
        if (item.getOwner().getId() == bookerId) throw new ObjectNotFoundException("item",bookerId);
        if (!item.getAvailable()) throw new BadRequestException("Item not available now for booking");
        bookingDto.setStatus(BookingStatus.WAITING);
        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));
        BookingResponseDto bookingDtoResponse = BookingMapper.toBookingDtoResponse(booking);
        return bookingDtoResponse;
    }

    @Override
    @Transactional
    public BookingResponseDto changeStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Booking",bookingId);
        });
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) throw new ObjectNotFoundException("user",userId);
        if (booking.getStatus() == BookingStatus.APPROVED)
            throw new BadRequestException("You can't change status after approving");
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else booking.setStatus(BookingStatus.REJECTED);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingInfo(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new ObjectNotFoundException("Booking",bookingId);
        });
        Item item = booking.getItem();
        if (booking.getBooker().getId() == userId || item.getOwner().getId() == userId) {
            return BookingMapper.toBookingDtoResponse(booking);
        } else throw new ObjectNotFoundException("User",userId);
    }

    @Override
    public List<BookingResponseDto> getByBooker(long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("User",userId);
        });
        int page = from / size;
        PageRequest pg = PageRequest.of(page, size);
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pg));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByBookerCurrent(userId, LocalDateTime.now(), pg));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByBookerPast(userId, LocalDateTime.now(), pg));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByBookerFuture(userId, LocalDateTime.now(), pg));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByBookerAndStatus(userId, BookingStatus.WAITING, pg));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByBookerAndStatus(userId, BookingStatus.REJECTED, pg));
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return books.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getByOwner(long userId, String state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException("User",userId);
        });
        int page = from / size;
        PageRequest pg = PageRequest.of(page, size);
        List<Booking> books = new ArrayList<>();
        switch (state) {
            case "ALL":
                books.addAll(bookingRepository.findByItemOwnerIdOrderByStartDesc(userId, pg));
                break;
            case "CURRENT":
                books.addAll(bookingRepository.findByItemOwnerCurrent(userId, LocalDateTime.now(), pg));
                break;
            case "PAST":
                books.addAll(bookingRepository.findByItemOwnerPast(userId, LocalDateTime.now(), pg));
                break;
            case "FUTURE":
                books.addAll(bookingRepository.findByItemOwnerFuture(userId, LocalDateTime.now(), pg));
                break;
            case "WAITING":
                books.addAll(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.WAITING, pg));
                break;
            case "REJECTED":
                books.addAll(bookingRepository.findByItemOwnerAndStatus(userId, BookingStatus.REJECTED, pg));
                break;
            default:
                throw new UnsupportedStateException("Unknown state: " + state);
        }
        return books.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }
}