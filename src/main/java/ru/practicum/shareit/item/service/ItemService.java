package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItemDto(ItemDto itemDto, long userId);

    ItemDto patchItemDto(long userId, long itemId, ItemDto itemDto);

    ItemDto getItemDtoById(long itemId, long userId);

    List<ItemDto> getItemsDtoByUserId(long userId);

    List<ItemDto> searchItemsDtoByText(String text);
}
