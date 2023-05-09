package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {
    ItemDto getItemDtoById(int itemId);

    ItemDto addItemDto(ItemDto itemDto);

    ItemDto patchItemDto(ItemDto newItemDto, int itemId);

    ItemDto saveItemDto(ItemDto itemDto);

    List<ItemDto> getItemsDtoByUserId(int userId);

    List<ItemDto> searchItemsDtoByText(String text);
}
