package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
        return itemMapper.toDto(itemRepository.save(itemMapper.toEntity(itemDto)));
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
        return itemMapper.toDto(itemRepository.save(oldItem));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemDtoById(long itemId, long userId) {
        return itemMapper.toDto(itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsDtoByUserId(long userId, int from, int size) {
        User user = userRepository.getById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRepository.findByOwner(user,page).stream().map(x -> itemMapper.toDto(x,userId)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> searchItemsDtoByText(String text, int from, int size) {
        if (text != null && !text.equals("")) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
            return itemRepository.searchItemsDtoByText(text,page).stream().map(itemMapper::toDto).collect(Collectors.toList());
        } else {
            return new ArrayList<ItemDto>();
        }

    }
}
