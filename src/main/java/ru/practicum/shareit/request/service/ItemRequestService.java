package ru.practicum.shareit.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;


import java.util.List;

@Transactional(readOnly = true)
public interface ItemRequestService {
    @Transactional
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getOtherUserRequests(Long userId, Integer from, Integer size);

    ItemRequestDto getRequest(Long requestId, Long userId);
}
