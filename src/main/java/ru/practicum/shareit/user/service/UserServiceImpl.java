package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(long id) {
        return userRepository.getById(id).orElseThrow(() -> new NotFoundException("user", id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        checkEmailDuplicates(userDto.getEmail());
        return userRepository.create(userDto);
    }

    @Override
    public User update(long id,User user) {
        if (user.getEmail() != null) {
            checkEmailDuplicates(user.getEmail());
        }
        return userRepository.update(id,user);
    }

    @Override
    public void delete(long id){
        userRepository.delete(id);
    }

    private void checkEmailDuplicates(String email){
        List<User> users = userRepository.findAll();
        boolean check = users.stream().anyMatch(repoUser -> repoUser.getEmail().equals(email));
        if(check){
            throw new DuplicateEmailException(email);
        }
    }
}
