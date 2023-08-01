package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsersDto() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserDtoById(long userId) {
        return userMapper.toDto(userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
    }

    @Override
    @Transactional
    public UserDto addUserDto(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public long removeUserDtoById(long userId) {
        userRepository.deleteById(userId);
        return userId;
    }

    @Override
    @Transactional
    public UserDto patchUserDto(UserDto userDto, long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setName(userDto.getName() == null ? user.getName() : userDto.getName());
        user.setEmail(userDto.getEmail() == null ? user.getEmail() : userDto.getEmail());
        return userMapper.toDto(userRepository.save(user));
    }
}
