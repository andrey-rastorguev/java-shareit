package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;


import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getOtherUserRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequest(Long requestId, Long userId);
}
