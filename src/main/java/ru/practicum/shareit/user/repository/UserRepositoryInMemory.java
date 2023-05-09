package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.other.exception.ObjectNotFoundException;
import ru.practicum.shareit.other.exception.WrongInputDataException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserRepositoryInMemory implements UserRepository {

    private int lastId = 1;

    private final Map<Integer,UserDto> users = new HashMap<>();

    @Override
    public List<UserDto> getUsersDto() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDtoById(int userId) {
        UserDto userDto = users.get(userId);
        if (userDto != null) {
            return userDto;
        } else {
            throw new ObjectNotFoundException("UserDto");
        }
    }

    @Override
    public UserDto addUserDto(UserDto userDto) {
        if (checkUniEmail(userDto)) {
            userDto.setId(lastId++);
        }
        return saveUserDto(userDto);
    }

    @Override
    public UserDto removeUserDtoById(int userId) {
        UserDto userDto = users.get(userId);
        if (userDto != null) {
            users.remove(userId);
            return userDto;
        } else {
            throw new ObjectNotFoundException("UserDto");
        }
    }

    @Override
    public UserDto patchUserDto(UserDto userDto) {
        UserDto oldUserDto = users.get(userDto.getId());
        if (oldUserDto != null) {
            checkUniEmail(userDto);
            if (userDto.getName() != null) {
                oldUserDto.setName(userDto.getName());
            }
            if (userDto.getEmail() != null) {
                oldUserDto.setEmail(userDto.getEmail());
            }
            return saveUserDto(oldUserDto);
        } else {
            throw new ObjectNotFoundException("UserDto");
        }
    }

    @Override
    public UserDto saveUserDto(UserDto userDto) {
        if (users.containsKey(userDto.getId())) {
            users.remove(userDto.getId());
        }
        users.put(userDto.getId(),userDto);
        return userDto;
    }

    @Override
    public boolean checkUserExistsById(int userId) {
        return users.containsKey(userId);
    }

    private boolean checkUniEmail(UserDto userDto) {
        Long countTheSameEmail = users.values().stream()
                .filter(u -> (u.getId() != userDto.getId()) && (u.getEmail().equals(userDto.getEmail())))
                .count();
        if (countTheSameEmail > 0) {
            throw new WrongInputDataException("The same 'email' in 'User'");
        }
        return true;
    }

}
