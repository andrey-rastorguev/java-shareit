package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.other.exception.AccessDeniedForUserException;
import ru.practicum.shareit.other.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;


@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto addItemDao(ItemDto itemDto, int userId) {
        if (!userRepository.checkUserExistsById(userId)) {
            throw new ObjectNotFoundException("User");
        }
        itemDto.setOwnerId(userId);
        return itemRepository.addItemDto(itemDto);
    }

    @Override
    public ItemDto patchItemDto(int userId, int itemId, ItemDto itemDto) {
        if (!userRepository.checkUserExistsById(userId)) {
            throw new ObjectNotFoundException("User");
        }
        ItemDto oldItemDto = itemRepository.getItemDtoById(itemId);
        if (oldItemDto.getOwnerId() != userId) {
            throw new AccessDeniedForUserException(userId);
        }

        return itemRepository.patchItemDto(itemDto, itemId);
    }

    @Override
    public ItemDto getItemDtoById(int itemId) {
        return itemRepository.getItemDtoById(itemId);
    }

    @Override
    public List<ItemDto> getItemsDtoByUserId(int userId) {
        return itemRepository.getItemsDtoByUserId(userId);
    }

    @Override
    public List<ItemDto> searchItemsDtoByText(String text) {
        return itemRepository.searchItemsDtoByText(text);
    }
}
