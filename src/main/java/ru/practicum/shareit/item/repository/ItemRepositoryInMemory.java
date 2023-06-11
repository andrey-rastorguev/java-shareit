package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.other.exception.ObjectNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ItemRepositoryInMemory {

    private long lastId = 1;

    private final Map<Long, ItemDto> items = new HashMap<>();


    public ItemDto getItemDtoById(long itemDtoId) {
        ItemDto itemDto = items.get(itemDtoId);
        if (itemDto != null) {
            return itemDto;
        } else {
            throw new ObjectNotFoundException("ItemDto");
        }
    }

    public ItemDto addItemDto(ItemDto itemDto) {
        itemDto.setId(lastId++);
        return saveItemDto(itemDto);
    }

    public ItemDto patchItemDto(ItemDto newItemDto, int itemId) {
        ItemDto oldItemDto = getItemDtoById(itemId);
        if (newItemDto.getName() != null) {
            oldItemDto.setName(newItemDto.getName());
        }
        if (newItemDto.getDescription() != null) {
            oldItemDto.setDescription(newItemDto.getDescription());
        }
        if (newItemDto.getAvailable() != null) {
            oldItemDto.setAvailable(newItemDto.getAvailable());
        }
        return saveItemDto(oldItemDto);
    }

    public List<ItemDto> getItemsDtoByUserId(int userId) {
        return items.values().stream().filter(x -> x.getOwnerId() == userId).collect(Collectors.toList());
    }


    public List<ItemDto> searchItemsDtoByText(String text) {
        if (text.equals("")) {
            return new ArrayList<ItemDto>();
        }
        return items.values().stream()
                .filter(x -> x.getDescription().toLowerCase(Locale.ROOT).indexOf(text.toLowerCase(Locale.ROOT)) > -1 && x.getAvailable())
                .collect(Collectors.toList());
    }

    public ItemDto saveItemDto(ItemDto itemDto) {
        if (items.containsKey(itemDto.getId())) {
            items.remove(itemDto.getId());
        }
        items.put(itemDto.getId(), itemDto);
        return itemDto;
    }

}
