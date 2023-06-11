package ru.practicum.shareit.booking.dto.light;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.booking.other.StatusBooking;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Builder
@Getter
@Setter
@ToString
public class BookingLightDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private StatusBooking status;
}
