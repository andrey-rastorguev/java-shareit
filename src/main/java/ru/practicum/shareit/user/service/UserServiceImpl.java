package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsersDto() {
        return userRepository.getUsersDto();
    }

    @Override
    public UserDto getUserDtoById(int userId) {
        return userRepository.getUserDtoById(userId);
    }

    @Override
    public UserDto addUserDto(UserDto userDto) {
        return userRepository.addUserDto(userDto);
    }

    @Override
    public UserDto removeUserDtoById(int userId) {
        return userRepository.removeUserDtoById(userId);
    }

    @Override
    public UserDto patchUserDto(UserDto userDto, int userId) {
        userDto.setId(userId);
        return userRepository.patchUserDto(userDto);
    }
}
