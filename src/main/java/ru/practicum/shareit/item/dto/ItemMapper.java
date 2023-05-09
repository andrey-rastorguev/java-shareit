package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface ItemMapper {
    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "available", source = "item.available")
    @Mapping(target = "requestId", expression = "java(item.getRequest() != null ? item.getRequest().getId() : null)")
    @Mapping(target = "ownerId", expression = "java(item.getOwner() != null ? item.getOwner().getId() : null)")
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "description", source = "itemDto.description")
    @Mapping(target = "available", source = "itemDto.available")
    @Mapping(target = "request", source = "itemRequest")
    @Mapping(target = "owner", source = "user")
    Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest);
}