package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTestIntegral {

    private final EntityManager em;

    private final UserService userService;

    private final List<String> uniKeys = List.of("user1", "user2", "user3", "user4", "user5");

    private List<UserDto> usersDtoIn;
    private List<User> usersIn;

    @BeforeEach
    void initUserDto() {
        em.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        usersDtoIn = uniKeys.stream()
                .map(k -> UserDto.builder().name(k).email(k + "@mail.com").build()).collect(Collectors.toList());
        usersIn = uniKeys.stream()
                .map(k -> User.builder().name(k).email(k + "@mail.com").build()).collect(Collectors.toList());
        for (User user : usersIn) {
            em.persist(user);
        }
        em.flush();
    }

    @Test
    void getAllUsersDto() {
        List<UserDto> usersDtoOut = userService.getAllUsersDto();
        for (UserDto userDto : usersDtoIn) {
            assertThat(usersDtoOut, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(userDto.getName())),
                    hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    @Test
    void getUserDtoById() {
        UserDto userDto = userService.getUserDtoById(2L);
        assertThat(userDto.getId(), equalTo(2L));
        assertThat(userDto.getName(), equalTo("user2"));
        assertThat(userDto.getEmail(), equalTo("user2@mail.com"));
    }

    @Test
    void addUserDto() {
        UserDto userDtoIn = UserDto.builder().name("kolya").email("kolya@mail.com").build();
        userService.addUserDto(userDtoIn);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userOut = query.setParameter("email", userDtoIn.getEmail()).getSingleResult();
        assertThat(userOut.getId(), notNullValue());
        assertThat(userOut.getName(), equalTo(userDtoIn.getName()));
        assertThat(userOut.getEmail(), equalTo(userDtoIn.getEmail()));
    }

    @Test
    void removeUserDtoById() {
        userService.removeUserDtoById(1L);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        List<User> usersOut = query.setParameter("id", 1L).getResultList();
        assertThat(usersOut, hasSize(0));
    }

    @Test
    void patchUserDto() {
        usersDtoIn.get(0).setEmail("user01@mail.com");
        usersDtoIn.get(0).setName("user01");
        userService.patchUserDto(usersDtoIn.get(0), 1L);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userOut = query.setParameter("id", 1L).getSingleResult();
        assertThat(userOut.getId(), equalTo(1L));
        assertThat(userOut.getName(), equalTo(usersDtoIn.get(0).getName()));
        assertThat(userOut.getEmail(), equalTo(usersDtoIn.get(0).getEmail()));
    }


}