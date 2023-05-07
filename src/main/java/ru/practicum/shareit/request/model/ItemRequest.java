package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Builder
public class ItemRequest {
    @Positive
    private int id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
