package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.booking.other.StatusBooking;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Builder
public class BookingDto {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private int itemId;
    private int bookerId;
    private StatusBooking status;
}
