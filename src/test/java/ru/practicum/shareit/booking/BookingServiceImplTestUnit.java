package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.light.BookingLightMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.other.BookingRequestStates;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.in;
import static org.mockito.ArgumentMatchers.any;

class BookingServiceImplTestUnit {

    @MockBean
    private BookingService bookingService;

    private final Map<Long, User> users = Map.of(
            1L, User.builder().id(1L).name("user1").email("user1@mail.com").build(),
            2L, User.builder().id(2L).name("user2").email("user2@mail.com").build(),
            3L, User.builder().id(3L).name("user3").email("user3@mail.com").build()
    );
    private final Map<Long, UserDto> usersDto = Map.of(
            1L, UserDto.builder().id(1L).name("user1").email("user1@mail.com").build(),
            2L, UserDto.builder().id(2L).name("user2").email("user2@mail.com").build(),
            3L, UserDto.builder().id(3L).name("user3").email("user3@mail.com").build()
    );

    private final Map<Long, Item> items = Map.of(
            1L, Item.builder().id(1L).name("item1").description("test item1").available(true).owner(users.get(1L)).build(),
            2L, Item.builder().id(2L).name("item2").description("test item2").available(true).owner(users.get(2L)).build(),
            3L, Item.builder().id(3L).name("item3").description("test item3").available(true).owner(users.get(2L)).build(),
            4L, Item.builder().id(4L).name("item4").description("test item4").available(true).owner(users.get(2L)).build()
    );

    private final Map<Long, ItemDto> itemsDto = Map.of(
            1L, ItemDto.builder().id(1L).name("item1").description("test item1").available(true).ownerId(1L).build(),
            2L, ItemDto.builder().id(2L).name("item2").description("test item2").available(true).ownerId(2L).build(),
            3L, ItemDto.builder().id(3L).name("item3").description("test item3").available(true).ownerId(2L).build(),
            4L, ItemDto.builder().id(4L).name("item4").description("test item4").available(true).ownerId(2L).build()
    );


    private final Map<Long, Booking> bookings = Map.of(
            1L, Booking.builder().id(1L)
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                    .booker(users.get(2L)).item(items.get(1L)).status(StatusBooking.WAITING).build(),
            2L, Booking.builder().id(2L)
                    .start(LocalDateTime.of(2023, 2, 1, 10, 0))
                    .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                    .booker(users.get(1L)).item(items.get(2L)).status(StatusBooking.WAITING).build(),
            3L, Booking.builder().id(3L)
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2023, 1, 2, 10, 0))
                    .booker(users.get(1L)).item(items.get(3L)).status(StatusBooking.CANCELED).build(),
            4L, Booking.builder().id(4L).start(LocalDateTime.of(2030, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2030, 1, 2, 10, 0))
                    .booker(users.get(1L)).item(items.get(4L)).status(StatusBooking.WAITING).build()
    );
    private final Map<Long, BookingDto> bookingsDto = Map.of(
            1L, BookingDto.builder().id(1L)
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                    .booker(usersDto.get(2L)).item(itemsDto.get(1L)).status(StatusBooking.WAITING).build(),
            2L, BookingDto.builder().id(2L)
                    .start(LocalDateTime.of(2023, 2, 1, 10, 0))
                    .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                    .booker(usersDto.get(1L)).item(itemsDto.get(2L)).status(StatusBooking.WAITING).build(),
            3L, BookingDto.builder().id(3L)
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2023, 1, 2, 10, 0))
                    .booker(usersDto.get(1L)).item(itemsDto.get(3L)).status(StatusBooking.CANCELED).build(),
            4L, BookingDto.builder().id(4L).start(LocalDateTime.of(2030, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2030, 1, 2, 10, 0))
                    .booker(usersDto.get(1L)).item(itemsDto.get(4L)).status(StatusBooking.WAITING).build()
    );


    @BeforeEach
    public void init() {

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        for (long i = 1L; i < 4L; i++) {
            Mockito.when(userRepository.findById(i))
                    .thenReturn(Optional.of(users.get(i)));
        }
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);

        Mockito.when(bookingRepository.findByBooker(any(User.class)))
                .thenAnswer(user -> bookings.values().stream()
                        .filter(booking -> booking.getBooker().equals(user.getArgument(0)))
                        .collect(Collectors.toList()));
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        BookingMapper bookingMapper = Mockito.mock(BookingMapper.class);
        for (long i = 1L; i < 5L; i++) {
            Mockito.when(bookingMapper.toDto(bookings.get(i)))
                    .thenReturn(bookingsDto.get(i));
        }
        BookingLightMapper bookingLightMapper = Mockito.mock(BookingLightMapper.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingLightMapper, bookingMapper);
    }

    @Test
    public void getBookingsForUserId() {
        List<BookingDto> bs = bookingService.getBookingsForUserId(3L, BookingRequestStates.ALL, 0, 10);
        assertThat(bs.size(), equalTo(0));

        bs = bookingService.getBookingsForUserId(2L, BookingRequestStates.ALL, 0, 10);
        assertThat(bs.size(), equalTo(1));
        assertThat(bs.get(0).getId(), equalTo(1L));

        bs = bookingService.getBookingsForUserId(1L, BookingRequestStates.ALL, 0, 10);
        assertThat(bs.size(), equalTo(3));
        assertThat(bs.get(0).getId(), in(List.of(2L, 3L, 4L)));
        assertThat(bs.get(1).getId(), in(List.of(2L, 3L, 4L)));
        assertThat(bs.get(2).getId(), in(List.of(2L, 3L, 4L)));
    }


}