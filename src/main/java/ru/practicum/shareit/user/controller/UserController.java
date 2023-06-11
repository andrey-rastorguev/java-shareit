package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping
    public List<UserDto> getAllUsersDto() {
        return userService.getAllUsersDto();
    }

    @GetMapping("/{id}")
    public UserDto getUserDtoById(@PathVariable("id") long userId) {
        return userService.getUserDtoById(userId);
    }

    @PostMapping
    public UserDto addUserDto(@RequestBody @Valid final UserDto user) {
        return userService.addUserDto(user);
    }

    @DeleteMapping("/{id}")
    public void removeUserDtoById(@PathVariable("id") long userId) {
        userService.removeUserDtoById(userId);
    }

    @PatchMapping("/{id}")
    public UserDto patchUserDto(@RequestBody final UserDto user, @PathVariable("id") int userId) {
        return userService.patchUserDto(user, userId);
    }

}
