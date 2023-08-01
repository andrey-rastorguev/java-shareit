package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTestRest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final List<UserDto> testUsersDto = List.of(
            UserDto.builder().id(1L).name("user1").email("user1@test.ru").build(),
            UserDto.builder().id(2L).name("user2").email("user2@test.ru").build(),
            UserDto.builder().id(3L).name("user3").email("user3@test.ru").build()
    );

    @Test
    void getUsers_WhenAllOk() throws Exception {
        when(userService.getAllUsersDto())
                .thenReturn(testUsersDto);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(testUsersDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(testUsersDto.get(0).getName())))
                .andExpect(jsonPath("$[0].email", is(testUsersDto.get(0).getEmail())));
    }

    @Test
    void getUser_WhenUserExist() throws Exception {
        int userIndex = 0;
        when(userService.getUserDtoById(anyLong()))
                .thenReturn(testUsersDto.get(userIndex));

        mvc.perform(get("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUsersDto.get(userIndex).getId()), Long.class))
                .andExpect(jsonPath("$.name", is(testUsersDto.get(userIndex).getName())))
                .andExpect(jsonPath("$.email", is(testUsersDto.get(userIndex).getEmail())));

    }

    @Test
    void getUser_WhenUserNotExist() throws Exception {
        when(userService.getUserDtoById(anyLong()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(get("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void createUser_WhenUserIsRight() throws Exception {

        int userIndex = 0;

        when(userService.addUserDto(any()))
                .thenReturn(testUsersDto.get(userIndex));

        mvc.perform(post("/users")
                .content(mapper.writeValueAsString(testUsersDto.get(userIndex)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", is(testUsersDto.get(userIndex).getName())))
                .andExpect(jsonPath("$.email", is(testUsersDto.get(userIndex).getEmail())));

    }

    @Test
    void createUser_WhenUserIsWrongValidate() throws Exception {

        when(userService.addUserDto(any()))
                .thenThrow(new javax.validation.ConstraintViolationException("", null));

        mvc.perform(post("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void createUser_WhenUserIsWrongHibernateValidate() throws Exception {

        when(userService.addUserDto(any()))
                .thenThrow(new org.hibernate.exception.ConstraintViolationException("", new SQLException(""), ""));

        mvc.perform(post("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void patchUser_WhenUpdateUserIsRight() throws Exception {
        int userIndex = 0;
        when(userService.patchUserDto(any(), anyLong()))
                .thenReturn(testUsersDto.get(userIndex));


        mvc.perform(patch("/users/1")
                .content(mapper.writeValueAsString(testUsersDto.get(userIndex)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.email", notNullValue()));
    }

    @Test
    void patchUser_WhenUpdateUserIsWrongValidate() throws Exception {
        when(userService.patchUserDto(any(), anyLong()))
                .thenThrow(new javax.validation.ConstraintViolationException("", null));

        mvc.perform(post("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void patchUser_WhenUpdateUserIsWrongHibernateValidate() throws Exception {
        when(userService.patchUserDto(any(), anyLong()))
                .thenThrow(new org.hibernate.exception.ConstraintViolationException("", new SQLException(""), ""));

        mvc.perform(post("/users"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(400));
    }

    @Test
    void deleteUser_WhenUserIsExists() throws Exception {
        when(userService.removeUserDtoById(anyLong()))
                .thenReturn(anyLong());

        mvc.perform(delete("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_WhenUserIsNotExists() throws Exception {
        when(userService.removeUserDtoById(anyLong()))
                .thenThrow(new UserNotFoundException());
        mvc.perform(delete("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }
}
