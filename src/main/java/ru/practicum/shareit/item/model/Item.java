package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Builder
@Setter
public class Item {
    @Positive
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private boolean available;
    private User owner;
    private ItemRequest request;
}
