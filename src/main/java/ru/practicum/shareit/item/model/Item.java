package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */

@Getter
@Builder
public class Item {
    @Positive
    private int id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;
}
