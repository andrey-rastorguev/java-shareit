package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Builder
public class ItemRequestDto {
    private int id;
    private String description;
    private int requestorId;
    private LocalDateTime created;
}
