package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.other.exception.AccessDeniedForUserException;
import ru.practicum.shareit.other.exception.ItemNotFoundException;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemDto addItemDto(ItemDto itemDto, long userId) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        itemDto.setOwnerId(userId);
        return itemMapper.toItemDto(itemRepository.save(itemMapper.toItem(itemDto)));
    }

    @Override
    @Transactional
    public ItemDto patchItemDto(long userId, long itemId, ItemDto itemDto) {
        Item oldItem = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);

        if (oldItem.getOwner().getId() != userId) {
            throw new AccessDeniedForUserException(userId);
        }
        oldItem.setAvailable(itemDto.getAvailable() == null ? oldItem.isAvailable() : itemDto.getAvailable());
        oldItem.setDescription(itemDto.getDescription() == null ? oldItem.getDescription() : itemDto.getDescription());
        oldItem.setName(itemDto.getName() == null ? oldItem.getName() : itemDto.getName());
        return itemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemDtoById(long itemId, long userId) {
        return itemMapper.toItemDto(itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsDtoByUserId(long userId) {
        User user = userRepository.getById(userId);
        return itemRepository.findByOwner(user).stream().map(x -> itemMapper.toItemDto(x,userId)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItemsDtoByText(String text) {
        if (text != null && !text.equals("")) {
            return itemRepository.searchItemsDtoByText(text).stream().map(itemMapper::toItemDto).collect(Collectors.toList());
        } else {
            return new ArrayList<ItemDto>();
        }

    }
}
