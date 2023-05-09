package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItemDao(ItemDto itemDto, int userId);

    ItemDto patchItemDto(int userId, int itemId, ItemDto itemDto);

    ItemDto getItemDtoById(int itemId);

    List<ItemDto> getItemsDtoByUserId(int userId);

    List<ItemDto> searchItemsDtoByText(String text);
}
