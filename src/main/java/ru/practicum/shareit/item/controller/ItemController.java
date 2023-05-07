package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
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

    @PostMapping
    public ItemDto addItemDto(@RequestHeader(HEADER_ITEM_FOR_USER_ID) int userId,
                              @RequestBody @Valid ItemDto itemDto) {
        return itemService.addItemDao(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchItemDto(@RequestHeader(HEADER_ITEM_FOR_USER_ID) int userId,
                                @PathVariable("itemId") int itemId,
                                @RequestBody ItemDto itemDto) {
        return itemService.patchItemDto(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") int itemId) {
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader(HEADER_ITEM_FOR_USER_ID) int userId) {
        return itemService.getItemsDtoByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByUserId(@RequestParam("text") String text) {
        return itemService.searchItemsDtoByText(text);
    }
}
