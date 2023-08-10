package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.other.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTestRest {
    private static final String HEADER_ITEM_FOR_USER_ID = "X-Sharer-User-Id";

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final List<ItemRequestDto> testItemRequestDto = List.of(
            ItemRequestDto.builder().id(1L).requesterId(1L).description("1").created(LocalDateTime.now()).items(List.of()).build(),
            ItemRequestDto.builder().id(2L).requesterId(2L).description("2").created(LocalDateTime.now()).items(List.of()).build(),
            ItemRequestDto.builder().id(3L).requesterId(3L).description("3").created(LocalDateTime.now()).items(List.of()).build()
    );

    @Test
    void createRequest_WhenAllOk() throws Exception {
        int userIndex = 0;

        when(itemRequestService.createRequest(any(), anyLong()))
                .thenReturn(testItemRequestDto.get(userIndex));

        mvc.perform(post("/requests")
                .header(HEADER_ITEM_FOR_USER_ID, 1L)
                .content(mapper.writeValueAsString(testItemRequestDto.get(userIndex)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.requesterId", is(testItemRequestDto.get(userIndex).getRequesterId()), Long.class))
                .andExpect(jsonPath("$.description", is(testItemRequestDto.get(userIndex).getDescription())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", empty()));
    }

    @Test
    void createRequest_WhenUserNotExist() throws Exception {
        int userIndex = 0;

        when(itemRequestService.createRequest(any(), anyLong()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(post("/requests")
                .header(HEADER_ITEM_FOR_USER_ID, 1L)
                .content(mapper.writeValueAsString(testItemRequestDto.get(userIndex)))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getUserRequests_WhenAllOk() throws Exception {
        when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(testItemRequestDto.subList(0, 1));

        mvc.perform(get("/requests").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testItemRequestDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(testItemRequestDto.get(0).getDescription())))
                .andExpect(jsonPath("$[0].requesterId", is(testItemRequestDto.get(0).getRequesterId()), Long.class))
                .andExpect(jsonPath("$[0].created", notNullValue()))
                .andExpect(jsonPath("$[0].items", empty()));
    }

    @Test
    void getUserRequests_WhenUserNotExist() throws Exception {
        when(itemRequestService.getUserRequests(anyLong()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(get("/requests").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getOtherRequests_WhenAllOk() throws Exception {
        when(itemRequestService.getOtherUserRequests(any(), any(), any()))
                .thenReturn(testItemRequestDto.subList(0, 1));

        mvc.perform(get("/requests/all").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testItemRequestDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(testItemRequestDto.get(0).getDescription())))
                .andExpect(jsonPath("$[0].requesterId", is(testItemRequestDto.get(0).getRequesterId()), Long.class))
                .andExpect(jsonPath("$[0].created", notNullValue()))
                .andExpect(jsonPath("$[0].items", empty()));
    }

    @Test
    void getOtherRequests_WhenUserNotExist() throws Exception {
        when(itemRequestService.getOtherUserRequests(any(), any(), any()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(get("/requests/all").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getItemRequest_WhenAllOk() throws Exception {
        int userIndex = 0;
        when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenReturn(testItemRequestDto.get(userIndex));

        mvc.perform(get("/requests/1").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testItemRequestDto.get(userIndex).getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testItemRequestDto.get(userIndex).getDescription())))
                .andExpect(jsonPath("$.requesterId", is(testItemRequestDto.get(userIndex).getRequesterId()), Long.class));
    }

    @Test
    void getItemRequest_WhenRequestNotExist() throws Exception {
        int userIndex = 0;
        when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenThrow(new ItemRequestNotFoundException());

        mvc.perform(get("/requests/1").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

    @Test
    void getItemRequest_WhenUserNotExist() throws Exception {
        int userIndex = 0;
        when(itemRequestService.getRequest(anyLong(), anyLong()))
                .thenThrow(new UserNotFoundException());

        mvc.perform(get("/requests/1").header(HEADER_ITEM_FOR_USER_ID, 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(404));
    }

}