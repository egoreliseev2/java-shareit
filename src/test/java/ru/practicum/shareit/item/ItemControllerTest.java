package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    private final ObjectMapper objectMapper;
    private final String header = "X-Sharer-User-Id";
    private final MockMvc mvc;
    @MockBean
    ItemService itemService;

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
    void findAllTest() throws Exception {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user, null);
        ItemBookingDto itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        when(itemService.findAll(anyLong(), any())).thenReturn(Collections.singletonList(itemDtoBooking));

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"));
    }

    @Test
    void findItemTest() throws Exception {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user, null);
        ItemBookingDto itemDtoBooking = ItemMapper.toItemDtoBooking(item);
        when(itemService.findItem(anyLong(), anyLong())).thenReturn(itemDtoBooking);

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void createTest() throws Exception {
        when(itemService.create(1L, itemDto)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    void updateTest() throws Exception {
        ItemDto forUpdate = new ItemDto(1L, "updated", "updated description", true, null);
        when(itemService.update(anyLong(), anyLong(), any())).thenReturn(forUpdate);

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(forUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.description").value("updated description"));
    }

    @Test
    void update_whenItemNotFound_thenObjectNotFoundThrown() throws Exception {
        ItemDto forUpdate = new ItemDto(1L, "updated", "updated description", true, null);
        when(itemService.update(anyLong(), anyLong(), any())).thenThrow(new NotFoundException("Item not found"));

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(forUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
                .andExpect(result -> assertEquals("Item not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void searchItemTest() throws Exception {
        when(itemService.searchItem(anyString(), any())).thenReturn(Collections.singletonList(itemDto));

        mvc.perform(get("/items/search?text=дрель")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"));
    }

    @Test
    void addCommentTest() throws Exception {
        User user = new User(
                1L,
                "name",
                "email@email.ru");
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Хорошая дрель");
        commentDto.setAuthorName(user.getName());
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(header, 1L)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Хорошая дрель"))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()));
    }
}