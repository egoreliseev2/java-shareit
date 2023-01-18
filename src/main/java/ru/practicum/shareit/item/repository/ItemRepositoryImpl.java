package ru.practicum.shareit.item.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long id = 1;

    @Override
    public List<ItemDto> findAll(long userId) {
        List<Item> usersItems = items.getOrDefault(userId, Collections.emptyList());
        return usersItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDto> findItem(long itemId) {
        List<Item> items1 = new ArrayList<>();
        items.forEach((user, items2) -> items1.addAll(items2));
        return items1.stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .map(ItemMapper::toItemDto);
    }

    @Override
    public Optional<ItemDto> findItemForUpdate(long userId, long itemId) {
        return items.getOrDefault(userId, Collections.emptyList()).stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .map(ItemMapper::toItemDto);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> items1 = new ArrayList<>();
        items.forEach((userId, items2) -> items1.addAll(items.get(userId)));

        return items1.stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        itemDto.setId(id++);
        Item item = ItemMapper.toItem(itemDto, userId);
        items.compute(userId, (id, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, Item item) {
        Item repoItem = items.get(userId).stream()
                .filter(item1 -> item1.getId() == itemId)
                .findFirst()
                .get();
        if (item.getName() != null) {
            repoItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            repoItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null){
            repoItem.setAvailable(item.getAvailable());
        }
        items.get(userId).removeIf(item1 -> item1.getId() == itemId);
        items.get(userId).add(repoItem);
        return ItemMapper.toItemDto(repoItem);
    }

}
