package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {
    UserDto getUserDtoById(int userId);

    UserDto addUserDto(UserDto userDto);

    UserDto removeUserDtoById(int userId);

    UserDto patchUserDto(UserDto userDto);

    UserDto saveUserDto(UserDto userDto);

    List<UserDto> getUsersDto();

    boolean checkUserExistsById(int userId);
}
