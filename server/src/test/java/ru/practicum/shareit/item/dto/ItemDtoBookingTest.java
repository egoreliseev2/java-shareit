package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoBookingTest {
    @Autowired
    private JacksonTester<ItemBookingDto> json;

    private final UserDto userDto = new UserDto(
            1L,
            "user",
            "user@user.ru");

    private final ItemDto itemDto = new ItemDto(
            1L,
            "name",
            "description",
            true,
            null);

    @Test
    void testUserDto() throws Exception {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user, null);
        ItemBookingDto itemDtoBooking = ItemMapper.toItemDtoBooking(item);

        JsonContent<ItemBookingDto> result = json.write(itemDtoBooking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEqualTo(Collections.emptyList());
    }
}