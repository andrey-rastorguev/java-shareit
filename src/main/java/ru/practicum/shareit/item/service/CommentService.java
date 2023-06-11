package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;

public interface CommentService {
    CommentDto createCommentForItem(CommentDto commentLightDto, long itemId, long userId);
}
