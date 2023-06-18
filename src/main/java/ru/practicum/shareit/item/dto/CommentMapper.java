package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.other.exception.ItemNotFoundException;
import ru.practicum.shareit.other.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Component
@AllArgsConstructor
public class CommentMapper {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
        return commentDto;
    }

    public Comment toComment(CommentDto commentDto) {
        Item item = itemRepository.findById(commentDto.getItemId()).orElseThrow(ItemNotFoundException::new);
        User user = userRepository.findById(commentDto.getAuthorId()).orElseThrow(UserNotFoundException::new);
        Comment comment = Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(item)
                .author(user)
                .created(commentDto.getCreated())
                .build();
        return comment;

    }
}