package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    BookingService bookingService;

    User user = new User(
            1L,
            "name",
            "email@email.ru");
    Item item = new Item(
            1L,
            "name",
            "description",
            true,
            user,
            null);
    BookingResponseDto bdr = new BookingResponseDto(
            1L,
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().plusHours(3),
            item,
            user,
            BookingStatus.WAITING);
    BookingDto bookingDto = new BookingDto(
            null,
            LocalDateTime.now().plusHours(2),
            LocalDateTime.now().plusHours(3),
            1L,
            1L,
            BookingStatus.WAITING);

    @Test
    void createTest() throws Exception {
        when(bookingService.create(anyLong(), any())).thenReturn(bdr);

        mvc.perform(post("/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1L)
                    .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("name"))
                .andExpect(jsonPath("$.booker.name").value("name"));
    }

    @Test
    void changeStatusTest() throws Exception {
        bdr.setStatus(BookingStatus.APPROVED);
        when(bookingService.changeStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bdr);

        mvc.perform(patch("/bookings/1?approved=true")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getByIdTest() throws Exception {
        when(bookingService.getBookingInfo(anyLong(), anyLong())).thenReturn(bdr);

        mvc.perform(get("/bookings/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("name"))
                .andExpect(jsonPath("$.booker.name").value("name"));
    }

    @Test
    void getByBookerTest() throws Exception {
        when(bookingService.getByBooker(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(bdr));

        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("name"))
                .andExpect(jsonPath("$[0].booker.name").value("name"));
    }

    @Test
    void getByOwnerTest() throws Exception {
        when(bookingService.getByOwner(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(bdr));

        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("name"))
                .andExpect(jsonPath("$[0].booker.name").value("name"));
    }
}