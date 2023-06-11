package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.light.BookingLightDto;
import ru.practicum.shareit.booking.other.BookingRequestStates;

import java.util.List;


public interface BookingService {
    BookingDto createBooking(BookingLightDto bookingLightDto, long bookerId);

    BookingDto approveBooking(long bookingId, long userId, Boolean approved);

    BookingDto getBooking(long id, long userId);

    List<BookingDto> getBookingsForUserId(Long userId, BookingRequestStates state);

    List<BookingDto> getBookingsForItemOfUserId(Long userId, BookingRequestStates state);
}
