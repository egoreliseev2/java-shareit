package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Validated
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {

    private final ItemClient itemClient;
    private final String header = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestHeader(header) long id,
                                  @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                  @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return itemClient.findAll(id, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@RequestHeader(header) long userId, @PathVariable long itemId) {
        return itemClient.findItem(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(header) long userId, @Validated(Create.class) @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(header) long userId, @PathVariable long itemId, @RequestBody ItemDto itemDto) {
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(header) long userId,
                                             @RequestParam String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                             @Positive @RequestParam(defaultValue = "20", required = false) int size) {
        return itemClient.searchItem(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(header) long userId,
                                 @PathVariable long itemId,
                                 @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
