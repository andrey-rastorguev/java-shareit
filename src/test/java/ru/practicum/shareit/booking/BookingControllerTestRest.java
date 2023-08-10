package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.other.exception.BookingNotFoundException;
import ru.practicum.shareit.other.exception.ItemNotFoundException;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTestRest {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final String HEADER_ITEM_FOR_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final List<UserDto> testUsersDto = List.of(
            UserDto.builder().id(1L).name("user1").email("user1@test.ru").build(),
            UserDto.builder().id(2L).name("user2").email("user2@test.ru").build(),
            UserDto.builder().id(3L).name("user3").email("user3@test.ru").build()
    );

    private final List<ItemDto> testItemDto = List.of(
            ItemDto.builder().id(1L).name("item01").available(true).description("1").ownerId(1L).build(),
            ItemDto.builder().id(2L).name("item02").available(true).description("2").ownerId(2L).build(),
            ItemDto.builder().id(3L).name("item03").available(true).description("3").ownerId(3L).build()
    );

    private final List<BookingDto> bookings = List.of(
            BookingDto.builder().id(1L)
                    .start(LocalDateTime.of(2023, 8, 2, 1, 2, 3).plusMinutes(1))
                    .end(LocalDateTime.of(2023, 8, 2, 1, 2, 3).plusMinutes(10))
                    .item(testItemDto.get(0)).booker(testUsersDto.get(0)).status(StatusBooking.WAITING).build(),
            BookingDto.builder().id(2L)
                    .start(LocalDateTime.of(2023, 8, 2, 1, 2, 3).plusMinutes(100))
                    .end(LocalDateTime.of(2023, 8, 2, 1, 2, 3).plusMinutes(400))
                    .item(testItemDto.get(1)).booker(testUsersDto.get(0)).status(StatusBooking.APPROVED).build(),
            BookingDto.builder().id(3L)
                    .start(LocalDateTime.of(2023, 8, 2, 1, 2, 3).minusMinutes(10))
                    .end(LocalDateTime.of(2023, 8, 2, 1, 2, 3).plusMinutes(10))
                    .item(testItemDto.get(2)).booker(testUsersDto.get(2)).status(StatusBooking.REJECTED).build()
    );

    @Test
    void createBooking_WhenAllOk() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenReturn(bookings.get(0));

        mvc.perform(post("/bookings")
                .header(HEADER_ITEM_FOR_USER_ID, 2)
                .content(mapper.writeValueAsString(bookings.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.start", is(bookings.get(0).getStart().format(DATE_FORMAT))))
                .andExpect(jsonPath("$.end", is(bookings.get(0).getEnd().format(DATE_FORMAT))))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(testItemDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id", is(testUsersDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookings.get(0).getStatus().name())));
    }

    @Test
    void createBooking_WhenUserNotExist() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(post("/bookings")
                .header(HEADER_ITEM_FOR_USER_ID, 2)
                .content(mapper.writeValueAsString(bookings.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void createBooking_WhenItemNotExist() throws Exception {
        when(bookingService.createBooking(any(), anyLong()))
                .thenThrow(new ItemNotFoundException());

        mvc.perform(post("/bookings")
                .header(HEADER_ITEM_FOR_USER_ID, 2)
                .content(mapper.writeValueAsString(bookings.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void approveBooking_WhenAllOk() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookings.get(1));

        mvc.perform(patch("/bookings/1")
                .header(HEADER_ITEM_FOR_USER_ID, 2)
                .param("approved", "true"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class));
    }

    @Test
    void approveBooking_WhenBookingNotExist() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new BookingNotFoundException());

        mvc.perform(patch("/bookings/1")
                .header(HEADER_ITEM_FOR_USER_ID, 2)
                .param("approved", "true"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getBooking_WhenAllOk() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookings.get(0));

        mvc.perform(get("/bookings/1").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.start", is(bookings.get(0).getStart().format(DATE_FORMAT))))
                .andExpect(jsonPath("$.end", is(bookings.get(0).getEnd().format(DATE_FORMAT))))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(testItemDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id", is(testUsersDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookings.get(0).getStatus().name())));
    }

    @Test
    void getBooking_WhenBookingNotExist() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenThrow(new BookingNotFoundException());

        mvc.perform(get("/bookings/1").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getBookingsForUserId_whenAllOk() throws Exception {
        when(bookingService.getBookingsForUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].id", notNullValue(), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookings.get(1).getStart().format(DATE_FORMAT))))
                .andExpect(jsonPath("$[1].end", is(bookings.get(1).getEnd().format(DATE_FORMAT))))
                .andExpect(jsonPath("$[1].item", notNullValue()))
                .andExpect(jsonPath("$[1].item.id", is(testItemDto.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].booker", notNullValue()))
                .andExpect(jsonPath("$[1].booker.id", is(testUsersDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(bookings.get(1).getStatus().name())));
    }

    @Test
    void getBookingsForUserId_whenUserNotExist() throws Exception {
        when(bookingService.getBookingsForUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(get("/bookings").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getBookingsForItemOfUserId_WhenAllOk() throws Exception {
        when(bookingService.getBookingsForItemOfUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].id", notNullValue(), Long.class))
                .andExpect(jsonPath("$[2].start", is(bookings.get(2).getStart().format(DATE_FORMAT))))
                .andExpect(jsonPath("$[2].end", is(bookings.get(2).getEnd().format(DATE_FORMAT))))
                .andExpect(jsonPath("$[2].item", notNullValue()))
                .andExpect(jsonPath("$[2].item.id", is(testItemDto.get(2).getId()), Long.class))
                .andExpect(jsonPath("$[2].booker", notNullValue()))
                .andExpect(jsonPath("$[2].booker.id", is(testUsersDto.get(2).getId()), Long.class))
                .andExpect(jsonPath("$[2].status", is(bookings.get(2).getStatus().name())));
    }

    @Test
    void getBookingsForItemOfUserId_WhenUserNotExist() throws Exception {
        when(bookingService.getBookingsForItemOfUserId(anyLong(), any(), anyInt(), anyInt()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(get("/bookings/owner").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }
}