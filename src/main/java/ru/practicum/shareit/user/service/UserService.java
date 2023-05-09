package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto getUserDtoById(int userId);
    UserDto addUserDto(UserDto userDto);
    UserDto removeUserDtoById(int userId);
    UserDto patchUserDto(UserDto userDto, int userId);

    List<UserDto> getAllUsersDto();
}
