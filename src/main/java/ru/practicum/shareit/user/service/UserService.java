package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserDtoById(long userId);

    UserDto addUserDto(UserDto userDto);

    void removeUserDtoById(long userId);

    UserDto patchUserDto(UserDto userDto, long userId);

    List<UserDto> getAllUsersDto();
}
