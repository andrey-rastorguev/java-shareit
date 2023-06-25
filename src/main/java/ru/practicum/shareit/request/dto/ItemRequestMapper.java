package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ItemRequestMapper {

    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemMapper itemMapper;

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requesterId(itemRequest.getRequester().getId())
                .items(itemRequest.getItems().stream().map(itemMapper::toDto).collect(Collectors.toList()))
                .build();

        return itemRequestDto;
    }

    public ItemRequest toEntity(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated() != null ? itemRequestDto.getCreated() : LocalDateTime.now())
                .requester(itemRequestDto.getRequesterId() != null ? userRepository.findById(itemRequestDto.getRequesterId()).orElseThrow(UserNotFoundException::new): null)
                .items(itemRequestDto.getItems() != null ? itemRequestDto.getItems().stream().map(itemMapper::toEntity).collect(Collectors.toList()) : new ArrayList<Item>())
                .build();
        return itemRequest;
    }
}
