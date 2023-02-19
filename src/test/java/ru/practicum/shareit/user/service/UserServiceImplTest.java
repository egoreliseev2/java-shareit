package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceImpl userService;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void getAllUsers_whenInvoked_thenReturnEmptyList() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());
        assertTrue(userService.getAllUsers().isEmpty());
    }

    @Test
    void getById_whenUserFound_thenReturnUser() {
        UserDto expectedUser = new UserDto();
        when(userRepository.findById(0L))
                .thenReturn(Optional.of(new User()));

        UserDto actualUser = userService.getById(0L);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getById_whenUserNotFound_thenObjectNotFoundThrown() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.getById(0L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void create_whenUserValid_thenUserSaved() {
        UserDto expectedUserDto = new UserDto(1L, "User", "test@test.ru");
        User user = new User(1L, "User", "test@test.ru");
        when(userRepository.save(any())).thenReturn(user);

        UserDto actualUserDto = userService.create(expectedUserDto);

        assertEquals(expectedUserDto, actualUserDto);
        verify(userRepository).save(any());
    }

    @Test
    void update_whenUserForUpdateValid_thenUserUpdated() {
        User oldUser = new User(1L, "User", "test@test.ru");
        UserDto userDtoForUpdate = new UserDto(1L, "Updated", "update@test.ru");
        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(UserMapper.toUser(userDtoForUpdate));

        UserDto actual = userService.update(1L, userDtoForUpdate);

        verify(userRepository).save(userArgumentCaptor.capture());
        User saved = userArgumentCaptor.getValue();

        assertEquals(actual.getId(), saved.getId());
        assertEquals(actual.getName(), saved.getName());
        assertEquals(actual.getEmail(), saved.getEmail());
    }

    @Test
    void update_whenUserForUpdate_thenObjectNotFoundThrown() {
        when(userRepository.findById(0L))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.update(0L, new UserDto()));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void delete_verifyInvokingMethod() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        userService.delete(1L);

        verify(userRepository).delete(any());
    }
}
