package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    UserService userService;

    private final UserDto userDto = new UserDto(
            1L,
            "user",
            "user@user.ru");

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDto));

        mvc.perform(get("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("user"))
                .andExpect(jsonPath("$[0].email").value("user@user.ru"));
    }

    @Test
    void getById_whenUserFound_thenReturnUser() throws Exception {
        when(userService.getById(1L)).thenReturn(userDto);

        mvc.perform(get("/users/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@user.ru"));
    }

    @Test
    void getById_whenUserNotFound_thenObjectNotFoundExceptionThrown() throws Exception {
        when(userService.getById(2L)).thenThrow(new ObjectNotFoundException("User not found"));

        mvc.perform(get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("User not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void createTest() throws Exception {
        when(userService.create(userDto)).thenReturn(userDto);

        mvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@user.ru"));
    }

    @Test
    void updateTest() throws Exception {
        UserDto forUpdate = new UserDto(1L, "updated", "updated@test.ru");
        when(userService.update(1L, forUpdate)).thenReturn(forUpdate);

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(forUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.email").value("updated@test.ru"));
    }

    @Test
    void update_whenUserNotFound_thenObjectNotFoundThrown() throws Exception {
        UserDto forUpdate = new UserDto(1L, "updated", "updated@test.ru");
        when(userService.update(1L, forUpdate)).thenThrow(new ObjectNotFoundException("User not found"));

        mvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(forUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("User not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void deleteTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}