package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> findAll(long userId) {
        return itemRepository.findAll(userId);
    }

    @Override
    public ItemDto findItem(long itemId) {
        return itemRepository.findItem(itemId).orElseThrow(() -> new NotFoundException("item", itemId));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItem(text);
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        userRepository.getById(userId).orElseThrow(() -> new NotFoundException("user", userId));
        return itemRepository.create(userId,itemDto);
    }

    @Override
    public ItemDto update(long userId, long itemId, Item item) {
        itemRepository.findItemForUpdate(userId,itemId).orElseThrow(() -> new NotFoundException("Item",itemId));
        return itemRepository.update(userId,itemId,item);
    }
}
