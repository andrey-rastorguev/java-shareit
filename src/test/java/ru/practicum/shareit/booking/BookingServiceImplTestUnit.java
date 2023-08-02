package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Map;

class BookingServiceImplTestUnit {
    private BookingService bookingService;

    private final Map<Long, UserDto> users = Map.of(
            1L, UserDto.builder().id(1L).name("user1").email("user1@mail.com").build(),
            2L, UserDto.builder().id(2L).name("user2").email("user2@mail.com").build(),
            3L, UserDto.builder().id(3L).name("user3").email("user3@mail.com").build()
    );

    private final Map<Long, ItemDto> items = Map.of(
            1L, ItemDto.builder().id(1L).name("item1").description("test item1").available(true).ownerId(1L).build(),
            2L, ItemDto.builder().id(2L).name("item2").description("test item2").available(true).ownerId(2L).build(),
            3L, ItemDto.builder().id(3L).name("item3").description("test item3").available(true).ownerId(2L).build(),
            4L, ItemDto.builder().id(4L).name("item4").description("test item4").available(true).ownerId(2L).build()
    );

    private final Map<Long, BookingDto> bookings = Map.of(
            1L, BookingDto.builder().id(1L)
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                    .booker(users.get(2L)).item(items.get(1L)).status(StatusBooking.WAITING).build(),
            2L, BookingDto.builder().id(2L)
                    .start(LocalDateTime.of(2023, 2, 1, 10, 0))
                    .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                    .booker(users.get(1L)).item(items.get(2L)).status(StatusBooking.WAITING).build(),
            3L, BookingDto.builder().id(3L)
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2023, 1, 2, 10, 0))
                    .booker(users.get(1L)).item(items.get(3L)).status(StatusBooking.CANCELED).build(),
            4L, BookingDto.builder().id(4L).start(LocalDateTime.of(2030, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2030, 1, 2, 10, 0))
                    .booker(users.get(1L)).item(items.get(4L)).status(StatusBooking.WAITING).build()
    );


}