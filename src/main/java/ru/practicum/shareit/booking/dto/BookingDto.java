package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Builder
@Getter
@Setter
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private ItemDto item;
    private UserDto booker;
    private StatusBooking status;

    @Override
    public String toString() {
        return "BookingDto{" +
                "start=" + start +
                ", end=" + end +
                ", itemId=" + itemId +
                ", bookerId=" + booker.getId() +
                ", status=" + status +
                '}';
    }
}
