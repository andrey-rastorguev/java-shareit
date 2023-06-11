package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.other.exception.IllegalUpdateObjectException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentLightMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentDto createCommentForItem(CommentDto commentLightDto, long itemId, long userId) {
        commentLightDto.setAuthorId(userId);
        commentLightDto.setItemId(itemId);
        Comment comment = commentLightMapper.toComment(commentLightDto);
        if (!bookingRepository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(comment.getItem(), comment.getAuthor(), LocalDateTime.now())) {
            throw new IllegalUpdateObjectException("Недостаточно прав и условий для добавления комментария");
        }
        comment = commentRepository.save(comment);
        commentLightDto = commentLightMapper.toCommentDto(comment);
        return commentLightDto;
    }
}
