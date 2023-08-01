package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.other.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    @Transactional
    public ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId) {
        itemRequestDto.setRequesterId(userId);
        ItemRequest itemRequest = itemRequestRepository.save(itemRequestMapper.toEntity(itemRequestDto));
        return itemRequestMapper.toDto(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return itemRequestRepository.findByRequester(user).stream().map(itemRequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getOtherUserRequests(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterNotOrderByIdDesc(user, page);
        return itemRequests.stream().map(itemRequestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequest(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(ItemRequestNotFoundException::new);
        return itemRequestMapper.toDto(request);
    }
}
