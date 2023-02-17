package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final CommentRepository commentRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public List<ItemBookingDto> findAll(long userId, Pageable p) {
        return setAllBookingsAndComments(userId, itemRepository.findAllByOwnerIdOrderByIdAsc(userId, p));
    }

    @Override
    public ItemBookingDto findItem(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item not found");
        });
        return setAllBookingsAndComments(userId, Collections.singletonList(item)).get(0);
    }

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("User not found");
        });
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Request not found"));
        }
        Item item = itemRepository.save(ItemMapper.toItem(itemDto, user, itemRequest));
        itemDto.setId(item.getId());
        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException("Item not found for update");
        });
        if (item.getOwner().getId() == userId) {
            if (itemDto.getName() != null) item.setName(itemDto.getName());
            if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
            if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
            itemRepository.save(item);
        } else {
            throw new NotFoundException("Item not found for update");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text, Pageable p) {
        if (text.isBlank()) return Collections.emptyList();
        return itemRepository.searchByText(text.toLowerCase(), p)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        bookingRepository.findByBookerIdAndItemIdAndEndBefore(userId, itemId, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("You can't make a comment to this item"));
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = CommentMapper.toComment(user, item, commentDto);
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }

    private List<ItemBookingDto> setAllBookingsAndComments(long userId, List<Item> items) {
        List<Long> ids = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findBookingsLast(ids, LocalDateTime.now(), userId);
        Map<Long, ItemBookingDto> itemsMap = items.stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(Collectors.toMap(ItemBookingDto::getId, item -> item, (a, b) -> b));
        bookings.forEach(booking -> itemsMap.get(booking.getItem().getId())
                .setLastBooking(BookingMapper.toBookingDto(booking)));
        bookings = bookingRepository.findBookingsNext(ids, LocalDateTime.now(), userId);
        bookings.forEach(booking -> itemsMap.get(booking.getItem().getId())
                .setNextBooking(BookingMapper.toBookingDto(booking)));
        List<Comment> comments = commentRepository.findAllComments(ids);
        comments.forEach(comment -> itemsMap.get(comment.getItem().getId())
                .getComments().add(CommentMapper.toCommentDto(comment)));
        return new ArrayList<>(itemsMap.values());
    }
}
