package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Builder
public class Booking {
    @Positive
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Item booker;
    private StatusBooking status;
}
