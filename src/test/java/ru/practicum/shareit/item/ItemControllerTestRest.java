package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.dto.light.BookingLightDto;
import ru.practicum.shareit.booking.other.StatusBooking;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.other.exception.ItemNotFoundException;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTestRest {
    private static final String HEADER_ITEM_FOR_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final List<UserDto> testUsersDto = List.of(
            UserDto.builder().id(1L).name("user1").email("user1@test.ru").build(),
            UserDto.builder().id(2L).name("user2").email("user2@test.ru").build(),
            UserDto.builder().id(3L).name("user3").email("user3@test.ru").build()
    );

    private final List<CommentDto> comments = List.of(
            CommentDto.builder().id(1L).authorId(1L).authorName("1").itemId(1L).text("123").created(LocalDateTime.now()).build(),
            CommentDto.builder().id(2L).authorId(2L).authorName("2").itemId(2L).text("123").created(LocalDateTime.now()).build(),
            CommentDto.builder().id(3L).authorId(3L).authorName("3").itemId(3L).text("123").created(LocalDateTime.now()).build()
    );

    private final List<ItemDto> testItemDto = List.of(
            ItemDto.builder().id(1L).name("item01").available(true).description("1").ownerId(1L).comments(comments).build(),
            ItemDto.builder().id(2L).name("item02").available(true).description("2").ownerId(2L).comments(new ArrayList<>()).build(),
            ItemDto.builder().id(3L).name("item03").available(true).description("3").ownerId(3L).comments(new ArrayList<>()).build()
    );

    private final List<BookingLightDto> bookings = List.of(
            BookingLightDto.builder().id(1L).start(LocalDateTime.now().plusMinutes(1)).end(LocalDateTime.now().plusMinutes(10))
                    .itemId(1L).bookerId(1L).status(StatusBooking.WAITING).build(),
            BookingLightDto.builder().id(2L).start(LocalDateTime.now().plusMinutes(100)).end(LocalDateTime.now().plusMinutes(400))
                    .itemId(2L).bookerId(1L).status(StatusBooking.APPROVED).build(),
            BookingLightDto.builder().id(3L).start(LocalDateTime.now().minusMinutes(10)).end(LocalDateTime.now().plusMinutes(10))
                    .itemId(3L).bookerId(3L).status(StatusBooking.REJECTED).build()
    );


    @Test
    void addItemDto_WhenAllOk() throws Exception {
        when(itemService.addItemDto(any(), anyLong()))
                .thenReturn(testItemDto.get(0));

        mvc.perform(post("/items")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .content(mapper.writeValueAsString(testItemDto.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.name", is(testItemDto.get(0).getName())))
                .andExpect(jsonPath("$.description", is(testItemDto.get(0).getDescription())))
                .andExpect(jsonPath("$.available", is(testItemDto.get(0).getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", notNullValue()))
                .andExpect(jsonPath("$.comments", hasSize(3)))
                .andExpect(jsonPath("$.requestId", nullValue()));

    }

    @Test
    void addItemDto_WhenUserNotExist() throws Exception {
        when(itemService.addItemDto(any(), anyLong()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(post("/items")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .content(mapper.writeValueAsString(testItemDto.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));

    }

    @Test
    void patchItemDto_WhenAllOk() throws Exception {
        when(itemService.patchItemDto(anyLong(), anyLong(), any()))
                .thenReturn(testItemDto.get(0));

        mvc.perform(patch("/items/1")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .content(mapper.writeValueAsString(testItemDto.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", is(testItemDto.get(0).getName())))
                .andExpect(jsonPath("$.description", is(testItemDto.get(0).getDescription())))
                .andExpect(jsonPath("$.available", is(testItemDto.get(0).getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", notNullValue()))
                .andExpect(jsonPath("$.comments", hasSize(3)))
                .andExpect(jsonPath("$.requestId", nullValue()));
    }


    @Test
    void patchItemDto_WhenUserNotExist() throws Exception {
        when(itemService.patchItemDto(anyLong(), anyLong(), any()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(patch("/items/1")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .content(mapper.writeValueAsString(testItemDto.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void patchItemDto_WhenItemNotExist() throws Exception {
        when(itemService.patchItemDto(anyLong(), anyLong(), any()))
                .thenThrow(new ItemNotFoundException());

        mvc.perform(patch("/items/1")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .content(mapper.writeValueAsString(testItemDto.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getItemById_WhenAllOk() throws Exception {
        when(itemService.getItemDtoById(1L, 1L))
                .thenReturn(testItemDto.get(0));

        mvc.perform(get("/items/1").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", is(testItemDto.get(0).getName())))
                .andExpect(jsonPath("$.description", is(testItemDto.get(0).getDescription())))
                .andExpect(jsonPath("$.available", is(testItemDto.get(0).getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", notNullValue()))
                .andExpect(jsonPath("$.comments", hasSize(3)))
                .andExpect(jsonPath("$.requestId", nullValue()));
    }

    @Test
    void getItemById_WhenUserNotExist() throws Exception {
        when(itemService.getItemDtoById(1L, 1L))
                .thenThrow(new UserNotFoundException());

        mvc.perform(get("/items/1").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getItemById_WhenItemNotExist() throws Exception {
        when(itemService.getItemDtoById(1L, 1L))
                .thenThrow(new ItemNotFoundException());

        mvc.perform(get("/items/1").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getItemsByUserId_WhenAllOk() throws Exception {
        when(itemService.getItemsDtoByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(testItemDto);

        testItemDto.get(0).setLastBooking(bookings.get(0));
        testItemDto.get(0).setNextBooking(bookings.get(1));

        mvc.perform(get("/items").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(testItemDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(testItemDto.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(testItemDto.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(testItemDto.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", notNullValue()))
                .andExpect(jsonPath("$[0].nextBooking", notNullValue()))
                .andExpect(jsonPath("$[0].nextBooking.id", is(testItemDto.get(0).getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments", notNullValue()))
                .andExpect(jsonPath("$[0].comments", hasSize(3)))
                .andExpect(jsonPath("$[0].comments[0].id", is(testItemDto.get(0).getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].requestId", nullValue()));
    }

    @Test
    void getItemsByUserId_WhenUserNotExist() throws Exception {
        when(itemService.getItemsDtoByUserId(anyLong(), anyInt(), anyInt()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(get("/items").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void searchItemsByText() throws Exception {
        when(itemService.searchItemsDtoByText(any(), anyInt(), anyInt()))
                .thenReturn(testItemDto);

        testItemDto.get(0).setLastBooking(bookings.get(0));
        testItemDto.get(0).setNextBooking(bookings.get(1));

        mvc.perform(get("/items/search")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .param("text", "123"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(testItemDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(testItemDto.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(testItemDto.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(testItemDto.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", notNullValue()))
                .andExpect(jsonPath("$[0].nextBooking", notNullValue()))
                .andExpect(jsonPath("$[0].nextBooking.id", is(testItemDto.get(0).getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].comments", notNullValue()))
                .andExpect(jsonPath("$[0].comments", hasSize(3)))
                .andExpect(jsonPath("$[0].comments[0].id", is(testItemDto.get(0).getComments().get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].requestId", nullValue()));
    }


    @Test
    void createCommentForItem_WhenAllOk() throws Exception {
        when(commentService.createCommentForItem(any(), anyLong(), anyLong()))
                .thenReturn(comments.get(0));

        mvc.perform(post("/items/1/comment")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .content(mapper.writeValueAsString(comments.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.text", is(comments.get(0).getText())))
                .andExpect(jsonPath("$.authorName", is(comments.get(0).getAuthorName())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.itemId", notNullValue()));
    }

    @Test
    void createCommentForItem_WhenUserNotExist() throws Exception {
        when(commentService.createCommentForItem(any(), anyLong(), anyLong()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(post("/items/1/comment")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .content(mapper.writeValueAsString(comments.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void createCommentForItem_WhenItemNotExist() throws Exception {
        when(commentService.createCommentForItem(any(), anyLong(), anyLong()))
                .thenThrow(new ItemNotFoundException());

        mvc.perform(post("/items/1/comment")
                .header(HEADER_ITEM_FOR_USER_ID, 1)
                .content(mapper.writeValueAsString(comments.get(0)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }
}