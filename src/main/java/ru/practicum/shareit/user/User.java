package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Builder
public class User {
    @Positive
    private int id;
    private String name;
    @Email
    private String email;
}
