package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository{
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public List<User> findAll(){
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(long id){
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public UserDto create(UserDto userDto){
        userDto.setId(id++);
        users.put(userDto.getId(), UserMapper.toUser(userDto));
        return userDto;
    }

    @Override
    public User update(long idOfUser,User user){
        if (users.containsKey(idOfUser)) {
            if (user.getName() != null) {
                users.get(idOfUser).setName(user.getName());
            }
            if (user.getEmail() != null) {
                users.get(idOfUser).setEmail(user.getEmail());
            }
            return users.get(idOfUser);
        }
        else {
            throw new NotFoundException("User",user.getId());
        }
    }

    @Override
    public void delete(long id){
        users.remove(id);
    }

}
