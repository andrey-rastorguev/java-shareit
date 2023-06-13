package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private static final String HEADER_ITEM_FOR_USER_ID = "X-Sharer-User-Id";

    private final ItemService itemService;
    private final CommentService commentService;

    @PostMapping
    public ItemDto addItemDto(@RequestHeader(HEADER_ITEM_FOR_USER_ID) long userId,
                              @RequestBody @Valid ItemDto itemDto) {
        return itemService.addItemDto(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItemDto(@RequestHeader(HEADER_ITEM_FOR_USER_ID) long userId,
                                @PathVariable("itemId") long itemId,
                                @RequestBody final ItemDto itemDto) {
        return itemService.patchItemDto(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(HEADER_ITEM_FOR_USER_ID) long userId,
                               @PathVariable("itemId") long itemId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(HEADER_ITEM_FOR_USER_ID) long userId) {
        return itemService.getItemsDtoByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByUserId(@RequestParam("text") final String text) {
        return itemService.searchItemsDtoByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createCommentForItem(@RequestBody final CommentDto commentLightDto,
                                           @PathVariable final Long itemId,
                                           @RequestHeader(HEADER_ITEM_FOR_USER_ID) Long userId) {
        return commentService.createCommentForItem(commentLightDto, itemId, userId);
    }
}
