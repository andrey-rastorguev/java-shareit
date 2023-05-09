package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper {
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    UserDto toUserDto(User user);

    @Mapping(target = "id", source = "userDto.id")
    @Mapping(target = "name", source = "userDto.name")
    @Mapping(target = "email", source = "userDto.email")
    User toUser(UserDto userDto);
}
