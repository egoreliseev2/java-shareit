package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {
    private final ItemService itemService;
    private final String header = "X-Sharer-User-Id";
    private final String path = "/{itemId}";

    @GetMapping
    public List<ItemDto> findAll(@RequestHeader(header) long id) {
        return itemService.findAll(id);
    }

    @GetMapping(path)
    public ItemDto findItem(@PathVariable long itemId) {
        return itemService.findItem(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping
    public ItemDto create(@RequestHeader(header) long userId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.create(userId, itemDto);
    }

    @PatchMapping(path)
    public ItemDto update(@RequestHeader(header) long userId, @PathVariable long itemId, @RequestBody Item item) {
        return itemService.update(userId, itemId, item);
    }
}
