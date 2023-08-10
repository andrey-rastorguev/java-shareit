package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@RestController
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private static final String HEADER_ITEM_FOR_USER_ID = "X-Sharer-User-Id";

    private ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody final ItemRequestDto itemRequestDto,
                                        @RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId) {
        return itemRequestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherRequests(@RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        return itemRequestService.getOtherUserRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(@PathVariable final Long id,
                                         @RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId) {
        return itemRequestService.getRequest(id, userId);
    }
}
