package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@Builder
public class User {
    @Positive
    private int id;
    private String name;
    @NotBlank
    @Email
    private String email;
}
